/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 * 
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2019, 2020 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package de.cau.cs.kieler.klighd.lsp

import com.google.common.base.Throwables
import com.google.inject.Inject
import com.google.inject.Provider
import de.cau.cs.kieler.klighd.IDiagramWorkbenchPart
import de.cau.cs.kieler.klighd.KlighdDataManager
import de.cau.cs.kieler.klighd.SynthesisOption
import de.cau.cs.kieler.klighd.ViewContext
import de.cau.cs.kieler.klighd.kgraph.KNode
import de.cau.cs.kieler.klighd.lsp.model.SKGraph
import de.cau.cs.kieler.klighd.util.KlighdSynthesisProperties
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.concurrent.CompletableFuture
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.sprotty.IDiagramServer
import org.eclipse.sprotty.SGraph
import org.eclipse.sprotty.xtext.ILanguageAwareDiagramServer
import org.eclipse.sprotty.xtext.ls.DiagramLanguageServer
import org.eclipse.sprotty.xtext.ls.DiagramUpdater
import org.eclipse.xtext.util.CancelIndicator

/**
 * Connection between {@link IDiagramServer} and the {@link DiagramLanguageServer}. With this singleton diagram updater,
 * any diagram server can update its diagrams via this diagramUpdater directly to the language server.
 * 
 * @author nre
 */
class KGraphDiagramUpdater extends DiagramUpdater {
    /**
     * The {@link Provider} to call an injected {@link KGraphDiagramGenerator} to generate {@link KNode KGraphs} and 
     * {@link SKGraph}s from that.
     */
    @Inject
    Provider<KGraphDiagramGenerator> diagramGeneratorProvider

    /**
     * The language server using this diagram updater. Double of the private languageServer field of the DiagramUpdater
     * class, with protected instead of private field accessibility here.
     */
    protected DiagramLanguageServer languageServer

    /**
     * Stores data for the generation of diagrams.
     */
    @Inject
    KGraphDiagramState diagramState
    
    /**
     * The handler for sending info, warnings, or errors to the user.
     */
    @Inject
    INotificationHandler notificationHandler

    override initialize(DiagramLanguageServer languageServer) {
        this.languageServer = languageServer
        super.initialize(languageServer)
    }

    /**
     * Updates the diagram without re-generating the underlying KGraph.
     * So this will use the stored KGraph saved for this diagramServer and create a new SGraph for that and call the
     * layout.
     * 
     * @param diagramServer The diagram server that should update its layout.
     */
    def updateLayout(KGraphDiagramServer diagramServer) {
        return CompletableFuture.completedFuture(doUpdateLayout(diagramServer))
    }

    /**
     * Updates the layout for the diagramServer, see {@see #updateLayout(KGraphDiagramServer)}.
     * Does this later as a completable future.
     * 
     * @param diagramServer The diagram server that should update its layout.
     */
    protected def CompletableFuture<Void> doUpdateLayout(KGraphDiagramServer diagramServer) {
        return (languageServer as KGraphLanguageServerExtension).doRead(diagramServer.sourceUri) [ resource, ci |
            // Just update the SGraph from the already existing KGraph.
            var ViewContext viewContext = null
            val uri = resource.URI.toString
            synchronized(diagramState) {
                viewContext = diagramState.getKGraphContext(uri)
            }
            
            return diagramServer -> createModel(viewContext, uri, ci)
        ].thenAccept [
            key.prepareUpdateModel(value)
        ].exceptionally [ throwable |
            notificationHandler.sendError(Throwables.getStackTraceAsString(throwable))
            return null
        ]
    }

    override protected doUpdateDiagrams(String uri, List<? extends ILanguageAwareDiagramServer> diagramServers) {
        if (diagramServers.empty) {
            return CompletableFuture.completedFuture(null)
        }
        return (languageServer as KGraphLanguageServerExtension).doRead(uri) [ resource, ci |
            var Object snapshotModel = null
            synchronized (diagramState) {
                snapshotModel = diagramState.getSnapshotModel(uri)
            }
            val model = if (snapshotModel === null) {
                    resource.contents.head
                } else {
                    snapshotModel
                }
            if (!(model instanceof EObject) || (model as EObject).eResource  === null ||
                (model as EObject).eResource.errors.isEmpty
            ) {
                (diagramServers as List<KGraphDiagramServer>).forEach [ server |
                    prepareModel(server, model, uri)
                    updateLayout(server)
                ]
            }
            return null as Void
        ]
    }

    /**
     * Prepares the DiagramState and the diagram server to generate an SGraph for the given model the next time the 
     * createModel is called.
     * 
     * @param server The diagramServer for that the diagram state should be updated.
     * @param model The new model that should be shown for the server.
     * @param uri The identifying URI to access the diagram state maps.
     */
    synchronized def void prepareModel(KGraphDiagramServer server, Object model, String uri) {

        val properties = new KlighdSynthesisProperties()
        var SprottyViewer viewer = null
        var String synthesisId
        synchronized (diagramState) {
            val iViewer = diagramState.getViewer()
            if (iViewer instanceof SprottyViewer) {
                viewer = iViewer
            }

            synthesisId = diagramState.getSynthesisId(uri)
        }

        // Set properties.
        if (synthesisId !== null) {
            // If the synthesisId is null, KLighD will use the a default synthesis defined for this model.
            properties.setProperty(KlighdSynthesisProperties.REQUESTED_DIAGRAM_SYNTHESIS, synthesisId)
        }

        // Save previous synthesis options to restore later.
        storeCurrentSynthesisOptions()
        // Configure options.
        var Map<SynthesisOption, Object> recentSynthesisOptions = null
        synchronized (diagramState) {
            recentSynthesisOptions = diagramState.recentSynthesisOptions
        }
        properties.configureSynthesisOptionValues(recentSynthesisOptions)

        // Indicated if the model type changed against the current model
        var modelTypeChanged = false
        var ViewContext viewContext = null

        if (viewer === null || viewer.viewContext === null) {
            // if viewer or context does not exist always init view
            modelTypeChanged = true
        } else {
            viewContext = viewer.viewContext
            if (viewContext.inputModel === null || viewContext.inputModel.class !== model.class) {
                modelTypeChanged = true
            }
            if (viewContext.getDiagramSynthesis() !== null
                && !KlighdDataManager.instance.getSynthesisID(viewContext.getDiagramSynthesis()).equals(synthesisId)) {
                // In case the synthesis changed the sidebar should be updated
                modelTypeChanged = true
            }
        }

        // If the type changed the view must be reinitialized to provide a correct ViewContext
        // otherwise the ViewContext can be simply updated.
        if (modelTypeChanged) {
            // Configure the ViewContext and the KlighD synthesis to generate the KGraph model correctly.
            properties.useViewer(SprottyViewer.ID)
            // needs to be a IDiagramWorkbenchPart, as it calls the standard constructor.
            viewContext = new ViewContext(null as IDiagramWorkbenchPart, model).configure(properties)
            viewer = viewContext.createViewer(null, null) as SprottyViewer
            viewer.diagramServer = server as KGraphDiagramServer
            viewer.viewContext = viewContext
        } else {
            viewContext.copyProperties(properties)
        }
        // Update the model and with that call the diagram synthesis.
        viewContext.update(model)

        synchronized (diagramState) {
            diagramState.putURIString(server.clientId, uri)
            diagramState.putKGraphContext(uri, viewContext)
            diagramState.putSynthesisId(uri, KlighdDataManager.instance.getSynthesisID(viewContext.diagramSynthesis))
            if (viewer !== null) {
                diagramState.setViewer(viewer)
            }
        }
    }

    /**
     * Generates an {@link SGraph} from the given {@link ViewContext} and the id under which it should be remembered in
     * the {@link KGraphDiagramState}.
     * 
     * @param viewContext The viewContext containing the {@link KNode KGraph} view model that should be translated to
     * an {@link SGraph} model.
     * @param uri The identifying URI of the source model used in the SGraph model generation and that is used to store
     * diagram generation relevant data in the {@link KGraphDiagramState}.
     * @param cancelIndicator The {@link CancelIndicator} used to tell the diagram translation to stop.
     * @return The generated SGraph
     */
    synchronized def SGraph createModel(ViewContext viewContext, String uri, CancelIndicator cancelIndicator) {
        // Generate the SGraph model from the KGraph model and store every later relevant part in the
        // diagram state.
        val diagramGenerator = diagramGeneratorProvider.get
        var shouldSelectText = false
        if (languageServer instanceof KGraphLanguageServerExtension) {
            shouldSelectText = languageServer.shouldSelectText
        }
        diagramGenerator.activeTracing = shouldSelectText
        val sGraph = diagramGenerator.toSGraph(viewContext.viewModel, uri, cancelIndicator)
        synchronized (diagramState) {
            diagramState.putKGraphToSModelElementMap(uri, diagramGenerator.getKGraphToSModelElementMap)
            diagramState.putIdToKGraphElementMap(uri, diagramGenerator.idToKGraphElementMap)
            diagramState.putTexts(uri, diagramGenerator.getModelLabels)
            diagramState.putTextMapping(uri, diagramGenerator.getTextMapping)
            diagramState.putImages(uri, diagramGenerator.images)
        }

        return sGraph
    }

    /**
     * Stores the current synthesisOptions configured in the current {@link ViewContext}.
     * Similar to storing the options in Eclipse UI.
     * 
     * @see de.cau.cs.kieler.klighd.ui.view.DiagramView#storeCurrentSynthesisOptions
     */
    def storeCurrentSynthesisOptions() {
        synchronized (diagramState) {
            val viewer = diagramState.viewer
            if (viewer !== null && viewer.viewContext !== null) {
                val viewContext = viewer.viewContext
                val allUsedSynthesisOptions = new HashSet<SynthesisOption>
                val usedRootSynthesis = viewContext.diagramSynthesis

                // Save used syntheses.
                diagramState.addUsedSynthesis(usedRootSynthesis)

                // Find all available synthesis options for the currently used syntheses.
                if (usedRootSynthesis !== null) {
                    allUsedSynthesisOptions.addAll(usedRootSynthesis.displayedSynthesisOptions)
                }
                for (childVC : viewContext.getChildViewContexts(true)) {
                    diagramState.addUsedSynthesis(childVC.diagramSynthesis)
                    allUsedSynthesisOptions.addAll(childVC.diagramSynthesis.displayedSynthesisOptions)
                }

                // Save used options.
                for (option : allUsedSynthesisOptions) {
                    diagramState.addRecentSynthesisOption(option, viewContext.getOptionValue(option))
                }
            }
        }
    }

    /**
     * Makes the protected updateDiagram(List<URI>) method accessible from the outside.
     */
    def updateDiagrams2(List<URI> uris) {
        updateDiagrams(uris)
    }
}

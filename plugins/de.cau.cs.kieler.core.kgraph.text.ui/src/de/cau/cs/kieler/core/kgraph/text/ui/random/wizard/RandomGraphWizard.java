/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2011 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.core.kgraph.text.ui.random.wizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.text.ui.internal.KGraphActivator;
import de.cau.cs.kieler.core.kgraph.text.ui.random.GeneratorOptions;
import de.cau.cs.kieler.core.kgraph.text.ui.random.RandomGraphGenerator;
import de.cau.cs.kieler.core.util.Maybe;

/**
 * The new-wizard for creating random KGraphs.
 * 
 * @author mri
 * @author msp
 */
public class RandomGraphWizard extends Wizard implements INewWizard {

    /** the soft-limit for the number of generated graphs. */
    private static final int SOFT_LIMIT_GRAPHS = 10000;

    /** the generation options passed through the wizard pages to the generator. */
    private GeneratorOptions options = new GeneratorOptions();
    /** the selection this wizard is invoked on. */
    private IStructuredSelection selection;
    
    /** the new file page. */
    private RandomGraphNewFilePage newFilePage;
    /** the graph type page. */
    private RandomGraphTypePage typePage;
    /** the page for the ANY graph type. */
    private RandomGraphAnyPage anyPage;
    /** the page for the TREE graph type. */
    private RandomGraphTreePage treePage;
    /** the page for the BICONNECTED graph type. */
    private RandomGraphBiconnectedPage biconnectedPage;
    /** the page for the TRICONNECTED graph type. */
    private RandomGraphTriconnectedPage triconnectedPage;
    /** the page for the ACYCLIC_NO_TRANSITIV_EDGES graph type. */
    private RandomGraphANTEPage antePage;
    /** the options page. */
    private RandomGraphOptionsPage optionsPage;

    /**
     * Creates a RandomGraphWizard.
     */
    public RandomGraphWizard() {
        super();
        setNeedsProgressMonitor(true);
        setWindowTitle(Messages.RandomGraphWizard_title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPages() {
        // load the generation options from the preferences
        options.loadPreferences();
        
        newFilePage = new RandomGraphNewFilePage(selection, options);
        typePage = new RandomGraphTypePage(options);
        anyPage = new RandomGraphAnyPage(options);
        treePage = new RandomGraphTreePage(options);
        biconnectedPage = new RandomGraphBiconnectedPage(options);
        triconnectedPage = new RandomGraphTriconnectedPage(options);
        antePage = new RandomGraphANTEPage(options);
        optionsPage = new RandomGraphOptionsPage(options);
        addPage(newFilePage);
        addPage(typePage);
        addPage(anyPage);
        addPage(treePage);
        addPage(biconnectedPage);
        addPage(triconnectedPage);
        addPage(antePage);
        addPage(optionsPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IWizardPage getNextPage(final IWizardPage page) {
        if (page == newFilePage) {
            return typePage;
        } else if (page == typePage) {
            switch (options.getProperty(GeneratorOptions.GRAPH_TYPE)) {
            case TREE:
                return treePage;
            case BICONNECTED:
                return biconnectedPage;
            case TRICONNECTED:
                return triconnectedPage;
            case ACYCLIC_NO_TRANSITIVE_EDGES:
                return antePage;
            case ANY:
            default:
                return anyPage;
            }
        } else if (page == treePage
                || page == biconnectedPage
                || page == triconnectedPage
                || page == antePage
                || page == anyPage) {
            
            return optionsPage;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performFinish() {
        // save all generation options into the plugin preferences
        options.savePreferences();
        
        // if necessary ask the user to verify his decisions on the number of generated graphs
        if (options.getProperty(GeneratorOptions.NUMBER_OF_GRAPHS) > SOFT_LIMIT_GRAPHS) {
            if (!askUser(Messages.RandomGraphWizard_soft_limit_graphs_message)) {
                getContainer().showPage(newFilePage);
                return false;
            }
        }
        
        // run the generation in the wizard container
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            public void run(final IProgressMonitor monitor) throws InterruptedException,
                    InvocationTargetException {
                try {
                    doFinish(monitor);
                } catch (InterruptedException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new InvocationTargetException(e);
                }
            }
        };
        
        try {
            getContainer().run(true, true, runnable);
        } catch (InterruptedException exception) {
            return false;
        } catch (InvocationTargetException exception) {
            IStatus status =
                    new Status(IStatus.ERROR, KGraphActivator.DE_CAU_CS_KIELER_CORE_KGRAPH_TEXT_KGRAPH,
                            Messages.RandomGraphWizard_graph_generated_failed_error,
                            exception.getCause());
            StatusManager.getManager().handle(status, StatusManager.BLOCK | StatusManager.SHOW);
        }

        return true;
    }

    /**
     * Display a message box with a yes / no question.
     * 
     * @param question the text displayed in the message box
     * @return true if the user selected "yes"
     */
    private boolean askUser(final String question) {
        MessageBox messageBox =
                new MessageBox(getContainer().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messageBox.setMessage(question);
        messageBox.setText(Messages.RandomGraphWizard_revise_settings_title);
        int response = messageBox.open();
        return response == SWT.YES;
    }

    /**
     * Performs the actual generation and serialization.
     * 
     * @param monitor
     *            the progress monitor
     * @throws IOException
     *             when serializing a graph failed
     * @throws CoreException
     *             when refreshing the resource hierarchy failed
     * @throws InterruptedException
     *             when the user cancels the operation
     */
    private void doFinish(final IProgressMonitor monitor) throws IOException, CoreException,
            InterruptedException {
        int numberOfGraphs = options.getProperty(GeneratorOptions.NUMBER_OF_GRAPHS);
        monitor.beginTask(Messages.RandomGraphWizard_generating_graphs_task, numberOfGraphs);
        
        // create a random graph generator
        Random random;
        if (options.getProperty(GeneratorOptions.TIME_BASED_RANDOMIZATION)) {
            random = new Random();
        } else {
            random = new Random(options.getProperty(GeneratorOptions.RANDOMIZATION_SEED));
        }
        RandomGraphGenerator generator = new RandomGraphGenerator(random);
        
        // do the generation
        try {
            if (numberOfGraphs == 1) {
                final Maybe<IFile> file = new Maybe<IFile>();
                PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                    public void run() {
                        file.set(newFilePage.createNewFile());
                    }
                });
                // generate and serialize the graph
                try {
                    KNode graph = generator.generate(options);
                    serialize(graph, file.get());
                    monitor.worked(1);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            } else {
                // prepare to build filenames
                final Maybe<String> name = new Maybe<String>();
                final Maybe<String> ext = new Maybe<String>();
                final Maybe<IPath> containerPath = new Maybe<IPath>();
                PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                    public void run() {
                        name.set(newFilePage.getFileName());
                        ext.set(newFilePage.getFileExtension());
                        containerPath.set(newFilePage.getContainerFullPath());
                    }
                });
                String nameWithoutExt = 
                    name.get().substring(0, name.get().lastIndexOf(".")); //$NON-NLS-1$
                // generate the desired number of graphs
                int graphNumber = 1;
                int decimalPlaces = (int) Math.log10(numberOfGraphs) + 1;
                for (int i = 0; i < numberOfGraphs; i++) {
                    if (monitor.isCanceled()) {
                        throw new InterruptedException();
                    }
                    
                    // construct the file path
                    IFile file;
                    do {
                        int p = (int) Math.log10(graphNumber) + 1;
                        String fileName = nameWithoutExt + generateZeros(decimalPlaces - p)
                                + graphNumber + "." + ext.get();
                        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                        IPath path = containerPath.get().append(new Path(fileName));
                        file = workspaceRoot.getFile(path);
                        graphNumber++;
                    } while (file.exists());
                    
                    // generate and serialize the graph
                    try {
                        KNode graph = generator.generate(options);
                        serialize(graph, file);
                        monitor.worked(1);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        } finally {
            monitor.done();
        }
    }
    
    /**
     * Generate a string with n zeros.
     * 
     * @param n the number of zeros
     * @return a string with n zeros
     */
    private static String generateZeros(final int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append('0');
        }
        return result.toString();
    }

    /**
     * Generate a random graph and serialize it to the given file.
     * 
     * @param graph the graph to serialize
     * @param file target file for serialization
     * @throws IOException if the file cannot be written
     * @throws CoreException if the file cannot be refreshed
     */
    private void serialize(final KNode graph, final IFile file)
            throws IOException, CoreException {
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createURI(file.getLocationURI().toString()));
        resource.getContents().add(graph);
        resource.save(Collections.EMPTY_MAP);
        file.refreshLocal(1, null);
    }

    /**
     * {@inheritDoc}
     */
    public void init(final IWorkbench workbench, final IStructuredSelection theselection) {
        this.selection = theselection;
    }
    
}

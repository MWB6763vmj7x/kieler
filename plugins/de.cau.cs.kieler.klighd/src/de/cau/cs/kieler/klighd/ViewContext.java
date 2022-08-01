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
package de.cau.cs.kieler.klighd;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.statushandlers.StatusManager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.MapPropertyHolder;
import de.cau.cs.kieler.core.properties.Property;
import de.cau.cs.kieler.core.util.RunnableWithResult;

/**
 * A view context contains a viewer provider and a model that is accepted by the viewer provider.
 * 
 * @author mri
 */
public final class ViewContext extends MapPropertyHolder {

    /** the serial version UID. */
    private static final long serialVersionUID = -431994394109554393L;

    /** A pre-defined property to be used for handing over an {@link RunnableWithResult} to the
     * KLighD view allowing to update the represented model. */
    public static final IProperty<RunnableWithResult<?>> MODEL_ACCESS =
            new Property<RunnableWithResult<?>>("modelAccess"); 


    /** the part the source model was selected from (if can reasonably be determined). */
    private transient IWorkbenchPart sourceWorkbenchPart = null;
    
    /** the viewer provider. */
    private transient IViewerProvider<?> viewerProvider = null;
    /** the update strategy. */
    private transient IUpdateStrategy<?> updateStrategy = null;
    /** the list of transformation contexts in this view context. */
    private transient List<TransformationContext<?, ?>> transformationContexts = null;
    /** the reversed list of transformation contexts. */
    private transient List<TransformationContext<?, ?>> transformationContextsRev = null;
    /** the business model to be represented by means of this context. */
    private Object businessModel = null;
    /** the view model initiated while configuring an {@link IUpdateStrategy}
     * and kept for the whole life-cycle of the view context,
     * in order to enable proper incremental update. */
    private Object viewModel = null;
    
    /**
     * Default constructor.
     */
    public ViewContext() {
        super();
        this.transformationContexts = Lists.newLinkedList();
        this.transformationContextsRev = Lists.reverse(transformationContexts);
    }
    
    /**
     * Sets the source workbench part (part the source model has been chosen in).
     * 
     * @param theSourceWorkbenchPart
     *            the source workbench part (part the source model has been chosen in)
     */
    public void setSourceWorkbenchPart(final IWorkbenchPart theSourceWorkbenchPart) {
        this.sourceWorkbenchPart = theSourceWorkbenchPart;
    }

    /**
     * Returns the source workbench part.
     * 
     * @return the source workbench part (part the source model has been chosen in)
     */
    public IWorkbenchPart getSourceWorkbenchPart() {
        return sourceWorkbenchPart;
    }

    /**
     * Returns the source workbench part viewer (experimental).
     * 
     * @return the source workbench part viewer(viewer the source model has been chosen in)
     */
    public Viewer getSourceWorkbenchPartViewer() {
        if (this.sourceWorkbenchPart == null) {
            return null;
        }
        try {
            IWorkbenchPart part = this.sourceWorkbenchPart;
            return (Viewer) part.getClass().getMethod("getViewer").invoke(part);
        } catch (Exception e) {
            StatusManager.getManager().addLoggedStatus(
                    new Status(IStatus.ERROR, KlighdPlugin.PLUGIN_ID,
                            "KLighD: Determination of a viewer widget (beyond the KLighD viewer) "
                                    + "showing the currently depicted model failed.\n"
                                    + "This error might occured while trying to focus a depicted model "
                                    + "element in a related editor."));
        }
        return null;               
    }
    
    /**
     * Sets the input model.
     * 
     * @param model
     *            the input model
     */    
    protected void setInputModel(final Object model) {
        this.businessModel = model; 
    }

    /**
     * Returns the current model to be represented.
     * 
     * @return the current model to be represented.
     */
    public Object getInputModel() {
        RunnableWithResult<?> modelAccess = this.getProperty(MODEL_ACCESS);
        if (modelAccess != null) {
            modelAccess.run();
            return modelAccess.getResult();
        }
        return this.businessModel;
    }

    /**
     * Sets the contexts viewer provider.
     * 
     * @param viewerProvider
     *            the viewer provider
     */
    protected void setViewerProvider(final IViewerProvider<?> viewerProvider) {
        this.viewerProvider = viewerProvider;
    }

    /**
     * Returns the contexts viewer provider.
     * 
     * @return the viewer provider
     */
    public IViewerProvider<?> getViewerProvider() {
        return viewerProvider;
    }

    /**
     * Sets the update strategy used in this view context.
     * In addition, it configures the view model root, which
     * is kept for the whole life-cycle of the view context,
     * in order to enable proper incremental update. 
     * 
     * @param updateStrategy
     *            the update strategy
     */
    public void setUpdateStrategy(final IUpdateStrategy<?> updateStrategy) {
        this.updateStrategy = updateStrategy;
        if (updateStrategy != null) {
            viewModel = updateStrategy.getInitialBaseModel(this);
        } else {
            viewModel = null;
        }
    }

    /**
     * Returns the update strategy used in this view context.
     * 
     * @return the update strategy
     */
    public IUpdateStrategy<?> getUpdateStrategy() {
        return updateStrategy;
    }

    /**
     * Returns the view model root, which is derived from the update strategy
     * and kept for the whole life-cycle of the view context,
     * in order to enable proper incremental update.
     * 
     * @return the base model or null if no update strategy is set
     */
    public Object getViewModel() {
        return viewModel;
    }

    /**
     * Adds a transformation context to the view context.
     * 
     * @param transformationContext
     *            the transformation context
     */
    protected void addTransformationContext(final TransformationContext<?, ?> transformationContext) {
        transformationContexts.add(transformationContext);
        transformationContext.setViewContext(this);
    }

    /**
     * Adds a list of transformation contexts to the view context.
     * 
     * @param contexts
     *            the transformation contexts
     */
    protected void addTransformationContexts(final List<TransformationContext<?, ?>> contexts) {
        for (TransformationContext<?, ?> transformationContext : contexts) {
            addTransformationContext(transformationContext);
        }
    }

    /**
     * Returns the list of transformation contexts.
     * 
     * @return the transformation contexts
     */
    public List<TransformationContext<?, ?>> getTransformationContexts() {
        return transformationContexts;
    }

    /**
     * Returns the class of the target model in the complete context.
     * 
     * @return the class of the target model or null if there is no transformation in this context
     */
    public Class<?> getTargetClass() {
        return viewerProvider.getModelClass();
    }

    /**
     * Returns the element in the source model that translates to the given object in the context's
     * model by using the transformations invoked to obtain that model.
     * 
     * @param element
     *            the object in the context's model
     * @return the element in the source model or null if the link could not be made
     */
    public Object getSourceElement(final Object element) {
        Object source = element;
        for (TransformationContext<?, ?> transformationContext : transformationContextsRev) {
            if (source == null) {
                return null;
            }
            source = transformationContext.getSourceElement(source);
        }
        return source;
    }

    /**
     * Returns the element in the context's model that derives from the given element in the source
     * model by using the transformations invoked to obtain the context's model.
     * 
     * @param element
     *            the element in the source model
     * @return the element in the context's model or null if the link could not be made
     */
    public Object getTargetElement(final Object element) {
        Object target = element;
        for (TransformationContext<?, ?> transformationContext : transformationContexts) {
            if (target == null) {
                return null;
            }
            target = transformationContext.getTargetElement(element);
        }
        return target;
    }

    /**
     * Resets the view context.
     */
    protected void reset() {
        transformationContexts.clear();
        viewerProvider = null;
        updateStrategy = null;
        businessModel = null;
        viewModel = null;
    }
    
    private Map<TransformationContext<?, ?>, Set<TransformationOption>> options = null;
    
    /**
     * Returns the set of {@link TransformationOption TransformationOptions} declared by the
     * transformation and forward to the users in the UI in order to allow them to influence the
     * transformation result.
     * 
     * @return the set of {@link TransformationOption TransformationOptions}
     * 
     * @author chsch
     */
    public Map<TransformationContext<?, ?>, Set<TransformationOption>> getTransformationOptions() {
        if (this.options == null) {
            Map<TransformationContext<?, ?>, Set<TransformationOption>> map = Maps
                    .newLinkedHashMap();
            for (TransformationContext<?, ?> c : this.transformationContexts) {
                map.put(c, c.getTransformationOptions());
            }        
            this.options = ImmutableMap.copyOf(map);
        }
        return this.options;
    }
}

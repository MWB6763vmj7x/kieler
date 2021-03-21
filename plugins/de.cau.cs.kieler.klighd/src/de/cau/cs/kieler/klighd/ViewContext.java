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

import com.google.common.collect.Lists;

import de.cau.cs.kieler.core.properties.MapPropertyHolder;

/**
 * A view context contains a viewer provider and a model that is accepted by the viewer provider.
 * 
 * @author mri
 */
public final class ViewContext extends MapPropertyHolder {

    /** the viewer provider. */
    private IViewerProvider viewerProvider = null;
    /** the list of transformation contexts in this view context. */
    private List<TransformationContext<?, ?>> transformationContexts = Lists.newLinkedList();
    /** the reveresed list of transformation contexts. */
    private List<TransformationContext<?, ?>> transformationContextsRev = Lists
            .reverse(transformationContexts);

    /**
     * Serts the contexts viewer provider.
     * 
     * @param viewerProvider
     *            the viewer provider
     */
    protected void setViewerProvider(final IViewerProvider viewerProvider) {
        this.viewerProvider = viewerProvider;
    }

    /**
     * Returns the contexts viewer provider.
     * 
     * @return the viewer provider
     */
    public IViewerProvider getViewerProvider() {
        return viewerProvider;
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
     * Returns the contexts source model.
     * 
     * @return the source model
     */
    public Object getSourceModel() {
        return transformationContexts.get(0).getSourceModel();
    }

    /**
     * Returns the contexts target model.
     * 
     * @return the target model
     */
    public Object getTargetModel() {
        return transformationContextsRev.get(0).getTargetModel();
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
    }

}

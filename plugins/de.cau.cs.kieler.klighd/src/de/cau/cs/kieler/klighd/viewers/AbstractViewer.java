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
package de.cau.cs.kieler.klighd.viewers;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.klighd.IViewer;
import de.cau.cs.kieler.klighd.IViewerEventListener;

/**
 * An abstract base class for viewers which provides an implementation for the handling of listeners
 * and an empty implementation for advanced functionality.
 * 
 * @author mri
 * @author chsch
 * 
 * @param <T>
 *            the type of the model this viewer accepts
 */
public abstract class AbstractViewer<T> implements IViewer<T> {

    /** the listeners registered on this viewer. */
    private Set<IViewerEventListener> listeners = new LinkedHashSet<IViewerEventListener>();
    
    /**
     * {@inheritDoc}
     */
    public void setModel(final T model) {
        setModel(model, false);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelection(final Iterable<KGraphElement> diagramElements) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void clearSelection() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void select(final Iterable<KGraphElement> diagramElements) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void unselect(final Iterable<KGraphElement> diagramElements) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void zoom(final float zoomLevel, final int duration) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void zoomToFit(final int duration) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void reveal(final KGraphElement diagramObject, final int duration) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void centerOn(final KGraphElement diagramObject, final int duration) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void centerOn(final Object diagramObject, final int duration) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void collapse(final KNode diagramElement) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void collapse(final Object semanticElement) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void expand(final Object semanticElement) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void expand(final KNode diagramElement) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void toggleExpansion(final KNode diagramElement) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void toggleExpansion(final Object semanticElement) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void hide(final Object semanticElement) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void hide(final KGraphElement diagramElement) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void show(final Object semanticElement) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void show(final KGraphElement diagramElement) {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    public void addEventListener(final IViewerEventListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeEventListener(final IViewerEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies the registered listeners about the occurrence of a selection event.
     * 
     * @param selectedElements
     *            the selected elements
     */
    protected void notifyListenersSelection(final Iterable<? extends Object> selectedElements) {
        for (IViewerEventListener listener : listeners) {
            listener.selection(this, selectedElements);
        }
    }

    /**
     * Notifies the registered listeners about the occurrence of a selected event.
     * 
     * @param selectedElement
     *            the selected element
     */
    protected void notifyListenersSelected(final EObject selectedElement) {
        for (IViewerEventListener listener : listeners) {
            listener.selected(this, selectedElement);
        }
    }
    
    /**
     * Notifies the registered listeners about the occurrence of a unselected event.
     * 
     * @param unselectedElement
     *            the selected element
     */
    protected void notifyListenersUnselected(final EObject unselectedElement) {
        for (IViewerEventListener listener : listeners) {
            listener.unselected(this, unselectedElement);
        }
    }
    
}


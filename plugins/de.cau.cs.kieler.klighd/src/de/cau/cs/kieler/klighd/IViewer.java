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

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.widgets.Control;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.krendering.KText;
import de.cau.cs.kieler.klighd.viewers.ContextViewer;

/**
 * The common interface for viewers displaying diagrams of provided models.<br>
 * <br>
 * This interface is supposed to be used in client's code for revealing elements in diagrams,
 * expanding & collapsing elements, clipping the diagram, or asking for the source elements
 * of representatives in diagrams by referring to {@link #getViewContext()}.
 * 
 * @author mri
 * @author chsch
 * 
 * @param <T>
 *            the type of the model this viewer accepts
 */
public interface IViewer<T> {

    /**
     * Returns the control used by this viewer.
     * 
     * @return the control
     */
    Control getControl();

    /**
     * Returns the {@link ContextViewer} containing this viewer.
     * 
     * @return the {@link ContextViewer}
     */
    ContextViewer getContextViewer();
    
    /**
     * Sets the input model for this viewer.
     * 
     * @param model
     *            the input model
     */
    void setModel(T model);

    /**
     * Sets the input model for this viewer.<br>
     * <br>
     * If synchronization is enabled his model is incrementally updated by KLighD. The
     * implementation of the viewer should utilize a listener mechanic on the model to keep the
     * visualization in sync.
     * 
     * @param model
     *            the input model
     * @param sync
     *            true if the viewer should synchronize the visualization with the model; false else
     */
    void setModel(T model, boolean sync);

    /**
     * Returns the {@link ViewContext} that is associated to <code>this</this> viewer.
     * 
     * @return the associated {@link ViewContext} or <code>null</code> if no input model is set
     */
    ViewContext getViewContext();

    /**
     * Registers the given {@link IViewChangeListener} to be notified of events of the given
     * {@link ViewChangeType ViewChangeEventTypes}. The provided <code>listener</code> will be
     * notified after all possible changes if no {@link ViewChangeType} is specified.
     * 
     * @param listener
     *            the {@link IViewChangeListener} to be registered
     * @param eventTypes
     *            the {@link ViewChangeType} this listener is notified of
     */
    void addViewChangedListener(IViewChangeListener listener,
            ViewChangeType... eventTypes);

    /**
     * Unregisters the given {@link IViewChangeListener}.
     * 
     * @param listener the {@link IViewChangeListener} to be removed from the listeners queue
     */
    void removeViewChangedEventListener(IViewChangeListener listener);


    /* ----------------------------- */
    /*   the view modification API   */
    /* ----------------------------- */
    
    /**
     * Provides the visibility state of the given element's representation, assuming the parent of
     * {@link Object}'s representative is visible. A recursive invisibility check along the
     * containment hierarchy is omitted for performance reasons. Thus, given nested diagram nodes A
     * contains B contains C with B collapsed this method may return <code>true</code> for C.
     * 
     * @param semanticElement
     *            being visualized by a {@link KGraphElement}
     * @return <code>true</code> if the {@link KGraphElement} related to
     *         <code>semanticElement</code> is visible, <code>false</code> otherwise.
     */
    boolean isVisible(Object semanticElement);
    
    /**
     * Provides the visibility state of the given diagram element, assuming the parent
     * {@link KGraphElement} is visible. A recursive invisibility check along the containment
     * hierarchy is omitted for performance reasons. Thus, given nested diagram nodes A contains B
     * contains C with B collapsed this method may return <code>true</code> for C.
     * 
     * @param diagramElement
     *            a {@link KGraphElement}
     * @return <code>true</code> if the {@link KGraphElement} <code>diagramElement</code> is
     *         visible, <code>false</code> otherwise.
     */
    boolean isVisible(KGraphElement diagramElement);
     
    /**
     * Reveals the representation of the given semantic element over the specified duration.
     * 
     * @param semanticElement
     *            the semantic element to center on
     * @param duration
     *            the duration over which the panning animation is done
     */
    void reveal(Object semanticElement, int duration);
    
    /**
     * Reveals the given diagram element over the specified duration.
     * 
     * @param diagramElement
     *            the diagram element
     * @param duration
     *            the duration
     */
    void reveal(KGraphElement diagramElement, int duration);

    /**
     * Centers on the representation of the given semantic element over the specified duration.
     * 
     * @param semanticElement
     *            the semantic element to center on
     * @param duration
     *            the duration over which the panning animation is done
     */
    void centerOn(Object semanticElement, int duration);
    
    /**
     * Centers on the given diagram element over the specified duration.
     * 
     * @param diagramElement
     *            the diagram element
     * @param duration
     *            the duration over which the panning animation is done
     */
    void centerOn(KGraphElement diagramElement, int duration);
    
    /**
     * Zooms to the given zoom level over the specified duration.
     * 
     * @param zoomLevel
     *            the zoom level
     * @param duration
     *            the duration
     */
    void zoomToLevel(float zoomLevel, int duration);
    
    /**
     * Performs the specified zoom style over the specified duration.
     * 
     * @param style
     *            the desired zoom style
     * @param duration
     *            the duration
     */
    void zoom(ZoomStyle style, int duration);
    
    /**
     * Provides the expansion state of the given element's representation.
     * 
     * @param semanticElement
     *            being visualized by a (hierarchic) {@link KNode}
     * @return <code>true</code> if the {@link KNode} related to <code>semanticElement</code> is
     *         expanded, <code>false</code> otherwise.
     */
    boolean isExpanded(Object semanticElement);
    
    /**
     * Provides the expansion state of the given diagram node.
     * 
     * @param diagramElement
     *            a (hierarchic) {@link KNode}
     * @return <code>true</code> if the {@link KNode} <code>diagramElement</code> is expanded,
     *         <code>true</code> otherwise.
     */
    boolean isExpanded(KNode diagramElement);

    /**
     * Collapses the representation of the given element. Note that there must exist related element
     * <-> diagram element tracking information in the {@link TransformationContexts}.
     * 
     * @param semanticElement
     *            the semantic element to be expanded
     */
    void collapse(Object semanticElement);
    
    /**
     * Expands the given representation element.
     * 
     * @param diagramElement the diagram element to be expanded
     */
    void collapse(KNode diagramElement);

    /**
     * Expands the representation of the given element, i.e. populates it with children and changes
     * its rendering if necessary. Note that there must exist related element <-> diagram element
     * tracking information in the {@link TransformationContexts}.
     * 
     * @param semanticElement
     *            the semantic element to be expanded
     */
    void expand(Object semanticElement);
    
    /**
     * Expands the given representation element.
     * 
     * @param diagramElement the diagram element to be expanded
     */
    void expand(KNode diagramElement);

    /**
     * Expands the representation of the given element if collapsed and vice versa. Note that there
     * must exist related element <-> diagram element tracking information in the
     * {@link TransformationContexts}.
     * 
     * @param semanticElement
     *            the semantic element to be expanded
     */
    void toggleExpansion(Object semanticElement);
    
    /**
     * Toggles the expansion state of the given representation element. If it is expanded, it gets
     * collapsed, and vice versa.
     * 
     * @param diagramElement
     *            the diagram element to be expanded
     */
    void toggleExpansion(KNode diagramElement);

    /**
     * Hides the representation of the given {@link Object} from the diagram. In combination with
     * {@link #show(Object)} this method can be used for changing the diagram's amount of detail
     * without changing the view model.
     * 
     * @param semanticElement
     *            the semantic element to be hidden
     */
    void hide(Object semanticElement);
    
    /**
     * Hides the given {@link KGraphElement} from the diagram. In combination with
     * {@link #show(KGraphElement)} this method can be used for changing the diagram's amount of
     * detail without changing the view model.
     * 
     * @param diagramElement
     *            the diagram element to be hidden
     */
    void hide(KGraphElement diagramElement);
    
    /**
     * Shows the representation of the given {@link Object} in the diagram. In combination with
     * {@link #hide(Object)} this method can be used for changing the diagram's amount of detail
     * without changing the view model.
     * 
     * @param semanticElement
     *            the semantic element to show up
     */
    void show(Object semanticElement);
    
    /**
     * Shows the given {@link KGraphElement} in the diagram. In combination with
     * {@link #hide(KGraphElement)} this method can be used for changing the diagram's amount of
     * detail without changing the view model.
     * 
     * @param diagramElement
     *            the diagram element to show up
     */
    void show(KGraphElement diagramElement);

    /**
     * Limits the visible elements of the diagram to the content of the representation of the given
     * {@link Object} without causing any change on the view model. Hence, this method can be used
     * for changing the diagram's amount of detail without changing the view model.<br>
     * The clip can be reset to the whole diagram by calling <code>clip((Object) null)</code>.
     * 
     * @param semanticElement
     *            the semantic element to whose representation the diagram view is to be limited,
     *            may be <code>null</code>
     */
    void clip(Object semanticElement);
    
    /**
     * Limits the visible elements of the diagram to the content of the given {@link KNode} without
     * causing any change on the view model. Hence, this method can be used for changing the
     * diagram's amount of detail without changing the view model.<br>
     * The clip can be reset to the whole diagram by calling <code>clip((KNode) null)</code>.
     * 
     * @param diagramElement
     *            the diagram element to which the diagram view is to be limited, may be
     *            <code>null</code>
     */
    void clip(KNode diagramElement);
    
    // a getClipSemantic() is still absent, it's not clear whether that is really necessary
    
    /**
     * Provides the currently set diagram clip.
     * 
     * @return the {@link KNode} that is currently clipped.
     */
    KNode getClip();

    /**
     * Scales the representation of the given {@link Object} in the diagram, i.e. increases or
     * decreases the size of the corresponding diagram node wrt. to its siblings without
     * re-arranging the children of that node.
     * 
     * @param semanticElement
     *            the semantic element whose representing node is to be scaled 
     * @param factor
     *            the absolute factor by which the representing node is to be scaled
     */
    void scale(Object semanticElement, float factor);
    
    /**
     * Scales the given {@link KGraphElement} in the diagram, i.e. increases or decreases its size
     * wrt. to its siblings without re-arranging the children of that node.
     * 
     * @param diagramElement
     *            the diagram node to be scaled
     * @param factor
     *            the absolute factor by which the representing node is to be scaled
     */
    void scale(KNode diagramElement, float factor);
    
    /**
     * Provides the scale factor being currently applied to the diagram node representing the given
     * {@link Object}.
     * 
     * @param semanticElement
     *            the semantic element being represented by a diagram node
     * @return the scale factor of the corresponding diagram node 
     */
    float getScale(Object semanticElement);

    /**
     * Provides the scale factor being currently applied to the given diagram node.
     * 
     * @param diagramElement
     *            the diagram node whose scale factory is requested
     * @return the scale factor of the given diagram node 
     */
    float getScale(KNode diagramElement);


    /* ----------------------------- */
    /*   the selection setting API   */
    /* ----------------------------- */
    
    /**
     * Provides the current {@link org.eclipse.jface.viewers.ISelection} provided by the diagram viewer.
     *  
     * @return the current {@link org.eclipse.jface.viewers.ISelection}
     */
    KlighdTreeSelection getSelection();
    
    /**
     * Adds or removes the representative of the provided element to/from the current selection
     * depending on its presence in the current selection.
     * 
     * @param semanticElement
     *            the element to add or remove
     */
    void toggleSelectionOf(Object semanticElement);
  
    /**
     * Adds or removes a diagram element to/from the current selection depending on its presence in
     * the current selection.
     * 
     * @param diagramElement
     *            the element to add or remove
     */
    void toggleSelectionOf(KGraphElement diagramElement);
 
    /**
     * Adds or removes a diagram element to/from the current selection depending on its presence in
     * the current selection.
     * 
     * @param diagramElement
     *            the element to add or remove
     */
    void toggleSelectionOf(KText diagramElement);
 
    /**
     * Adds or removes the representatives of the provided set of semantic elements to/from the
     * current selection depending on their particular presences in the current selection.
     * 
     * @param semanticElements
     *            the elements to add or remove
     */
    void toggleSelectionOfSemanticElements(Set<Object> semanticElements);

    /**
     * Adds or removes a set of diagram element to/from the current selection depending on their
     * particular presences in the current selection.
     * 
     * @param diagramElements
     *            the elements to add or remove
     */
    void toggleSelectionOfDiagramElements(Set<? extends EObject> diagramElements);

    /**
     * Dismisses the current selection and creates a new one containing the only the representative
     * of the provided element.
     * 
     * @param diagramElement
     *            the element to be put into the new selection
     */
    void resetSelectionTo(Object diagramElement);

    /**
     * Dismisses the current selection and creates a new one containing the provided diagram
     * element.
     * 
     * @param diagramElement
     *            the element to be put into the new selection
     */
    void resetSelectionTo(KGraphElement diagramElement);

    /**
     * Dismisses the current selection and creates a new one containing the provided diagram
     * element.
     * 
     * @param diagramElement
     *            the element to be put into the new selection
     */
    void resetSelectionTo(KText diagramElement);

    /**
     * Dismisses the current selection and creates a new one containing the representatives of the
     * provided elements.
     * 
     * @param semanticElements
     *            the elements to be put into the new selection
     */
    void resetSelectionToSemanticElements(Iterable<? extends Object> semanticElements);

    /**
     * Dismisses the current selection and creates a new one containing the provided diagram
     * elements.
     * 
     * @param diagramElements
     *            the elements to be put into the new selection
     */
    void resetSelectionToDiagramElements(Iterable<? extends EObject> diagramElements);
}

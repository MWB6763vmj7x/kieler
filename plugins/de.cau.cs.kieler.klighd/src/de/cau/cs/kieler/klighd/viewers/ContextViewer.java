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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

import com.google.common.collect.Lists;

import de.cau.cs.kieler.klighd.IViewer;
import de.cau.cs.kieler.klighd.IViewerEventListener;
import de.cau.cs.kieler.klighd.LightDiagramServices;
import de.cau.cs.kieler.klighd.TransformationContext;
import de.cau.cs.kieler.klighd.TransformationOption;
import de.cau.cs.kieler.klighd.ViewContext;
import de.cau.cs.kieler.klighd.triggers.KlighdSelectionTrigger;
import de.cau.cs.kieler.klighd.triggers.KlighdSelectionTrigger.KlighdSelectionState;
import de.cau.cs.kieler.klighd.triggers.KlighdSelectionTrigger.KlighdSelectionState.SelectionElement;
import de.cau.cs.kieler.klighd.views.DiagramViewManager;
import de.cau.cs.kieler.klighd.views.DiagramViewPart;

/**
 * A viewer for instances of type {@code ViewContext}. This viewer acts as a wrapper for the viewer
 * supplied by the current view context. The method {@code getControl} returns the control for that
 * viewer and all other methods are delegated to the wrapped viewer.<br>
 * <br>
 * In addition it is possible to set a message to be shown instead of a view context, the wrapped
 * viewer is then of type {@code StringViewer}.<br>
 * <br>
 * This viewer also implements the {@code ISelectionProvider} interface and acts as the KLighD view
 * provider for selection events.
 * 
 * @author mri, chsch
 */
public class ContextViewer extends AbstractViewer<Object> implements IViewerEventListener,
        ISelectionProvider {

    private DiagramViewPart viewPart;
    /** the parent composite. */
    private Composite parent;
    /** the id of the view this viewer belongs to. */
    private String viewId;
    /** the current viewer. */
    private IViewer<Object> currentViewer;
    /** the current view context. */
    private ViewContext currentViewContext = null;
    /** the selection listeners registered on this view. */
    private Set<ISelectionChangedListener> selectionListeners
        = new LinkedHashSet<ISelectionChangedListener>();
    /** the current selection. */
    private Selection selection = new Selection();

    /**
     * Constructs a view context viewer.
     * 
     * @param parent
     *            the parent composite
     * @param viewId
     *            the id of the view this viewer belongs to
     * @param viewPart
     *            the viewPart this view is attached to
     */
    public ContextViewer(final Composite parent, final String viewId, final DiagramViewPart viewPart) {
        this.parent = parent;
        this.viewId = viewId;
        this.viewPart = viewPart;
        showMessage("");
    }

    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        return currentViewer.getControl();
    }
    

    /**
     * {@inheritDoc}
     */
    public synchronized void setModel(final Object model, final boolean sync) {
        // if the model is a view context adapt the viewer to the given context if possible
        if (model instanceof ViewContext) {
            ViewContext viewContext = (ViewContext) model;
            // remove the old viewer
            removeViewer();

            // create the new viewer
            IViewer<?> viewer =
                    LightDiagramServices.getInstance().createViewer(viewContext, parent);

            // add the new viewer
            addViewer(viewer);
            // set the new view context
            currentViewContext = viewContext;
            // reset the current selection
            resetSelection();
            
        } else if (model instanceof String) {
            // if the model is a string show it
            showMessage((String) model);

            // reset the current selection
            resetSelection();
        }
        
        // fill the options pane according to the the incorporated transformations
        // TODO implement this (the following line is a placeholder)
        viewPart.updateOptions();
        
        // fill menu with the option entries provided by the incorporated transformations
        updateOptionsMenu();
    }
    
    
    /**
     * Fills the synthesis option menu after the model (viewContext) has been set.
     */
    private void updateOptionsMenu() {
        if (viewPart == null) {
            return;
        }

        final ViewContext context = viewPart.getContextViewer().getCurrentViewContext();
        if (context == null) {
            return;
        }

        IMenuManager mm = viewPart.getViewSite().getActionBars().getMenuManager();
        mm.removeAll();

        for (final Map.Entry<TransformationContext<?, ?>, Set<TransformationOption>> entry : context
                .getTransformationOptions().entrySet()) {
            
            for (final TransformationOption option : entry.getValue()) {
                
                if (option.isCheckOption()) {
                    mm.add(new OptionEntryAction(option.getName(), IAction.AS_CHECK_BOX,
                            (Boolean) entry.getKey().getOptionValue(option)) {
                        public void runWithEvent(final Event event) {
                            if ((event.type & SWT.MouseUp) != 0) {
                                entry.getKey().configureOption(option, isChecked());
                                DiagramViewManager.getInstance().updateView(viewId);
                            }
                        }
                    });
                    
                } else if (option.isChoiceOption()) {
                    mm.add(new Separator(entry.getKey().getTransformation().getClass()
                            .getSimpleName() + "_" + option.getName()));
                    
                    for (final Object value : option.getValues()) {
                        
                        mm.add(new OptionEntryAction(value.toString(), IAction.AS_RADIO_BUTTON,
                                option.getInitialValue().equals(value)) {
                            public void runWithEvent(final Event event) {
                                if ((event.type & SWT.MouseUp) != 0 && isChecked()) {
                                    entry.getKey().configureOption(option, value);
                                    DiagramViewManager.getInstance().updateView(viewId);
                                }
                            }                        
                        });
                    }
                }
            }
            mm.add(new Separator());
        }
        viewPart.getViewSite().getActionBars().updateActionBars();
    }
    
    
    /**
     * A {@link Action Actions} representing view synthesis options in
     * {@link org.eclipse.ui.IViewPart IViewParts}' menus.
     * 
     * @author chsch
     */
    private static class OptionEntryAction extends Action {
     
        public OptionEntryAction(final String text, final int style, final Boolean initiallyChecked) {
            super(text, style);
            this.setChecked(initiallyChecked);
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public synchronized Object getModel() {
        if (currentViewer != null) {
            return currentViewer.getModel();
        }
        return null;
    }

    /**
     * Shows the given message.
     * 
     * @param message
     *            the message
     */
    public synchronized void showMessage(final String message) {
        if (!((IViewer<?>) currentViewer instanceof StringViewer)) {
            removeViewer();
            addViewer(new StringViewer(parent));
        }
        currentViewer.setModel(message, false);
    }

    @SuppressWarnings("unchecked")
    private synchronized void addViewer(final IViewer<?> viewer) {
        currentViewer = (IViewer<Object>) viewer;
        parent.layout();
        currentViewer.addEventListener(this);
    }

    private synchronized void removeViewer() {
        if (currentViewer != null) {
            currentViewer.removeEventListener(this);
            currentViewer.getControl().dispose();
            currentViewer = null;
            currentViewContext = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void selected(final IViewer<?> viewer, final Object selectedElement) {
        notifyListenersSelected(selectedElement);
    }

    /**
     * {@inheritDoc}
     */
    public void unselected(final IViewer<?> viewer, final Object unselectedElement) {
        notifyListenersUnselected(unselectedElement);
    }

    /**
     * {@inheritDoc}
     */
    public void selection(final IViewer<?> viewer, final Collection<?> selectedElements) {
        KlighdSelectionTrigger trigger = KlighdSelectionTrigger.getInstance();
        if (trigger != null) {
            // create the selection objects
            List<SelectionElement> selections = new LinkedList<SelectionElement>();
            // create the selection state
            KlighdSelectionState state = new KlighdSelectionState(viewId, getCurrentViewContext(),
                    currentViewer, selections);
            // fill the selection
            for (Object diagramObject : selectedElements) {
                selections.add(state.new SelectionElement(diagramObject));
            }
            trigger.trigger(state);
        }
        
        // chsch: was 
        //  updateSelection(selectedElements);
        //  notifyListenersSelection(selectedElements);
        //
        // updated it since it makes IMO more sense this way:

        // update the selection status for the ISelectionProvider interface
        List<Object> selectedModelElements = Lists.newArrayList();
        Object modelElement;
        for (Object element : selectedElements) {            
            modelElement = getCurrentViewContext().getSourceElement(element);
            if (modelElement != null) {
                selectedModelElements.add(modelElement);
            }
        }
        updateSelection(selectedModelElements);
        
        // propagate event to listeners on this viewer
        notifyListenersSelection(selectedModelElements);  
    }

    private void updateSelection(final Collection<?> selectedElements) {
        synchronized (selection) {
            selection.selectedElements.clear();
            selection.selectedElements.addAll(selectedElements);
        }
        notifySelectionListeners();
    }

    private void resetSelection() {
        synchronized (selection) {
            selection.selectedElements.clear();
        }
        notifySelectionListeners();
    }

    private void notifySelectionListeners() {
        synchronized (selectionListeners) {
            if (selectionListeners.size() > 0) {
                // create a clone of the selection
                Selection clone;
                synchronized (selection) {
                    clone = selection.clone();
                }
                // notify all selection listeners
                for (ISelectionChangedListener listener : selectionListeners) {
                    listener.selectionChanged(new SelectionChangedEvent(this, clone));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRecording(final boolean recording) {
        currentViewer.setRecording(recording);
    }

    /**
     * {@inheritDoc} 
     */
    public void setZoomToFit(final boolean zoomToFit) {
        currentViewer.setZoomToFit(zoomToFit);
    }
    
    /**
     * {@inheritDoc}
     */
    public ISelection getSelection() {
        synchronized (selection) {
            return selection.clone();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setSelection(final ISelection selection) {
        // not supported yet
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(final Object[] diagramElements) {
        if (currentViewer != null) {
            currentViewer.setSelection(diagramElements);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSelection() {
        if (currentViewer != null) {
            currentViewer.clearSelection();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(final Object[] diagramElements) {
        if (currentViewer != null) {
            currentViewer.select(diagramElements);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unselect(final Object[] diagramElements) {
        if (currentViewer != null) {
            currentViewer.unselect(diagramElements);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reveal(final Object diagramElement, final int duration) {
        if (currentViewer != null) {
            currentViewer.reveal(diagramElement, duration);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void centerOn(final Object diagramElement, final int duration) {
        if (currentViewer != null) {
            currentViewer.centerOn(diagramElement, duration);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoom(final float zoomLevel, final int duration) {
        if (currentViewer != null) {
            currentViewer.zoom(zoomLevel, duration);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoomToFit(final int duration) {
        if (currentViewer != null) {
            currentViewer.zoomToFit(duration);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addSelectionChangedListener(final ISelectionChangedListener listener) {
        selectionListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
        selectionListeners.remove(listener);
    }

    /**
     * Returns the currently active viewer.
     * 
     * @return the viewer
     */
    public IViewer<?> getActiveViewer() {
        return currentViewer;
    }

    /**
     * Returns the id of the related view part.
     * 
     * @return the view part id.
     */
    public String getViewPartId() {
        return viewId;
    }

    /**
     * Returns the currently active view context.
     * 
     * @return the view context
     */
    public synchronized ViewContext getCurrentViewContext() {
        return currentViewContext;
    }

    /**
     * An implementation of {@code IStructuredSelection} for the {@code ISelectionProvider}.
     */
    private class Selection implements IStructuredSelection, Iterable<Object>, Cloneable {
        // TODO chsch: IMO implementing ITreeSelection is reasonable and helpful 

        /** the objects which make up the selection. */
        private List<Object> selectedElements = new LinkedList<Object>();

        /**
         * {@inheritDoc}
         */
        public boolean isEmpty() {
            return selectedElements.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        public Object getFirstElement() {
            if (selectedElements.isEmpty()) {
                return null;
            } else {
                return selectedElements.get(0);
            }
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<Object> iterator() {
            return selectedElements.iterator();
        }

        /**
         * {@inheritDoc}
         */
        public int size() {
            return selectedElements.size();
        }

        /**
         * {@inheritDoc}
         */
        public Object[] toArray() {
            return selectedElements.toArray();
        }

        /**
         * {@inheritDoc}
         */
        public List<Object> toList() {
            return selectedElements;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Selection clone() {
            Selection clone = new Selection();
            clone.selectedElements.addAll(selectedElements);
            return clone;
        }

    }

}

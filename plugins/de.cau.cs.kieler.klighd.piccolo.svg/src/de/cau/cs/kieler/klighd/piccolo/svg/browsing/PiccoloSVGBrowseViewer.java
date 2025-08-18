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
package de.cau.cs.kieler.klighd.piccolo.svg.browsing;

import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.klighd.piccolo.internal.activities.ZoomActivity;
import de.cau.cs.kieler.klighd.piccolo.internal.controller.DiagramController;
import de.cau.cs.kieler.klighd.piccolo.internal.events.KlighdKeyEventListener;
import de.cau.cs.kieler.klighd.piccolo.internal.events.KlighdMouseEventListener;
import de.cau.cs.kieler.klighd.piccolo.internal.events.PMouseWheelZoomEventHandler;
import de.cau.cs.kieler.klighd.piccolo.internal.events.PSWTSimpleSelectionEventHandler;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.ITracingElement;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KNodeNode;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.PEmptyNode;
import de.cau.cs.kieler.klighd.piccolo.svg.KlighdSVGGraphicsImpl;
import de.cau.cs.kieler.klighd.piccolo.viewer.INodeSelectionListener;
import de.cau.cs.kieler.klighd.util.RenderingContextData;
import de.cau.cs.kieler.klighd.viewers.AbstractViewer;
import de.cau.cs.kieler.klighd.viewers.ContextViewer;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.swt.PSWTCanvas;

//import java.util.List;

/**
 * A viewer for Piccolo diagram contexts.
 * 
 * @author mri
 */
public class PiccoloSVGBrowseViewer extends AbstractViewer<KNode> implements INodeSelectionListener {

    /** the canvas used for drawing. */
    private PSWTCanvas canvas;
    /** the current selection event handler. */
    private PSWTSimpleSelectionEventHandler selectionHandler = null;

    /** the graph controller. */
    private DiagramController controller;

    private KlighdSVGGraphicsImpl graphics;
    
    private Map<Integer, KNode> currentHashCodeMapping = Maps.newHashMap();

    /**
     * Creates a Piccolo viewer with default style.
     * 
     * @param parentViewer
     *            the parent {@link ContextViewer}
     * @param parent
     *            the parent composite
     */
    public PiccoloSVGBrowseViewer(final Composite parent) {
        this(parent, SWT.NONE);
    }

    /**
     * Creates a Piccolo viewer with given style.
     * 
     * @param theParentViewer
     *            the parent {@link ContextViewer}
     * @param parent
     *            the parent composite
     * @param style
     *            the style attributes
     */
    public PiccoloSVGBrowseViewer(final Composite parent, final int style) {
        if (parent.isDisposed()) {
            final String msg =
                    "KLighD (piccolo): A 'PiccoloViewer' has been tried to attach to a"
                            + "disposed 'Composite' widget.";
            throw new IllegalArgumentException(msg);
        }

        graphics = new KlighdSVGGraphicsImpl(parent.getDisplay());

        this.canvas = new PSWTCanvas(parent, style) {

            @Override
            protected Graphics2D getGraphics2D(final GC gc, final Device device) {
                graphics.setDevice(device);
                graphics.setGC(gc);
                return (Graphics2D) graphics;
            }

            // with this specialized implementation I register
            // customized event listeners that do not translate SWT events into AWT ones.
            protected void installInputSources() {
                // TODO for the moment we need the original ones, too, as long as the the
                // PSWTSimpleSelectionEventHandler is not migrated to the custom listeners
                super.installInputSources();

                this.addKeyListener(new KlighdKeyEventListener(this));
                KlighdMouseEventListener mouseListener = new KlighdMouseEventListener(this);
                this.addMouseListener(mouseListener);
                this.addMouseMoveListener(mouseListener);
                this.addMouseTrackListener(mouseListener);
                this.addMouseWheelListener(mouseListener);
            }

            /**
             * {@inheritDoc}.<br>
             * <br>
             * This specialized method checks the validity of the canvas before something is painted
             * in order to avoid the 'Widget is disposed' errors.
             */
            public void repaint(final PBounds bounds) {
                if (!this.isDisposed()) {
                    // super.repaint(bounds);
                    super.repaint();
                }

                // update the svg
                BrowsingSVGServer.getInstance().setViewer(PiccoloSVGBrowseViewer.this);
                BrowsingSVGServer.getInstance().broadcastSVG();
            }

        };

        // this reduces flickering drastically
        canvas.setDoubleBuffered(true);

        // canvas.setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        // canvas.removeInputEventListener(canvas.getPanEventHandler());
        // prevent conflicts with selection handler
        canvas.getPanEventHandler().setEventFilter(
                new PInputEventFilter(InputEvent.BUTTON1_MASK, InputEvent.CTRL_MASK));
        // exchange the zoom event handler
        canvas.removeInputEventListener(canvas.getZoomEventHandler());
        canvas.addInputEventListener(new PMouseWheelZoomEventHandler());
        // add a context menu
        addContextMenu(canvas);
    }

    public void globalRedraw() {

        if (controller != null) {
            currentHashCodeMapping.clear();
            recurseNodes(controller.getNode());
        }
//        controller.getNode().invalidateFullBounds();
        canvas.redraw();
    }

    private void recurseNodes(PNode parent) {

        List<WrappedKNodeNode> toAdd = Lists.newLinkedList();

        for (int i = 0; i < parent.getChildrenCount(); i++) {
            PNode child = parent.getChild(i);
            recurseNodes(child);

            if (child instanceof KNodeNode) {
                // System.out.println(((KNodeNode) child).getGraphElement());
                WrappedKNodeNode wrapper =
                        new WrappedKNodeNode(child, ((KNodeNode) child).getGraphElement());
                toAdd.add(wrapper);
                child.addChild(wrapper);
                
                currentHashCodeMapping.put(((KNodeNode) child).getGraphElement().hashCode(), ((KNodeNode) child).getGraphElement());
                //expand(((KNodeNode) child).getGraphElement());
            }
        }

//        System.out.println("PRE " + parent.getChildrenCount());
//        for (WrappedKNodeNode node : toAdd) {
//            parent.addChild(node);
//        }
        
//        System.out.println("AFTER " + parent.getChildrenCount());

    }

    public KlighdSVGGraphicsImpl getGraphics() {
        return this.graphics;
    }

    /**
     * Creates the context menu and adds the actions.
     * 
     * @param composite
     *            the composite to add the context menu to
     */
    private void addContextMenu(final Composite composite) {
        MenuManager menuManager = new MenuManager();
        // add the 'save-as-image' action
        // Action saveAsImageAction =
        // new SaveAsImageAction(this, Messages.PiccoloViewer_save_as_image_text);
        // menuManager.add(saveAsImageAction);

        // create the context menu
        Menu menu = menuManager.createContextMenu(composite);
        composite.setMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        return canvas;
    }

    /**
     * {@inheritDoc}
     */
    public void setModel(final KNode model, final boolean sync) {
        // remove the old selection handler
        if (selectionHandler != null) {
            canvas.removeInputEventListener(selectionHandler);
            selectionHandler = null;
        }

        // prepare the camera
        PCamera camera = canvas.getCamera();
        // resetCamera(camera);
        resizeAndResetLayers(2);

        // create a controller for the graph
        controller = new DiagramController(model, camera.getLayer(0), sync);
        controller.initialize();

        // add a node for the marquee
        PEmptyNode marqueeParent = new PEmptyNode();
        camera.getLayer(1).addChild(marqueeParent);

        // add a selection handler
        selectionHandler = new PSWTSimpleSelectionEventHandler(camera, marqueeParent);
        canvas.addInputEventListener(selectionHandler);
        // canvas.addInputEventListener(new KlighdActionEventHandler(this));

        // forward the selection events
        selectionHandler.addSelectionListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public KNode getModel() {
        if (controller != null) {
            return controller.getNode().getGraphElement();
        }
        return null;
    }

    /**
     * Resizes the number of layers in the camera to the given number and resets them.
     * 
     * @param number
     *            the number of layers
     */
    private void resizeAndResetLayers(final int number) {
        PRoot root = canvas.getRoot();
        PCamera camera = canvas.getCamera();
        // resize down
        while (camera.getLayerCount() > number) {
            PLayer layer = camera.getLayer(camera.getLayerCount() - 1);
            camera.removeLayer(layer);
            root.removeChild(layer);
        }
        // resize up
        while (camera.getLayerCount() < number) {
            PLayer layer = new PLayer();
            root.addChild(layer);
            camera.addLayer(layer);
        }
        // reset
        @SuppressWarnings("unchecked")
        Iterable<PLayer> layers = camera.getLayersReference();
        for (PLayer layer : layers) {
            layer.removeAllChildren();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setRecording(final boolean recording) {
        controller.setRecording(recording);
    }

    /**
     * {@inheritDoc}
     */
    public void setZoomToFit(final boolean zoomToFit) {
        controller.setZoomToFit(zoomToFit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(final Iterable<EObject> diagramElements) {
        if (selectionHandler != null) {
            selectionHandler.unselectAll();
            select(diagramElements);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSelection() {
        if (selectionHandler != null) {
            selectionHandler.unselectAll();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void select(final Iterable<EObject> diagramElements) {
        if (selectionHandler != null) {
            for (Object diagramElement : diagramElements) {
                PNode node = getRepresentation(diagramElement);
                if (node != null) {
                    selectionHandler.select(node);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unselect(final Iterable<EObject> diagramElements) {
        if (selectionHandler != null) {
            for (Object diagramElement : diagramElements) {
                PNode node = getRepresentation(diagramElement);
                if (node != null) {
                    selectionHandler.unselect(node);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoom(final float zoomLevel, final int duration) {
        ZoomActivity zoomActivity = new ZoomActivity(canvas.getCamera(), zoomLevel, duration);
        if (duration > 0) {
            canvas.getRoot().addActivity(zoomActivity);
        } else {
            zoomActivity.apply();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void zoomToFit(final int duration) {
        controller.zoomToFit(duration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reveal(final EObject diagramElement, final int duration) {
        PNode node = getRepresentation(diagramElement);
        if (node != null) {
            // move the camera so it includes the bounds of the node
            PCamera camera = canvas.getCamera();
            camera.animateViewToPanToBounds(node.getFullBounds(), duration);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void centerOn(final EObject diagramElement, final int duration) {
        PNode node = getRepresentation(diagramElement);
        if (node != null) {
            // center the camera on the node
            PCamera camera = canvas.getCamera();
            camera.animateViewToCenterBounds(node.getFullBounds(), false, duration);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void expand(final KNode diagramElement) {
        controller.expand(diagramElement);
    }

    public void toggleExpansion(final String hashCode) {
        try {
            int hash = Integer.valueOf(hashCode);
            KNode node = currentHashCodeMapping.get(hash);
            toggleExpansion(node);
//            controller.setRecording(true);
//            expand(node);
//            controller.setRecording(false);
        } catch (NumberFormatException ex) {
            // fail silent
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void toggleExpansion(final KNode diagramElement) {
        controller.toggleExpansion(diagramElement);
    }

    /**
     * Returns the Piccolo representation for the given diagram element.
     * 
     * @param diagramElement
     *            the diagram element
     * @return the Piccolo representation
     */
    private PNode getRepresentation(final Object diagramElement) {
        if (diagramElement instanceof KGraphElement) {
            KGraphElement element = (KGraphElement) diagramElement;
            PNode node = RenderingContextData.get(element).getProperty(DiagramController.REP);
            if (node != null && node.getRoot() == canvas.getRoot()) {
                return node;
            }
        }
        return null;
    }

    /**
     * Returns the canvas used to render the scene graph.
     * 
     * @return the canvas
     */
    public PSWTCanvas getCanvas() {
        return canvas;
    }

    /**
     * {@inheritDoc}
     */
    public void selected(final PSWTSimpleSelectionEventHandler handler, final PNode node) {
        if (node instanceof ITracingElement<?>) {
            ITracingElement<?> graphElement = (ITracingElement<?>) node;
            notifyListenersSelected(graphElement.getGraphElement());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unselected(final PSWTSimpleSelectionEventHandler handler, final PNode node) {
        if (node instanceof ITracingElement<?>) {
            ITracingElement<?> graphElement = (ITracingElement<?>) node;
            notifyListenersUnselected(graphElement.getGraphElement());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void selection(final PSWTSimpleSelectionEventHandler handler, final Iterable<PNode> nodes) {

        @SuppressWarnings("unchecked")
        Iterable<ITracingElement<EObject>> elements =
                (Iterable<ITracingElement<EObject>>) (Iterable<?>) Iterables.filter(nodes,
                        ITracingElement.class);

        // List<EObject> graphElements = Lists.newLinkedList();
        // for (ITracingElement<EObject> element : elements) {
        // graphElements.add(element.getGraphElement());
        // }

        notifyListenersSelection(Iterables.transform(elements,
                new Function<ITracingElement<EObject>, EObject>() {
                    public EObject apply(final ITracingElement<EObject> element) {
                        return element.getGraphElement();
                    }
                }));
    }

    /**
     * {@inheritDoc}
     */
    public ContextViewer getContextViewer() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public IContentOutlinePage getOutlinePage() {
        return null;
    }

}

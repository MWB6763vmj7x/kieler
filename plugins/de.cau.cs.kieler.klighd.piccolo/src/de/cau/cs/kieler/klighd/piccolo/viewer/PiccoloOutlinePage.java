/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2013 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.klighd.piccolo.viewer;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Timer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.krendering.Colors;
import de.cau.cs.kieler.core.util.Pair;
import de.cau.cs.kieler.kiml.klayoutdata.KLayoutDataPackage;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.klighd.internal.IDiagramOutlinePage;
import de.cau.cs.kieler.klighd.piccolo.KlighdSWTGraphics;
import de.cau.cs.kieler.klighd.piccolo.internal.KlighdSWTGraphicsEx;
import de.cau.cs.kieler.klighd.piccolo.internal.events.KlighdBasicInputEventHandler;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KNodeNode;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KNodeTopNode;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KlighdCanvas;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KlighdMainCamera;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KlighdPath;
import de.cau.cs.kieler.klighd.piccolo.internal.util.KlighdPaintContext;
import de.cau.cs.kieler.klighd.piccolo.internal.util.NodeUtil;
import de.cau.cs.kieler.klighd.util.LimitedKGraphContentAdapter;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A content outline page for the Piccolo2D viewer.
 *
 * @author msp
 * @author uru
 * @author chsch
 */
public class PiccoloOutlinePage implements IDiagramOutlinePage {

    // Properties for the appearance of the outline rectangle.
    private static final int VIEWPORT_OUTLINE_LINEWIDTH = 2;
    private static final int VIEWPORT_OUTLINE_ROUNDNESS = 5;
    private static final Colors VIEWPORT_OUTLINE_COLOR = Colors.BLUE_3;
    private static final int VIEWPORT_OUTLINE_ALPHA = 25;

    private static final Colors CLIP_OUTLINE_COLOR = Colors.MEDIUM_PURPLE_4;
    private static final int CLIP_OVERLAY_ALPHA = 50;

    // Delay of the timed repaint.
    private static final int REPAINT_DELAY = 200;

    /** the canvas used for drawing. */
    private KlighdCanvas outlineCanvas;

    /** the graph layer to display. */
    private KNodeTopNode topNode;

    /** the observed {@link KNode}. */
    private KNode rootNode;

    /** the adapter listening to layout changes. */
    private Adapter nodeLayoutAdapter;

    /** the viewport outline rectangle. */
    private KlighdPath viewportOutlineRect;

    /** the clip outline overlay. */
    private KlighdPath clipOutlineOverlay;

    /** Indicates whether a drag action on the outline canvas is in progress. */
    private boolean isDragging = false;

    /** A timer to reduce the load during panning of the original canvas. */
    private Timer outlineRectTimer;

    /**
     * Configuration hook for customizing the viewport outline rectangle's fill color.
     *
     * @return a {@link Pair} of an {@link RGB} record determining the color and an {@link Integer}
     *         denoting the opacity (alpha value).
     */
    public Pair<RGB, Integer> getViewportOutlineRectPaint() {
        return Pair.of(toRGB(VIEWPORT_OUTLINE_COLOR), VIEWPORT_OUTLINE_ALPHA);
    }

    /**
     * Configuration hook for customizing the diagram clip overlay's fill color.<br>
     * This overlay cover's the whole diagram except the {@link KNode} the diagram is clipped to.
     *
     * @return a {@link Pair} of an {@link RGB} record determining the color and an {@link Integer}
     *         denoting the opacity (alpha value).
     */
    public Pair<RGB, Integer> getClipOutlineOverlayPaint() {
        return Pair.of(toRGB(CLIP_OUTLINE_COLOR), CLIP_OVERLAY_ALPHA);
    }

    /**
     * Convenience converter of {@link Colors} into {@link RGB} objects.
     *
     * @param color
     *            the {@link Colors} element to convert
     * @return the desired {@link RGB}
     */
    protected RGB toRGB(final Colors color) {
        if (color == null) {
            return null;
        } else {
            return new RGB(color.getRed(), color.getGreen(), color.getBlue());
        }
    }


    /**
     * Property Listener that listens to changes on the original canvas and triggers a redraw of the
     * outline rectangle.
     */
    private PropertyChangeListener propertyListener = new PropertyChangeListener() {

        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PCamera.PROPERTY_VIEW_TRANSFORM)
                    || evt.getPropertyName().equals(PNode.PROPERTY_FULL_BOUNDS)) {

                // only use the timer if we are not dragging
                if (isDragging) {
                    adjustOutlineRect();
                } else {
                    if (outlineRectTimer != null) {
                        outlineRectTimer.restart();
                    }
                }
            } else if (evt.getPropertyName().equals(PCamera.PROPERTY_LAYERS)
                    && evt.getNewValue() != null) {
                // listening to the layers is required for enabling/disabling the clip outline overlay
                //  immediately without any flickering

                // that the clip overlay invisible if the current clip node is the KNodeTopNode,
                //  and visible otherwise, the clip node is a KNodeNode in those cases
                clipOutlineOverlay.setVisible(((KlighdMainCamera) evt.getSource())
                        .getDisplayedKNodeNode() instanceof KNodeNode);
                clipOutlineOverlay.repaint();
            }
        }
    };

    /**
     * Named subclass of {@link KlighdCanvas} providing outline paint contexts.
     */
    private static final class KlighdOutlineCanvas extends KlighdCanvas {

        public KlighdOutlineCanvas(final Composite parent, final int style) {
            super(parent, style);
        }

        @Override
        protected KlighdPaintContext getPaintContext(final Graphics2D g2) {
            return KlighdPaintContext.createOutlinePaintContext((KlighdSWTGraphics) g2);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        outlineCanvas = new KlighdOutlineCanvas(container, SWT.NONE);
        outlineCanvas.setVisible(false);

        // since we don't rely on the picked node in OutlineDragHandler below
        //  we just set the camera non-pickable in order reduce performance waste
        outlineCanvas.getCamera().setPickable(false);


        // add a handler to the outline canvas to allow dragging
        outlineCanvas.addInputEventListener(new KlighdBasicInputEventHandler(new OutlineDragHandler()));


        // initialize the timer triggering the request to redraw the outline rect
        outlineRectTimer = outlineCanvas.getRoot().createTimer(REPAINT_DELAY, new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                adjustOutlineRect();
            }
        });

        outlineRectTimer.setRepeats(false);
        outlineRectTimer.start();

        // create the actual outline view elements
        setContent(topNode);
    }

    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        if (outlineCanvas == null || outlineCanvas.isDisposed()) {
            return null;
        } else {
            return outlineCanvas.getParent();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        outlineCanvas.setFocus();
    }

    /** A flag for tracking the visibility state until the {@link #outlineCanvas} is created. */
    private boolean visible = false;

    /**
     * Sets the visibility property of the employed {@link KlighdCanvas}.
     *
     * @param isVisible the visibility state
     */
    public void setVisible(final boolean isVisible) {
        visible = isVisible;
        if (outlineCanvas != null) {
            adjustOutlineRect();
            outlineCanvas.setVisible(isVisible);
        }
    }

    /**
     * Update the content of the outline page.
     *
     * @param newTopNode
     *            the {@link KNodeTopNode} to be displayed
     */
    public void setContent(final KNodeTopNode newTopNode) {
        if (outlineCanvas == null) {
            this.topNode = newTopNode;
            return;
        }

        if (topNode != null && topNode != newTopNode) {
            // detach the propertyListener from the previously observed top node
            //  this' outlineCanvas' camera from the former topNode
            if (topNode.getCameraCount() == 0) {
                throw new IllegalStateException(
                        "The PLayer passed to the PiccoloOutlineView has "
                                + "to contain at least one camera.");
            }

            this.topNode.getDiagramMainCamera().removePropertyChangeListener(propertyListener);
            this.outlineCanvas.getCamera().removeChild(this.topNode);

            if (nodeLayoutAdapter != null) {
                this.topNode.getViewModelElement().eAdapters().remove(nodeLayoutAdapter);
            }
        }

        this.topNode = newTopNode;

        final KlighdMainCamera diagramMainCamera = topNode.getDiagramMainCamera();

        // listen to view transformations
        diagramMainCamera.addPropertyChangeListener(propertyListener);

        final KlighdMainCamera camera = outlineCanvas.getCamera();

        // install the outlineCanvas' camera into the given (new) topNode
        camera.addLayer(topNode);

        // add a further layer to the camera
        //  that contains a rectangle indicating the visible part of the model
        final PLayer overlayLayer = new PLayer();
        camera.addLayer(overlayLayer);

        // initialize & configure the outline rectangle
        //  the concrete path data are set later on in #adjustOutlineRect()
        //  when the timer expires the first time (it's started in constructor)
        viewportOutlineRect = new KlighdPath();

        final Pair<RGB, Integer> viewportOutlineRectColoring = getViewportOutlineRectPaint();
        viewportOutlineRect.setLineWidth(VIEWPORT_OUTLINE_LINEWIDTH);
        viewportOutlineRect.setPaint(viewportOutlineRectColoring.getFirst());
        viewportOutlineRect.setPaintAlpha(viewportOutlineRectColoring.getSecond());
        overlayLayer.addChild(viewportOutlineRect);

        // initialize & configure the outline rectangle
        //  its size is updated by the PropertyChangeListener on the BOUNDS of 'camera'
        clipOutlineOverlay = new KlighdPath() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paint(final KlighdPaintContext kpc) {

                final KlighdSWTGraphicsEx graphics = ((KlighdSWTGraphicsEx) kpc.getKlighdGraphics());

                final Rectangle2D globalClipBounds =
                        topNode.getDiagramMainCamera().getDisplayedLayer().getGlobalFullBounds();

                final Region clip = new Region(graphics.getDevice());
                clip.add(toSWTRectangle(clipOutlineOverlay.getBoundsReference().getBounds()));

                final Rectangle diagramClip = toSWTRectangle(
                        outlineCanvas.getCamera().getViewTransformReference()
                                .transform(globalClipBounds, null).getBounds());

                clip.subtract(diagramClip);

                graphics.getGC().setClipping(clip);
                super.paint(kpc);
            }

            private Rectangle toSWTRectangle(final java.awt.Rectangle rect) {
                return new Rectangle(rect.x, rect.y, rect.width, rect.height);
            }
        };

        final Pair<RGB, Integer> clipOutlineOverlayColoring = getClipOutlineOverlayPaint();
        clipOutlineOverlay.setStrokeColor((RGB) null);
        clipOutlineOverlay.setPaint(clipOutlineOverlayColoring.getFirst());
        clipOutlineOverlay.setPaintAlpha(clipOutlineOverlayColoring.getSecond());
        clipOutlineOverlay.setVisible(diagramMainCamera.getDisplayedKNodeNode() instanceof KNodeNode);
        // ... implies to switch it off if displayedNode is a KNodeTopNode.

        camera.addChild(clipOutlineOverlay);

        camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, new PropertyChangeListener() {

            public void propertyChange(final PropertyChangeEvent evt) {
                clipOutlineOverlay.setPathToRectangle((Rectangle2D) evt.getNewValue());
            }
        });


        // add listeners to layout changes and canvas resizing
        rootNode = topNode.getViewModelElement();
        nodeLayoutAdapter = new LimitedKGraphContentAdapter(KShapeLayout.class) {

            @Override
            public void notifyChanged(final Notification notification) {
                super.notifyChanged(notification);

                if (notification.getNotifier() == rootNode) {
                    // in case anything is changed on the node, e.g. the node's shape layout
                    //  is removed or a new one is added by the simple update strategy
                    //  don't do anything!!
                    return;
                }

                final int featureId = notification.getFeatureID(KShapeLayout.class);
                if (featureId == KLayoutDataPackage.KSHAPE_LAYOUT__WIDTH
                        || featureId == KLayoutDataPackage.KSHAPE_LAYOUT__HEIGHT
                        || featureId == KLayoutDataPackage.KSHAPE_LAYOUT__XPOS
                        || featureId == KLayoutDataPackage.KSHAPE_LAYOUT__YPOS) {
                    adjustCamera();
                }
            }
        };

        rootNode.eAdapters().add(nodeLayoutAdapter);
        outlineCanvas.addControlListener(canvasResizeListener);

        adjustCamera();
        adjustOutlineRect();

        outlineCanvas.setVisible(visible);
    }

    /** the control listener reacting to canvas resizing. */
    private ControlListener canvasResizeListener = new ControlListener() {
        public void controlMoved(final ControlEvent e) {
            adjustCamera();
        }

        public void controlResized(final ControlEvent e) {
            adjustCamera();
        }
    };


    /** the minimal size of the view. */
    private static final float MIN_SIZE = 10.0f;

    /**
     * Adjust the view bounds of this' outlineCanvas' camera to the bounds of the currently tracked
     * graph layout. Note, that the bounds of the canvas are automatically updated of the size of
     * the outline page change, since the outlineCanvas is provided by this class via
     * {@link #getControl()}. The canvas in turn immediately updates the bounds of its camera, see
     * {@link edu.umd.cs.piccolox.swt.PSWTCanvas#setBounds(int, int, int, int)
     * PSWTCanvas#.setBounds(int, int, int, int)}. Thus we don't need to care about the canvas' and
     * camera's (full) bounds and can limit the modifications to the camera's view bounds (/view
     * transform).
     */
    private void adjustCamera() {
        // always reveal the current shape layout - it may be exchanged over the diagram's life time
        final KShapeLayout layoutData = rootNode.getData(KShapeLayout.class);

        final float width = Math.max(layoutData.getWidth(), MIN_SIZE);
        final float height = Math.max(layoutData.getHeight(), MIN_SIZE);
        outlineCanvas.getCamera().setViewBounds(
                new Rectangle2D.Double(layoutData.getXpos(), layoutData.getYpos(), width, height));
    }


    /**
     * Adjusts the displayed outline rectangle to the current view snippet.
     */
    private void adjustOutlineRect() {
        final KlighdMainCamera originalCamera = topNode.getDiagramMainCamera();
        if (originalCamera == null) {
            return;
        }

        final PNode displayedNode = (PNode) originalCamera.getDisplayedKNodeNode();

        // get the new bounds
        final PBounds bounds = originalCamera.getViewBounds();
        NodeUtil.localToParent(displayedNode.getParent(), topNode).transform(bounds, bounds);

        viewportOutlineRect.setPathToRoundRectangle((float) bounds.x, (float) bounds.y,
                (float) bounds.width, (float) bounds.height, VIEWPORT_OUTLINE_ROUNDNESS,
                VIEWPORT_OUTLINE_ROUNDNESS);

        // schedule a repaint
        outlineCanvas.getCamera().invalidatePaint();
    }


    private boolean disposed;

    /**
     * Getter.
     *
     * @return <code>true</code> if {@link #dispose()} has been called on this outline page
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (topNode != null) {
            this.outlineCanvas.getCamera().removeLayer(this.topNode);

            final PCamera originalCamera = topNode.getDiagramMainCamera();
            if (originalCamera != null) {
                originalCamera.removePropertyChangeListener(propertyListener);
            }
        }
        topNode = null;
        propertyListener = null;

        if (rootNode != null) {
            rootNode.eAdapters().remove(nodeLayoutAdapter);
        }
        rootNode = null;
        nodeLayoutAdapter = null;


        // the canvas, which is accessible by the platform via #getControl()
        //  is disposed separately by the platform, ...
        outlineCanvas = null;
        outlineRectTimer = null;

        // afterwards the canvasResizeListener can't be removed anymore (SWT exception)
        canvasResizeListener = null;

        disposed = true;
    }


    /**
     * {@inheritDoc}
     */
    public void setActionBars(final IActionBars actionBars) {
        // no action to register
    }


    /**
     * A drag handler that allows the user to drag the outline rectangle within the outline view and
     * propagates the movement to the actual editor part.<br>
     * <br>
     * <b>Note:</b> The {@link #outlineCanvas}' camera is set non-pickable in order to reduce
     * performance waste. However, due to {@link edu.umd.cs.piccolo.PInputManager#processInput()
     * PInputManager#processInput()} and {@link PCamera#pick(double, double, double)}
     * <code>event.getPickedNode()</code> is supposed to return the camera, which is absolutely fine :-).
     *
     * @author uru
     * @author chsch
     */
    private class OutlineDragHandler extends PDragSequenceEventHandler {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void startDrag(final PInputEvent event) {
            isDragging = true;
            super.startDrag(event);
            final Point2D pos = event.getPosition();

            final KlighdMainCamera originalCamera = topNode.getDiagramMainCamera();

            // In clipped diagrams the accumulated 'translate' offset ((x,y) positions)
            //  of the displayed inode's parent pnodes (!) must be applied the determined
            //  view bounds in order to get the "actual" click position.
            // Note however that the displayed inode's translate (x,y) must not be taken
            //  into account because it is not contained in 'originalCamera.getViewBounds()'!
            // Since PCamera.paintCameraView() calls 'fullPaint(...)' on each displayed PLayer
            //  the transforms of those layers are applied on top of the camera's view transform!
            // For that reason the global translation of
            //  'originalCamera.getDisplayedLayer().getParent()' is calculated. This way an
            //  optional translation of the parent inode's child area is also respected!
            final Point2D clipOffset =
                    originalCamera.getDisplayedLayer().getParent().getGlobalTranslation();

            final PBounds outlineRectBounds =
                    originalCamera.getViewBounds().moveBy(clipOffset.getX(), clipOffset.getY());

            // if the user clicks outside the outline rect,
            // center it on this point before dragging starts
            final boolean withinRect = outlineRectBounds.contains(pos);
            if (!withinRect) {
                // translate the camera by the delta between click
                // and current center point of the bounds
                final Point2D center = outlineRectBounds.getCenter2D();
                originalCamera.translateView(center.getX() - pos.getX(), center.getY() - pos.getY());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void drag(final PInputEvent event) {
            super.drag(event);
            final PDimension delta = event.getDelta();
            topNode.getDiagramMainCamera().translateView(-delta.getWidth(), -delta.getHeight());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void endDrag(final PInputEvent event) {
            super.endDrag(event);
            isDragging = false;
        }
    }
}

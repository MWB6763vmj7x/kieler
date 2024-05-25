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
/**
 * 
 */
package de.cau.cs.kieler.klighd.piccolo.krendering.controller;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.Bundle;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KGraphPackage;
import de.cau.cs.kieler.core.kgraph.impl.IPropertyToObjectMapImpl;
import de.cau.cs.kieler.core.krendering.KArc;
import de.cau.cs.kieler.core.krendering.KChildArea;
import de.cau.cs.kieler.core.krendering.KContainerRendering;
import de.cau.cs.kieler.core.krendering.KCustomRendering;
import de.cau.cs.kieler.core.krendering.KEllipse;
import de.cau.cs.kieler.core.krendering.KGridPlacement;
import de.cau.cs.kieler.core.krendering.KImage;
import de.cau.cs.kieler.core.krendering.KPlacement;
import de.cau.cs.kieler.core.krendering.KPlacementData;
import de.cau.cs.kieler.core.krendering.KPointPlacementData;
import de.cau.cs.kieler.core.krendering.KPolygon;
import de.cau.cs.kieler.core.krendering.KPolyline;
import de.cau.cs.kieler.core.krendering.KRectangle;
import de.cau.cs.kieler.core.krendering.KRendering;
import de.cau.cs.kieler.core.krendering.KRenderingPackage;
import de.cau.cs.kieler.core.krendering.KRenderingRef;
import de.cau.cs.kieler.core.krendering.KRoundedBendsPolyline;
import de.cau.cs.kieler.core.krendering.KRoundedRectangle;
import de.cau.cs.kieler.core.krendering.KSpline;
import de.cau.cs.kieler.core.krendering.KStyle;
import de.cau.cs.kieler.core.krendering.KText;
import de.cau.cs.kieler.core.krendering.util.KRenderingSwitch;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.IPropertyHolder;
import de.cau.cs.kieler.core.util.Pair;
import de.cau.cs.kieler.klighd.KlighdPlugin;
import de.cau.cs.kieler.klighd.krendering.GridPlacementUtil;
import de.cau.cs.kieler.klighd.krendering.KCustomRenderingWrapperFactory;
import de.cau.cs.kieler.klighd.krendering.PlacementUtil;
import de.cau.cs.kieler.klighd.krendering.PlacementUtil.Bounds;
import de.cau.cs.kieler.klighd.piccolo.krendering.IGraphElement;
import de.cau.cs.kieler.klighd.piccolo.krendering.KCustomConnectionFigureNode;
import de.cau.cs.kieler.klighd.piccolo.krendering.KDecoratorNode;
import de.cau.cs.kieler.klighd.piccolo.krendering.KEdgeNode;
import de.cau.cs.kieler.klighd.piccolo.krendering.util.PiccoloPlacementUtil;
import de.cau.cs.kieler.klighd.piccolo.krendering.util.PiccoloPlacementUtil.Decoration;
import de.cau.cs.kieler.klighd.piccolo.nodes.PAlignmentNode;
import de.cau.cs.kieler.klighd.piccolo.nodes.PAlignmentNode.HAlignment;
import de.cau.cs.kieler.klighd.piccolo.nodes.PAlignmentNode.VAlignment;
import de.cau.cs.kieler.klighd.piccolo.nodes.PEmptyNode;
import de.cau.cs.kieler.klighd.piccolo.nodes.PSWTAdvancedPath;
import de.cau.cs.kieler.klighd.piccolo.nodes.PSWTStyledText;
import de.cau.cs.kieler.klighd.piccolo.nodes.PSWTTracingText;
import de.cau.cs.kieler.klighd.piccolo.util.NodeUtil;
import de.cau.cs.kieler.klighd.piccolo.util.StyleUtil;
import de.cau.cs.kieler.klighd.piccolo.util.StyleUtil.Styles;
import de.cau.cs.kieler.klighd.util.CrossDocumentContentAdapter;
import de.cau.cs.kieler.klighd.util.ModelingUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.swt.PSWTCanvas;
import edu.umd.cs.piccolox.swt.PSWTImage;

/**
 * The abstract base class for controllers that manages the transformation of a dedicated
 * {@link KGraphElement}'s KRendering data to Piccolo nodes and the synchronization of those Piccolo
 * nodes with the KRendering specification .
 * 
 * @author mri,chsch
 * 
 * @param <S>
 *            the type of the underlying graph element
 * @param <T>
 *            the type of the Piccolo node representing the graph element
 */
public abstract class AbstractRenderingController<S extends KGraphElement, T extends IGraphElement<S>> {

    /**
     * A map that tracks the {@link PNodeController PNodeControllers} that are deployed to manage
     * PNode that represent {@link KRendering} structure over the life cycle of the diagram.<br>
     * The map is populated initializing/updating the rendering of a {@link KGraphElement}. Pairs
     * are removed when {@link KRendering} objects are removed from the KGE, see
     * {@link #installRenderingSyncAdapter()}.<br>
     * The map is cleared in when the whole node is removed and this controller is disposed, see
     * references of {@link #removeMappedEntries()}.
     */
    private final Map<KRendering, PNodeController<? extends PNode>> pnodeControllers = Maps.newHashMap();
    
    
    /**
     * This attribute key is used to let the PNodes be aware of their related KRenderings in their
     * attributes list. It is used in the KlighdActionEventHandler, for example.
     */
    public static final Object ATTR_KRENDERING = new Object();

    /** the graph element which rendering is controlled by this controller. */
    private S element;
    /** the rendering currently in use by this controller. */
    private KRendering currentRendering;

    /** the Piccolo node representing the node. */
    private T repNode;
    /** the Piccolo node representing the rendering. */
    private PNode renderingNode = null;

    /** the adapter currently installed on the rendering. */
    private CrossDocumentContentAdapter renderingDeepAdapter = null;
    
    /**
     * An adapter on the graph element that is supposed to react on changes in the 'data' field. It
     * is sensitive to additions, exchanges, and removals of top level {@link KRendering} data.
     */
    private AdapterImpl elementAdapter = null;

    /** whether to synchronize the rendering with the model. */
    private boolean syncRendering = false;

    /**
     * Constructs a rendering controller.
     * 
     * @param element
     *            the graph element for this controller
     * @param repNode
     *            the Piccolo node representing the graph element
     */
    public AbstractRenderingController(final S element, final T repNode) {
        this.element = element;
        this.repNode = repNode;
        this.repNode.setRenderingController(this);
    }

    /**
     * Returns the underlying graph element.
     * 
     * @return the graph element
     */
    public S getGraphElement() {
        return element;
    }

    /**
     * Returns the Piccolo node representing the underlying graph element.
     * 
     * @return the Piccolo node
     */
    public T getRepresentation() {
        return repNode;
    }

    /**
     * Returns the rendering currently managed by this controller.
     * 
     * @return the rendering
     */
    public KRendering getCurrentRendering() {
        return currentRendering;
    }

    /**
     * Initializes the rendering controller.
     * 
     * @param sync
     *            true if the rendering should be synchronized with the model; false else
     */
    public void initialize(final boolean sync) {
        syncRendering = sync;

        // do the initial update of the rendering
        updateRendering();
    }

    /**
     * Updates the rendering by removing the current rendering and evaluating the rendering data
     * attached to the graph element.
     */
    // Review: TODO make public/package protected
    private void updateRendering() {
        // remove the rendering adapter
        if (currentRendering != null) {
            unregisterElementAdapter();
            unregisterRenderingAdapter();
            removeMappedEntries();
        }

        // remove the rendering node
        if (renderingNode != null) {
            removeListeners(renderingNode);
            renderingNode.removeFromParent();
            renderingNode = null;
        }

        // get the current rendering
        currentRendering = element.getData(KRendering.class);

        // update the rendering
        renderingNode = internalUpdateRendering();

        // install rendering adapter if sync is enabled
        if (syncRendering) {
            // register an adapter on the element (KGE) to stay in sync
            registerElementAdapter();

            if (currentRendering != null) {
                // register an adapter on the rendering to stay in sync
                installRenderingSyncAdapter();
            }
        }
    }

    /**
     * Performs the actual update of the rendering.
     * 
     * @return the Piccolo node representing the current rendering
     */
    protected abstract PNode internalUpdateRendering();

    /**
     * Registers an adapter on the current rendering to react on changes.
     */
    private void installRenderingSyncAdapter() {
        // register adapter on the rendering to stay in sync
        renderingDeepAdapter = new CrossDocumentContentAdapter() {

            protected boolean shouldAdapt(final EStructuralFeature feature) {
                // follow the rendering feature of the KRenderingRef
                return feature.getFeatureID() == KRenderingPackage.KRENDERING_REF__RENDERING;
            }

            public void notifyChanged(final Notification msg) {
                super.notifyChanged(msg);

                // iProperties and mappings are now in the update scope but we do not need them for
                // rendering
                if (msg.getNotifier() instanceof IPropertyToObjectMapImpl
                        || msg.getNewValue() instanceof IProperty<?>
                        || msg.getOldValue() instanceof IProperty<?>
                        || msg.getNewValue() instanceof Map.Entry<?, ?>
                        || msg.getOldValue() instanceof Map.Entry<?, ?>) {
                    return;
                }
                
                Iterable<KRendering> allRemovedRenderings = Collections.emptyList();
                switch (msg.getEventType()) {
                case Notification.REMOVE_MANY:
                    final Iterable<KRendering> removedRenderings = Iterables.filter(
                            (Iterable<?>) msg.getOldValue(), KRendering.class);
                    
                    allRemovedRenderings = Iterables.concat(Iterables.transform(
                            removedRenderings, new Function<KRendering, Iterable<KRendering>>() {
                                public Iterable<KRendering> apply(final KRendering r) {
                                    return ModelingUtil.selfAndEAllContentsOfSameType(r);
                                }
                            }));
                    // there is no break by intention !
                    
                case Notification.REMOVE:
                    if (msg.getOldValue() instanceof KRendering) {
                        allRemovedRenderings = ModelingUtil
                                .selfAndEAllContentsOfSameType((KRendering) msg.getOldValue());
                    }
                    for (KRendering r : allRemovedRenderings) {
                        removeMappedProperty(null, null, r);
                    }
                    // there is no break by intention !
                    
                case Notification.UNSET:
                case Notification.SET:
                    // TODO in case of KRenderingRefs: Will the stuff above work here, too? 
                    
                    
                case Notification.MOVE:
                case Notification.ADD:
                case Notification.ADD_MANY:

                    // Attention: Don't add 'newValue == null' as this will forbid to remove
                    // styles!!

                    // handle style changes
                    if (msg.getNotifier() instanceof KStyle) {
                        updateStylesInUi();
                        return;
                    }

                    // handle new, moved and removed styles
                    // Caution: Due to multi-inheritance of the KRendering class (interface)
                    // KRenderingPackage.KRENDERING__STYLES differs from
                    // KRenderingPackage.KSTYLE_HOLDER__STYLES !!
                    if (msg.getNotifier() instanceof KRendering
                            && msg.getFeatureID(KRendering.class)
                               == KRenderingPackage.KRENDERING__STYLES) {
                        updateStylesInUi();
                        return;
                    }

                    // handle other changes by reevaluating the rendering
                    updateRenderingInUi();
                    break;
                default:
                    break;
                }
            }
        };

        // add the adapter to the rendering
        currentRendering.eAdapters().add(renderingDeepAdapter);
    }

    /**
     * Unregisters the adapter currently installed on the rendering.
     */
    private void unregisterRenderingAdapter() {
        if (currentRendering != null && renderingDeepAdapter != null) {
            currentRendering.eAdapters().remove(renderingDeepAdapter);
            renderingDeepAdapter = null;
        }
    }

    /**
     * Registers an adapter on the graph element to react on changes in its graph data feature.
     * This on is sensitive to additions, exchanges, and removals of {@link KRendering} data.
     */
    private void registerElementAdapter() {
        elementAdapter = new AdapterImpl() {
            public void notifyChanged(final Notification msg) {
                if (msg.getFeatureID(KGraphElement.class) == KGraphPackage.KGRAPH_ELEMENT__DATA) {
                    switch (msg.getEventType()) {
                    case Notification.ADD:
                    case Notification.ADD_MANY:
                    case Notification.REMOVE:
                    case Notification.REMOVE_MANY:
                        final KRendering rendering = element.getData(KRendering.class);
                        if (rendering != currentRendering) {
                            // a rendering has been added or removed
                            updateRenderingInUi();
                        }
                        break;
                    default:
                        break;
                    }
                }
            }
        };
        element.eAdapters().add(elementAdapter);
    }

    /**
     * Unregisters the adapter currently installed on the element.
     */
    private void unregisterElementAdapter() {
        if (elementAdapter != null) {
            element.eAdapters().remove(elementAdapter);
            elementAdapter = null;
        }
    }

    /**
     * A little help to reduce the syncExec calls if possible.
     * 
     * @param r
     *            the runnable to be performed in the UI context.
     */
    private static void runInUI(final Runnable r) {
        if (Display.getCurrent() != null) {
            r.run();
        } else {
            PlatformUI.getWorkbench().getDisplay().syncExec(r);
        }
    }

    private Runnable updateRenderingRunnable = new Runnable() {
        public void run() {
            updateRendering();
        }
    };

    private void updateRenderingInUi() {
        runInUI(this.updateRenderingRunnable);
    }

    private Runnable updateStylesRunnable = new Runnable() {
        public void run() {
            updateStyles();
        }
    };

    private void updateStylesInUi() {
        runInUI(this.updateStylesRunnable);
    }
    
    

    /**
     * Updates the styles of the current rendering.
     */
    private void updateStyles() {
        // update using the recursive method
        updateStyles(currentRendering, null, new ArrayList<KStyle>(0));
    }

    private void updateStyles(final KRendering rendering, final Styles styles,
            final List<KStyle> propagatedStyles) {

        PNodeController<?> controller = getMappedProperty(null, null, rendering);
        if (controller == null) {
            return;
        }

        List<KStyle> renderingStyles = rendering.getStyles();

        // determine the styles for this rendering
        final Styles newStyles = StyleUtil.deriveStyles(styles,
                determineRenderingStyles(renderingStyles, propagatedStyles));

        // apply the styles to the rendering
        applyStyles(controller, newStyles);

        if (rendering instanceof KContainerRendering) {
            // update children
            KContainerRendering container = (KContainerRendering) rendering;
            if (container.getChildren().size() > 0) {
                // determine the styles for propagation to child nodes
                final List<KStyle> childPropagatedStyles = determinePropagationStyles(
                        renderingStyles, propagatedStyles);

                // propagate to all children
                for (KRendering child : container.getChildren()) {
                    updateStyles(child, null, childPropagatedStyles);
                }
            }
        } else if (rendering instanceof KRenderingRef) {
            // update referenced rendering
            KRenderingRef renderingRef = (KRenderingRef) rendering;

            // get the referenced rendering
            KRendering referencedRendering = renderingRef.getRendering();

            // proceed recursively with the referenced rendering
            updateStyles(referencedRendering, newStyles, propagatedStyles);
        }
    }

    /**
     * Creates the Piccolo nodes for a list of renderings inside a parent Piccolo node for the given
     * placement.
     * 
     * @param children
     *            the list of children
     * @param placement
     *            the placement
     * @param styles
     *            the styles propagated to the children
     * @param parent
     *            the parent Piccolo node
     */
    protected void handleChildren(final List<KRendering> children, final KPlacement placement,
            final List<KStyle> styles, final PNode parent) {
        if (placement == null) {
            // Area Placement
            for (final KRendering rendering : children) {
                handleAreaPlacementRendering(rendering, styles, parent);
            }
        } else {
            new KRenderingSwitch<Boolean>() {
                // Grid Placement
                public Boolean caseKGridPlacement(final KGridPlacement object) {
                    handleGridPlacementRendering(object, children, styles, parent);
                    return true;
                }
            } /**/.doSwitch(placement);
        }
    }

    /**
     * Creates the Piccolo node for a rendering inside a parent Piccolo node using direct placement.
     * 
     * @param rendering
     *            the rendering
     * @param styles
     *            the styles propagated to the children
     * @param parent
     *            the parent Piccolo node
     * @return the Piccolo node representing the rendering
     */
    protected PNode handleAreaPlacementRendering(final KRendering rendering,
            final List<KStyle> styles, final PNode parent) {
        final KPlacementData pcd = rendering.getPlacementData();
        Bounds bounds = null;
        if (pcd instanceof KPointPlacementData) {
            bounds = PiccoloPlacementUtil.evaluatePointPlacement((KPointPlacementData) pcd,
                    PlacementUtil.estimateSize(rendering, new PlacementUtil.Bounds(0.0f, 0.0f)),
                    parent.getBoundsReference());
        } else {
            // determine the initial bounds
            bounds = PiccoloPlacementUtil.evaluateAreaPlacement(
                    PlacementUtil.asAreaPlacementData(rendering.getPlacementData()),
                    parent.getBoundsReference());
        }
        // create the rendering and receive its controller
        final PNodeController<?> controller = createRendering(rendering, styles, parent, bounds);

        if (pcd instanceof KPointPlacementData) {
            addListener(PNode.PROPERTY_BOUNDS, parent, controller.getNode(),
                    new PropertyChangeListener() {
                        public void propertyChange(final PropertyChangeEvent e) {
                            Bounds bounds = null;
                            bounds = PiccoloPlacementUtil.evaluatePointPlacement(
                                    (KPointPlacementData) pcd, PlacementUtil.estimateSize(
                                            rendering, new PlacementUtil.Bounds(0.0f, 0.0f)),
                                    parent.getBoundsReference());
                            // use the controller to apply the new bounds
                            controller.setBounds(bounds);
                        }
                    });
        } else {
            addListener(PNode.PROPERTY_BOUNDS, parent, controller.getNode(),
                    new PropertyChangeListener() {
                        public void propertyChange(final PropertyChangeEvent e) {
                            Bounds bounds = null;
                            // calculate the new bounds of the rendering
                            bounds = PiccoloPlacementUtil.evaluateAreaPlacement(
                                    PlacementUtil.asAreaPlacementData(rendering.getPlacementData()),
                                    parent.getBoundsReference());
                            // use the controller to apply the new bounds
                            controller.setBounds(bounds);
                        }
                    });
        }

        // add a listener on the parent's bounds

        return controller.getNode();
    }

    /**
     * Creates the Piccolo nodes for a list of renderings inside a parent Piccolo node using grid
     * placement.
     * 
     * @param gridPlacement
     *            the grid placement
     * @param renderings
     *            the renderings
     * @param styles
     *            the styles propagated to the children
     * @param parent
     *            the parent Piccolo node
     */
    protected void handleGridPlacementRendering(final KGridPlacement gridPlacement,
            final List<KRendering> renderings, final List<KStyle> styles, final PNode parent) {
        if (renderings.size() == 0) {
            return;
        }

        // calculate the bounds
        GridPlacementUtil.GridPlacer gridPlacer = GridPlacementUtil.getGridPlacementObject(
                gridPlacement, renderings);
        Bounds parentBounds = new Bounds(parent.getBoundsReference());
        Bounds[] elementBounds = gridPlacer.evaluate(parentBounds);
        // create the renderings and collect the controllers
        Bounds currentBounds;
        final PNodeController<?>[] controllers = new PNodeController<?>[renderings.size()];
        
        for (int i = 0; i < renderings.size(); i++) {
            KRendering rendering = renderings.get(i);
            currentBounds = elementBounds[i];
//            PBounds currentPBounds = new PBounds(currentBounds.getX(), currentBounds.getY(),
//                    currentBounds.getWidth(), currentBounds.getHeight());
            controllers[i] = createRendering(rendering, styles, parent, new Bounds(currentBounds));
        }

        // add a listener on the parent's bounds
        addListener(PNode.PROPERTY_BOUNDS, parent, controllers[0].getNode(),
                new PropertyChangeListener() {
                    public void propertyChange(final PropertyChangeEvent e) {
                        // calculate the new bounds of the rendering
                        GridPlacementUtil.GridPlacer gridPlacer = GridPlacementUtil
                                .getGridPlacementObject(gridPlacement, renderings);
                        Bounds newParentBounds = new Bounds(parent.getBoundsReference());
                        Bounds[] bounds = gridPlacer.evaluate(newParentBounds);
                        // use the controllers to apply the new bounds
                        int i = 0;
                        Bounds currentBounds;
                        for (PNodeController<?> controller : controllers) {
                            currentBounds = bounds[i++];
                            controller.setBounds(currentBounds);
                        }
                    }
                });
    }

    /**
     * Creates the Piccolo node for a rendering inside a parent Piccolo node representing a polyline
     * using decorator placement.
     * 
     * @param rendering
     *            the rendering
     * @param styles
     *            the styles propagated to the children
     * @param parent
     *            the parent Piccolo node representing a polyline
     * @return the Piccolo node representing the rendering
     */
    protected PNode handleDecoratorPlacementRendering(final KRendering rendering,
            final List<KStyle> styles, final PSWTAdvancedPath parent) {
        // determine the initial bounds and rotation
        final Decoration decoration = PiccoloPlacementUtil
                .evaluateDecoratorPlacement(
                        PiccoloPlacementUtil.asDecoratorPlacementData(rendering.getPlacementData()),
                        parent);

        // create an empty node for the decorator
        final KDecoratorNode decorator = new KDecoratorNode(rendering);

        // NodeUtil.applyTranslation(decorator, decoration.getOrigin());
        parent.addChild(decorator);

        // create the rendering and receive its controller
        final PNodeController<?> controller = createRendering(rendering, styles, decorator,
                decoration.getBounds());
        decorator.setRepresentationNode(controller.getNode());

        // apply the initial rotation
        decorator.setRotation(decoration.getRotation());

        // let the decorator be pickable
        decorator.setPickable(true);

        // add a listener on the parent's path
        addListener(PPath.PROPERTY_PATH, parent, controller.getNode(),
                new PropertyChangeListener() {

                    public void propertyChange(final PropertyChangeEvent e) {
                        // calculate the new bounds and rotation for the rendering
                        Decoration decoration = PiccoloPlacementUtil.evaluateDecoratorPlacement(
                                PiccoloPlacementUtil.asDecoratorPlacementData(rendering
                                        .getPlacementData()), parent);

                        // apply the new offset
                        decorator.setOffset(decoration.getOrigin().getX(), decoration.getOrigin()
                                .getY());

                        // use the controller to apply the new bounds
                        controller.setBounds(decoration.getBounds());
                        // apply the new rotation
                        decorator.setRotation(decoration.getRotation());
                    }
                });

        return controller.getNode();
    }

    /**
     * Creates the Piccolo node representing the rendering inside the given parent with initial
     * bounds.
     * 
     * @param rendering
     *            the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createRendering(final KRendering rendering,
            final List<KStyle> propagatedStyles, final PNode parent, final Bounds initialBounds) {
        List<KStyle> renderingStyles = rendering.getStyles();

        // determine the styles for this rendering
        final Styles styles = StyleUtil.deriveStyles(null,
                determineRenderingStyles(renderingStyles, propagatedStyles));

        // determine the styles for propagation to child nodes
        final List<KStyle> childPropagatedStyles = determinePropagationStyles(renderingStyles,
                propagatedStyles);

        // dispatch the rendering
        PNodeController<?> controller = createRendering(rendering, styles, childPropagatedStyles,
                parent, initialBounds);

        // set the styles for the created rendering node using the controller
        applyStyles(controller, styles);

        // remember the KRendering-controller pair in RenderingContextData attached to the KNode
        //  that is 
        setMapPropertyEntry(null, /* RenderingContextData.get(element), CONTROLLER */null, rendering,
                controller);

        controller.getNode().addAttribute(ATTR_KRENDERING, rendering);

        return controller;
    }

    /**
     * Creates the Piccolo node representing the rendering inside the given parent with initial
     * bounds and given styles.
     * 
     * @param rendering
     *            the rendering
     * @param styles
     *            the styles for the rendering
     * @param childPropagatedStyles
     *            the style propagated to the renderings children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createRendering(final KRendering rendering, final Styles styles,
            final List<KStyle> childPropagatedStyles, final PNode parent,
            final Bounds initialBounds) {
        // create the rendering and return its controller
        PNodeController<?> controller = new KRenderingSwitch<PNodeController<?>>() {
            // Ellipse
            public PNodeController<?> caseKEllipse(final KEllipse ellipse) {
                return createEllipse(ellipse, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Rectangle
            public PNodeController<?> caseKRectangle(final KRectangle rect) {
                return createRectangle(rect, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Rounded Rectangle
            public PNodeController<?> caseKRoundedRectangle(final KRoundedRectangle rect) {
                return createRoundedRectangle(rect, styles, childPropagatedStyles, parent,
                        initialBounds);
            }

            // Arc
            public PNodeController<?> caseKArc(final KArc arc) {
                return createArc(arc, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Spline
            public PNodeController<?> caseKSpline(final KSpline spline) {
                return createLine(spline, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Polyline
            public PNodeController<?> caseKPolyline(final KPolyline polyline) {
                return createLine(polyline, styles, childPropagatedStyles, parent, initialBounds);
            }

            // RoundedBendPolyline
            public PNodeController<?> caseKRoundedBendsPolyline(final KRoundedBendsPolyline polyline) {
                return createLine(polyline, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Polygon
            public PNodeController<?> caseKPolygon(final KPolygon polygon) {
                return createPolygon(polygon, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Text
            public PNodeController<?> caseKText(final KText text) {
                return createText(text, styles, childPropagatedStyles, parent, initialBounds);
            };

            // Rendering Reference
            public PNodeController<?> caseKRenderingRef(final KRenderingRef renderingReference) {
                return createRenderingReference(renderingReference, styles, childPropagatedStyles,
                        parent, initialBounds);
            }

            // Image
            public PNodeController<?> caseKImage(final KImage object) {
                return createImage(object, styles, childPropagatedStyles, parent, initialBounds);
            }

            // Custom Rendering
            public PNodeController<?> caseKCustomRendering(final KCustomRendering rendering) {
                return createCustomRendering(rendering, styles, childPropagatedStyles, parent,
                        initialBounds);
            }

            // Child Area
            public PNodeController<?> caseKChildArea(final KChildArea childArea) {
                return createChildArea(parent, initialBounds);
            }

        } /**/.doSwitch(rendering);
        return controller;
    }

    /**
     * Creates a {@code PSWTAdvancedPath} representation for the {@code KEllipse}.
     * 
     * @param ellipse
     *            the ellipse rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTAdvancedPath> createEllipse(final KEllipse ellipse,
            final Styles styles, final List<KStyle> propagatedStyles, final PNode parent,
            final Bounds initialBounds) {
        // create the ellipse
        final PSWTAdvancedPath path = PSWTAdvancedPath.createEllipse(0, 0,
                initialBounds.getWidth(), initialBounds.getHeight());
        initializeRenderingNode(path);
        path.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(path);

        // handle children
        if (ellipse.getChildren().size() > 0) {
            handleChildren(ellipse.getChildren(), ellipse.getChildPlacement(), propagatedStyles,
                    path);
        }

        // return a controller for the ellipse
        return new PSWTAdvancedPathController(path) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                getNode().setPathToEllipse(0, 0, bounds.getWidth(), bounds.getHeight());
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Creates a {@code PSWTAdvancedPath} representation for the {@code KRectangle}.
     * 
     * @param rect
     *            the rectangle rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTAdvancedPath> createRectangle(final KRectangle rect,
            final Styles styles, final List<KStyle> propagatedStyles, final PNode parent,
            final Bounds initialBounds) {
        // create the rectangle
        final PSWTAdvancedPath path = PSWTAdvancedPath.createRectangle(0, 0,
                initialBounds.getWidth(), initialBounds.getHeight());
        initializeRenderingNode(path);
        path.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(path);

        // handle children
        if (rect.getChildren().size() > 0) {
            handleChildren(rect.getChildren(), rect.getChildPlacement(), propagatedStyles, path);
        }

        // create a controller for the rectangle and return it
        return new PSWTAdvancedPathController(path) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                getNode().setPathToRectangle(0, 0, bounds.getWidth(), bounds.getHeight());
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Creates a {@code PSWTAdvancedPath} representation for the {@code KRoundedRectangle}.
     * 
     * @param rect
     *            the rounded rectangle rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTAdvancedPath> createRoundedRectangle(
            final KRoundedRectangle rect, final Styles styles, final List<KStyle> propagatedStyles,
            final PNode parent, final Bounds initialBounds) {
        final float cornerWidth = 2 * rect.getCornerWidth();
        final float cornerHeight = 2 * rect.getCornerHeight();
        
        // create the rounded rectangle
        final PSWTAdvancedPath path = PSWTAdvancedPath.createRoundRectangle(0, 0,
                initialBounds.getWidth(), initialBounds.getHeight(), cornerWidth, cornerHeight);
        initializeRenderingNode(path);
        path.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(path);

        // handle children
        if (rect.getChildren().size() > 0) {
            handleChildren(rect.getChildren(), rect.getChildPlacement(), propagatedStyles, path);
        }

        // create a controller for the rounded rectangle and return it
        return new PSWTAdvancedPathController(path) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                getNode().setPathToRoundRectangle(0, 0, bounds.getWidth(), bounds.getHeight(),
                        cornerWidth, cornerHeight);
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Creates a {@code PSWTAdvancedPath} representation for the {@code KArc}.
     * 
     * @param arc
     *            the arc rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTAdvancedPath> createArc(final KArc arc, final Styles styles,
            final List<KStyle> propagatedStyles, final PNode parent, final Bounds initialBounds) {
        // create the rounded rectangle
        final PSWTAdvancedPath path = PSWTAdvancedPath.createArc(0, 0, initialBounds.getWidth(),
                initialBounds.getHeight(), arc.getStartAngle(), arc.getArcAngle());
        path.setPaint((RGB) null);
        initializeRenderingNode(path);
        path.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(path);

        // handle children
        if (arc.getChildren().size() > 0) {
            handleChildren(arc.getChildren(), arc.getChildPlacement(), propagatedStyles, path);
        }

        // create a controller for the rounded rectangle and return it
        return new PSWTAdvancedPathController(path) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                getNode().setPathToRoundRectangle(0, 0, bounds.getWidth(), bounds.getHeight(),
                        arc.getStartAngle(), arc.getArcAngle());
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Creates a {@code PSWTText} representation for the {@code KText}.
     * 
     * @param text
     *            the text rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTStyledText> createText(final KText text, final Styles styles,
            final List<KStyle> propagatedStyles, final PNode parent, final Bounds initialBounds) {
        // create the text
        PSWTTracingText textNode = new PSWTTracingText(text);
        textNode.setGreekColor(null);
        textNode.setTransparent(true); // supplement due to KIELER-2155
        initializeRenderingNode(textNode);

        // supplement (chsch)
        textNode.setPickable(true);

        // create the alignment node wrapping the text
        final PAlignmentNode alignmentNode = new PAlignmentNode();
        initializeRenderingNode(alignmentNode);
        alignmentNode.translate(initialBounds.getX(), initialBounds.getY());
        alignmentNode.setBounds(0, 0, initialBounds.getWidth(), initialBounds.getHeight());
        alignmentNode.addChild(textNode);
        alignmentNode.setHorizontalAlignment(textNode, HAlignment.CENTER);
        alignmentNode.setVerticalAlignment(textNode, VAlignment.CENTER);
        parent.addChild(alignmentNode);

        // create a controller for the text and return it
        return new PSWTTextController(textNode) {
            public void setBounds(final Bounds bounds) {
                NodeUtil.applySmartBounds(alignmentNode, bounds);
            }

            public void setHorizontalAlignment(final HAlignment alignment) {
                alignmentNode.setHorizontalAlignment(getNode(), alignment);
            }

            public void setVerticalAlignment(final VAlignment alignment) {
                alignmentNode.setVerticalAlignment(getNode(), alignment);
            }
        };
    }

    /**
     * Creates a {@code PSWTAdvancedPath} representation for the {@code KPolyline} or
     * {@code KSpline}.
     * 
     * @param line
     *            the polyline or spline rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTAdvancedPath> createLine(final KPolyline line,
            final Styles styles, final List<KStyle> propagatedStyles, final PNode parent,
            final Bounds initialBounds) {

        Point2D[] points = PiccoloPlacementUtil.evaluatePolylinePlacement(line, initialBounds);

        final PSWTAdvancedPath path;
        if (line instanceof KSpline) {
            // create the spline
            path = PSWTAdvancedPath.createSpline(points);
        } else if (line instanceof KRoundedBendsPolyline) {
            // create the rounded bends polyline
            path = PSWTAdvancedPath.createRoundedBendPolyline(points,
                    ((KRoundedBendsPolyline) line).getBendRadius());
        } else {
            // create the polyline
            path = PSWTAdvancedPath.createPolyline(points);
        }

        initializeRenderingNode(path);
        path.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(path);

        // handle children
        if (line.getChildren().size() > 0) {
            List<KRendering> restChildren = Lists.newLinkedList();
            for (final KRendering rendering : line.getChildren()) {
                if (PiccoloPlacementUtil.asDecoratorPlacementData(rendering.getPlacementData()) != null)
                {
                    handleDecoratorPlacementRendering(rendering, propagatedStyles, path);
                } else {
                    restChildren.add(rendering);
                }
            }

            // handle children without decorator placement data if any
            if (restChildren.size() > 0) {
                // create a proxy parent for the children without decorator placement data
                final PNode proxyParent = new PEmptyNode();
                path.addChild(proxyParent);
                NodeUtil.applySmartBounds(proxyParent, path.getBoundsReference());
                addListener(PNode.PROPERTY_BOUNDS, path, proxyParent, new PropertyChangeListener() {
                    public void propertyChange(final PropertyChangeEvent arg0) {
                        NodeUtil.applySmartBounds(proxyParent, path.getBoundsReference());
                    }
                });

                handleChildren(restChildren, line.getChildPlacement(), propagatedStyles,
                        proxyParent);
            }
        }

        // create a controller for the polyline and return it
        return new PSWTAdvancedPathController(path) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds

                Point2D[] points = PiccoloPlacementUtil.evaluatePolylinePlacement(line, bounds);

                if (line instanceof KSpline) {
                    // update spline
                    getNode().setPathToSpline(points);
                } else if (line instanceof KRoundedBendsPolyline) {
                    // update rounded bend polyline
                    getNode().setPathToRoundedBendPolyline(points,
                            ((KRoundedBendsPolyline) line).getBendRadius());
                } else {
                    // update polyline
                    getNode().setPathToPolyline(points);
                }

                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Creates a {@code PSWTAdvancedPath} representation for the {@code KPolygon}.
     * 
     * @param polygon
     *            the polygon rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<PSWTAdvancedPath> createPolygon(final KPolygon polygon,
            final Styles styles, final List<KStyle> propagatedStyles, final PNode parent,
            final Bounds initialBounds) {
        // create the polygon
        final PSWTAdvancedPath path = PSWTAdvancedPath.createPolygon(PiccoloPlacementUtil
                .evaluatePolylinePlacement(polygon, initialBounds));
        initializeRenderingNode(path);
        path.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(path);

        // handle children
        if (polygon.getChildren().size() > 0) {
            List<KRendering> restChildren = Lists.newLinkedList();
            for (final KRendering rendering : polygon.getChildren()) {
                if (PiccoloPlacementUtil.asDecoratorPlacementData(rendering.getPlacementData()) != null)
                {
                    handleDecoratorPlacementRendering(rendering, propagatedStyles, path);
                } else {
                    restChildren.add(rendering);
                }
            }

            // handle children without decorator placement data if any
            if (restChildren.size() > 0) {
                // create a proxy parent for the children without decorator placement data
                final PNode proxyParent = new PEmptyNode();
                path.addChild(proxyParent);
                NodeUtil.applySmartBounds(proxyParent, path.getBoundsReference());
                addListener(PNode.PROPERTY_BOUNDS, path, proxyParent, new PropertyChangeListener() {
                    public void propertyChange(final PropertyChangeEvent arg0) {
                        NodeUtil.applySmartBounds(proxyParent, path.getBoundsReference());
                    }
                });

                handleChildren(restChildren, polygon.getChildPlacement(), propagatedStyles,
                        proxyParent);
            }
        }

        // create a controller for the polyline and return it
        return new PSWTAdvancedPathController(path) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                getNode().setPathToPolygon(
                        (PiccoloPlacementUtil.evaluatePolylinePlacement(polygon, bounds)));
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Creates a representation for the {@code KRenderingRef}.
     * 
     * @param renderingReference
     *            the rendering reference
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createRenderingReference(final KRenderingRef renderingReference,
            final Styles styles, final List<KStyle> propagatedStyles, final PNode parent,
            final Bounds initialBounds) {

        KRendering rendering = renderingReference.getRendering();
        if (rendering == null) {
            // create a dummy node
            return createDummy(parent, initialBounds);
        }

        List<KStyle> renderingStyles = rendering.getStyles();

        // determine the styles for this rendering
        final Styles refStyles = StyleUtil.deriveStyles(styles, renderingStyles);

        // determine the styles for propagation to child nodes
        final List<KStyle> childPropagatedStyles = determinePropagationStyles(renderingStyles,
                propagatedStyles);

        // create a key for this reference
//        Object refKey = null; // new Object();
//        setMappedProperty(renderingReference, KEY, key, refKey);

        // dispatch the rendering
        final PNodeController<?> controller = createRendering(rendering, refStyles,
                childPropagatedStyles, parent, initialBounds);

        // set the styles for the created rendering node using the controller
        applyStyles(controller, refStyles);

        // remember the controller in the rendering
//        setMappedProperty(rendering, CONTROLLER, refKey, controller);

        // return a controller for the reference which sets the bounds of the referenced node
        return new PNodeController<PNode>(controller.getNode()) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                controller.setBounds(bounds);
            }
        };
    }

    /**
     * Creates a representation for the {@link KImage}.
     * 
     * @author uru, chsch
     * 
     * @param image
     *            the image rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createImage(final KImage image, final Styles styles,
            final List<KStyle> propagatedStyles, final PNode parent, final Bounds initialBounds) {

        PSWTImage pImage = null;

        if (image.getImageObject() instanceof Image) {

            pImage = new PSWTImage(PSWTCanvas.CURRENT_CANVAS, (Image) image.getImageObject());

        } else {

            // get the bundle and actual image, trim the leading and trailing quotation marks
            Bundle bundle = Platform.getBundle(image.getBundleName().replace("\"", ""));
            if (bundle == null) {
                return createDummy(parent, initialBounds);
            }

            URL entry = bundle.getEntry(image.getImagePath().replace("\"", ""));

            try {
                // create the image
                // the bounds of pImage are set within the PSWTImage implementation
                pImage = new PSWTImage(PSWTCanvas.CURRENT_CANVAS, entry.openStream());
            } catch (IOException e) {
                final String msg = "KLighD: Error occurred while loading the image "
                        + image.getImagePath() + " in bundle " + image.getBundleName();
                StatusManager.getManager().handle(
                        new Status(IStatus.ERROR, KlighdPlugin.PLUGIN_ID, msg, e),
                        StatusManager.LOG);
                return createDummy(parent, initialBounds);
            }
        }

        // initialize the node
        initializeRenderingNode(pImage);
        pImage.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(pImage);

        // handle children
        if (image.getChildren().size() > 0) {
            handleChildren(image.getChildren(), image.getChildPlacement(), propagatedStyles,
                    pImage);
        }

        // create a standard default node controller
        return new PNodeController<PNode>(pImage) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };

    }

    /**
     * Creates a representation for the {@code KCustomRendering}.
     * 
     * @param customRendering
     *            the custom rendering
     * @param styles
     *            the styles container for the rendering
     * @param propagatedStyles
     *            the styles propagated to the rendering's children
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createCustomRendering(final KCustomRendering customRendering,
            final Styles styles, final List<KStyle> propagatedStyles, final PNode parent,
            final Bounds initialBounds) {

        // get a wrapping PNode containing the actual figure
        // by means of the KCustomRenderingWrapperFactory
        PNode node;
        if (customRendering.getFigureObject() != null) {
            if (parent instanceof KEdgeNode) {
                node = KCustomRenderingWrapperFactory.getInstance().getWrapperInstance(
                        customRendering.getFigureObject(), KCustomConnectionFigureNode.class);
            } else {
                node = KCustomRenderingWrapperFactory.getInstance().getWrapperInstance(
                        customRendering.getFigureObject(), PNode.class);
            }

        } else {
            if (parent instanceof KEdgeNode) {
                node = KCustomRenderingWrapperFactory.getInstance().getWrapperInstance(
                        customRendering.getBundleName(), customRendering.getClassName(),
                        KCustomConnectionFigureNode.class);
            } else {
                node = KCustomRenderingWrapperFactory.getInstance().getWrapperInstance(
                        customRendering.getBundleName(), customRendering.getClassName(),
                        PNode.class);
            }
        }
        if (node == null) {
            return createDummy(parent, initialBounds);
        }
        // initialize the bounds of the node
        node.setBounds(0, 0, initialBounds.getWidth(), initialBounds.getWidth());

        // initialize the node
        initializeRenderingNode(node);
        node.translate(initialBounds.getX(), initialBounds.getY());
        parent.addChild(node);

        // handle children
        if (customRendering.getChildren().size() > 0) {
            handleChildren(customRendering.getChildren(), customRendering.getChildPlacement(),
                    propagatedStyles, node);
        }

        // create a standard default node controller
        return new PNodeController<PNode>(node) {
            public void setBounds(final Bounds bounds) {
                // apply the bounds
                getNode().setBounds(0, 0, bounds.getWidth(), bounds.getWidth());
                NodeUtil.applyTranslation(getNode(), bounds.getX(), bounds.getY());
            }
        };
    }

    /**
     * Configures the Piccolo node for the given {@code KChildArea}.
     * 
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createChildArea(final PNode parent, final Bounds initialBounds) {
        throw new RuntimeException(
                "Child area found in graph element which does not support a child area: " + element);
    }

    /**
     * Creates a dummy node.
     * 
     * @param parent
     *            the parent Piccolo node
     * @param initialBounds
     *            the initial bounds
     * @return the controller for the created Piccolo node
     */
    protected PNodeController<?> createDummy(final PNode parent, final Bounds initialBounds) {
        final PNode dummyChild = new PEmptyNode();
        NodeUtil.applySmartBounds(dummyChild, initialBounds);
        parent.addChild(dummyChild);
        return new PNodeController<PNode>(dummyChild) {
            public void setBounds(final Bounds bounds) {
                NodeUtil.applySmartBounds(dummyChild, bounds);
            }
        };
    }

    /**
     * Sets default values for the given Piccolo node used as representation for a rendering.
     * 
     * @param node
     *            the Piccolo node
     */
    protected void initializeRenderingNode(final PNode node) {
        node.setVisible(true);
        node.setPickable(false);
    }

    private static final Object PROPERTY_LISTENER_KEY = new Object();

    /**
     * Adds a listener for a child node on a parent node.
     * 
     * @param property
     *            the property to register the listener on
     * @param parent
     *            the parent node
     * @param node
     *            the child node
     * @param listener
     *            the listener
     */
    protected void addListener(final String property, final PNode parent, final PNode node,
            final PropertyChangeListener listener) {
        parent.addPropertyChangeListener(property, listener);
        @SuppressWarnings("unchecked")
        List<Pair<String, PropertyChangeListener>> listeners
          = (List<Pair<String, PropertyChangeListener>>) node.getAttribute(PROPERTY_LISTENER_KEY);
        if (listeners == null) {
            listeners = Lists.newLinkedList();
            node.addAttribute(PROPERTY_LISTENER_KEY, listeners);
        }
        listeners.add(new Pair<String, PropertyChangeListener>(property, listener));
    }

    /**
     * Removes a node as listener from its parent.
     * 
     * @param node
     *            the child node
     */
    protected void removeListeners(final PNode node) {
        @SuppressWarnings("unchecked")
        List<Pair<String, PropertyChangeListener>> listeners
          = (List<Pair<String, PropertyChangeListener>>) node.getAttribute(PROPERTY_LISTENER_KEY);
        if (listeners != null && node.getParent() != null) {
            for (Pair<String, PropertyChangeListener> pair : listeners) {
                node.getParent().removePropertyChangeListener(pair.getFirst(), pair.getSecond());
            }
        }
    }

    /**
     * Returns the list of styles for the rendering with the given rendering styles and propagated
     * styles.
     * 
     * @param renderingStyles
     *            the rendering styles
     * @param propagatedStyles
     *            the propagated styles
     * @return the list of styles for the rendering
     */
    protected List<KStyle> determineRenderingStyles(final List<KStyle> renderingStyles,
            final List<KStyle> propagatedStyles) {
        List<KStyle> combinedStyles = Lists.newLinkedList();
        combinedStyles.addAll(propagatedStyles);
        combinedStyles.addAll(renderingStyles);
        return combinedStyles;
    }

    /**
     * Returns the list of styles propagated to children of the rendering with the given rendering
     * styles and propagated styles.
     * 
     * @param renderingStyles
     *            the rendering styles
     * @param propagatedStyles
     *            the propagated styles
     * @return the list of styles for propagation to the children of the rendering
     */
    protected List<KStyle> determinePropagationStyles(final List<KStyle> renderingStyles,
            final List<KStyle> propagatedStyles) {
        List<KStyle> propagationStyles = Lists.newLinkedList();
        propagationStyles.addAll(propagatedStyles);
        for (KStyle style : renderingStyles) {
            if (style.isPropagateToChildren()) {
                propagationStyles.add(style);
            }
        }
        return propagationStyles;
    }

    /**
     * Applies the styles to the node associated with the given node controller.
     * 
     * @param controller
     *            the node controller
     * @param styles
     *            the styles
     */
    protected void applyStyles(final PNodeController<?> controller, final Styles styles) {

        controller.applyChanges(styles);
    }

//    *  This
//    * information is stored in the {@link #CONTROLLER} property that is attached to a
//    * {@link RenderingContextData} object, which in turn is attached to the related
//    * {@link de.cau.cs.kieler.core.kgraph.KNode KNode}.
    /**
     * Sets a value for a key in the given map {@link IProperty IProperty&lt;Map&lt;?,?&gt;&gt;},
     * that is attached to the given property holder.<br>
     * Here, it is used to memorize {@link PNodeController PNodeControllers} that are in charge of
     * controlling the {@link PNode PNodes} representing {@link KRendering} definitions.
     * 
     * @param propertyHolder
     *            the property holder
     * @param property
     *            the property
     * @param key
     *            the key
     * @param value
     *            the value
     * @param <R>
     *            the value-type of the map
     */
    protected <R> void setMapPropertyEntry(final IPropertyHolder propertyHolder,
            final IProperty<Map<KRendering, PNodeController<?>>> property, final KRendering key,
            final PNodeController<?> value) {
        
        this.pnodeControllers.put(key, value);
        
//        Map<KRendering, PNodeController<?>> map = propertyHolder.getProperty(property);
//        if (map == null) {
//            map = Maps.newHashMap();
//            propertyHolder.setProperty(property, map);
//        }
//        map.put(key, value);
//
        // track this mapping
//        List<Pair<IPropertyHolder, Object>> mappedPropertyList = mappedProperties.get(property);
//        if (mappedPropertyList == null) {
//            mappedPropertyList = Lists.newLinkedList();
//            mappedProperties.put(property, mappedPropertyList);
//        }
//        mappedPropertyList.add(new Pair<IPropertyHolder, Object>(propertyHolder, key));
    }

    /**
     * Returns a value for a key in a given property holder using a specified property for a map
     * type.
     * 
     * @param propertyHolder
     *            the property holder
     * @param property
     *            the property
     * @param key
     *            the key
     * @return the value
     */
//    * @param <R>
//    *            the value-type of the map
//    protected <R> R getMappedProperty(final IPropertyHolder propertyHolder,
    protected PNodeController<?> getMappedProperty(final IPropertyHolder propertyHolder,
            final IProperty<Map<KRendering, PNodeController<?>>> property, final KRendering key) {
        
        return this.pnodeControllers.get(key);
//        Map<KRendering, PNodeController<?>> map = propertyHolder.getProperty(property);
//        if (map != null) {
//            return map.get(key);
//        }
//        return null;
    }

    /**
     * Removes the key in a given property holder using a specified property for a map.
     * 
     * @param propertyHolder
     *            the property holder
     * @param property
     *            the property
     * @param key
     *            the key
     * @param <R>
     *            the value-type of the map
     */
    private <R> void removeMappedProperty(final IPropertyHolder propertyHolder,
            final IProperty<Map<Object, Object>> property, final KRendering key) {
        this.pnodeControllers.remove(key);
//        Map<Object, Object> map = propertyHolder.getProperty(property);
//        if (map != null) {
//            map.remove(key);
//            if (map.isEmpty()) {
//                propertyHolder.setProperty(property, null);
//            }
//        }
    }
    
    /**
     * Release all mapping information in order to let unused objects be garbage collected.
     */
    public void removeMappedEntries() {
        this.pnodeControllers.clear();
    }
    
//    /**
//     * Removes all mapped properties used in this controller from the associated property holders.
//     * 
//     * @param property
//     *            the property
//     * @param <R>
//     *            the value-type of the map
//     */
//    private void removeMappedProperties(final IProperty<Map<Object, Object>> property) {
//        List<Pair<IPropertyHolder, Object>> mappedPropertyList = mappedProperties.get(property);
//        if (mappedPropertyList != null) {
////            for (Pair<IPropertyHolder, Object> pair : mappedPropertyList) {
////                removeMappedProperty(pair.getFirst(), property, pair.getSecond());
////            }
//            mappedProperties.remove(property);
//        }
//    }
//
//    private void removeMappedProperties2(final IProperty<Map<KRendering, PNodeController<?>>>
//              property) {
//        List<Pair<IPropertyHolder, Object>> mappedPropertyList = mappedProperties.get(property);
//        if (mappedPropertyList != null) {
////            for (Pair<IPropertyHolder, Object> pair : mappedPropertyList) {
////                removeMappedProperty(pair.getFirst(), property, pair.getSecond());
////            }
//            mappedProperties.remove(property);
//        }
//    }
}

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
package de.cau.cs.kieler.klighd;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KGraphPackage;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.krendering.KRendering;
import de.cau.cs.kieler.core.krendering.Trigger;
import de.cau.cs.kieler.kiml.config.ILayoutConfig;
import de.cau.cs.kieler.klighd.util.ModelingUtil;
import de.cau.cs.kieler.klighd.viewers.ContextViewer;

/**
 * Common interface of classes providing implementations of
 * {@link de.cau.cs.kieler.core.krendering.KAction KActions}.<br>
 * <br>
 * {@link IAction IActions} are currently assumed to be stateless and, thus, a singleton instance is
 * created only.
 *
 * @author chsch
 *
 * @kieler.design proposed by chsch
 * @kieler.rating proposed yellow by chsch
 */
public interface IAction {

    /**
     * This method performs the intended action on a diagram.
     *
     * @param context
     *            an {@link ActionContext} instance providing various useful data.
     * @return an {@link ActionResult} providing {@link ILayoutConfig layout config(s)} to be
     *         incorporated while updating the layout after the action has been performed, as well
     *         as configurations of 'animateLayout', 'zoomToFit', etc.<br>
     *         Use {@link ActionResult#createResult(boolean, ILayoutConfig...)} for creation.
     */
    ActionResult execute(ActionContext context);


    /**
     * This class comprises various useful data required for performing things in diagrams.
     *
     * @author chsch
     */
    public static class ActionContext {

        private IViewer viewer = null;
        private Trigger trigger = null;
        private KGraphElement kgraphElement = null;
        private KRendering rendering = null;

        /**
         * Standard constructor.
         *
         * @param v
         *            the viewer the action was triggered in
         * @param t
         *            the trigger type that fired
         * @param kge
         *            the {@link KGraphElement} the action is invoked on
         * @param r
         *            the rendering the action is invoked on
         */
        public ActionContext(final IViewer v, final Trigger t, final KGraphElement kge,
                final KRendering r) {
            this.viewer = v;
            this.trigger = t;
            this.kgraphElement = kge;
            this.rendering = r;
        }

        /**
         * @return the current actual diagram viewer, usually the PiccoloViewer
         */
        public IViewer getActiveViewer() {
            return viewer;
        }

        /**
         * @return the current {@link ContextViewer}
         */
        public ContextViewer getContextViewer() {
            return viewer.getContextViewer();
        }

        /**
         * @return the view context related to the current diagram
         */
        public ViewContext getViewContext() {
            return viewer.getViewContext();
        }

        /**
         * @return the trigger
         */
        public Trigger getTrigger() {
            return trigger;
        }

        /**
         * @return the node
         */
        public KGraphElement getKGraphElement() {
            if (kgraphElement == null) {
                kgraphElement = (KGraphElement) ModelingUtil.eContainerOfType(rendering,
                        KGraphPackage.eINSTANCE.getKGraphElement());
            }
            return kgraphElement;
        }

        /**
         * @return the node
         */
        public KNode getKNode() {
            return (KNode) ModelingUtil.eContainerOrSelfOfType(getKGraphElement(),
                    KGraphPackage.eINSTANCE.getKNode());
        }

        /**
         * @return the rendering
         */
        public KRendering getKRendering() {
            return rendering;
        }


        /**
         * Reveals the source (domain) element associated with the given <code>node</code>
         * if any exists, or <code>null</code>.
         *
         * @param <T> the expected return type
         * @param viewElement the {@link KNode} to get the associated source element for
         * @return the related source (domain) element if any associated one exists, or <code>null</code>
         */
        @SuppressWarnings("unchecked")
        public <T> T getDomainElement(final KNode viewElement) {
            return (T) this.viewer.getViewContext().getSourceElement(viewElement);
        }
    }


    /**
     * Return type being expected by implementations of {@link IAction#execute(ActionContext)}.
     * Besides {@link ILayoutConfig ILayoutConfigs}
     *
     * @author chsch
     */
    public static final class ActionResult {

        private static final IPreferenceStore STORE = KlighdPlugin.getDefault().getPreferenceStore();

        private List<ILayoutConfig> layoutConfigs = null;

        private boolean actionPerformed = true;
        private Boolean animateLayout = null;
        private Boolean zoomToActualSize = null;
        private Boolean zoomToFit = null;
        private Boolean zoomToFocus = null;
        private KNode focus = null;

        private ActionResult(final boolean theActionPerformed) {
            this.actionPerformed = theActionPerformed;
        }

        private ActionResult(final boolean theActionPerformed, final List<ILayoutConfig> theConfigs) {
            this.actionPerformed = theActionPerformed;
            this.layoutConfigs = theConfigs;
        }

        /**
         * Creates a new {@link ActionResult} instance .
         *
         * @param actionRequiresLayout
         *            flag indicating whether the action performed changes on the diagram requiring
         *            a subsequent layout re-computation
         * @param config
         *            some additional {@link ILayoutConfig ILayoutConfigs} to by incorporated,
         *            will be ignored if {@code actionRequiresLayout == false}
         * @return the requested {@link ActionResult}
         */
        public static ActionResult createResult(final boolean actionRequiresLayout,
                final ILayoutConfig... config) {
            if (actionRequiresLayout) {
                return new ActionResult(actionRequiresLayout, Arrays.asList(config));
            } else {
                return new ActionResult(actionRequiresLayout).dontZoom();
            }
        }

        /**
         * Schedule animation during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult doAnimateLayout() {
            this.animateLayout = true;
            return this;
        }

        /**
         * Suppress animation during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult dontAnimateLayout() {
            this.animateLayout = false;
            return this;
        }

        /**
         * Schedule zoomToActualSize during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult doZoomToActualSize() {
            this.zoomToActualSize = true;
            this.zoomToFit = null;
            this.zoomToFocus = null;
            return this;
        }

        /**
         * Suppress zoomToActualSize during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult dontZoomToActualSize() {
            this.zoomToActualSize = false;
            return this;
        }

        /**
         * Schedule zoomToFit during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult doZoomToFit() {
            this.zoomToActualSize = null;
            this.zoomToFit = true;
            this.zoomToFocus = null;
            return this;
        }

        /**
         * Suppress zoomToFit during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult dontZoomToFit() {
            this.zoomToFit = false;
            return this;
        }

        /**
         * Schedule zoomToFocus during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult doZoomToFocus() {
            this.zoomToActualSize = null;
            this.zoomToFit = null;
            this.zoomToFocus = true;
            return this;
        }

        /**
         * Schedule zoomToFocus during the subsequent automatic layout run.
         *
         * @param focusNode
         *            the {@link KNode} to focus
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult doZoomToFocus(final KNode focusNode) {
            this.zoomToActualSize = null;
            this.zoomToFit = null;
            this.zoomToFocus = true;
            this.focus = focusNode;
            return this;
        }

        /**
         * Suppress zoomToFocus during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult dontZoomToFocus() {
            this.zoomToFocus = false;
            return this;
        }

        /**
         * Suppress any zooming during the subsequent automatic layout run.
         *
         * @return <code>this</code> {@link ActionResult}
         */
        public ActionResult dontZoom() {
            this.zoomToActualSize = false;
            this.zoomToFit = false;
            this.zoomToFocus = false;
            return this;
        }

        /**
         * Getter.
         *
         * @return the attached {@link ILayoutConfig}
         */
        public List<ILayoutConfig> getLayoutConfigs() {
            return this.layoutConfigs;
        }

        /**
         * Getter.
         *
         * Getter. Denotes whether a subsequent layout update run is required.
         * @return the {@link #actionPerformed} flag
         */
        public boolean getActionPerformed() {
            return this.actionPerformed;
        }

        /**
         * Getter.
         *
         * @return the {@link #animateLayout} flag
         */
        public boolean getAnimateLayout() {
            return this.animateLayout != null
                    ? this.animateLayout : STORE.getBoolean(KlighdPreferences.ANIMATE_LAYOUT);
        }

        /**
         * Getter. Returns a {@link Boolean} instead of the primitive <code>boolean</code>
         * in order distinguish the 'not configured' state. Returns <code>null</code> in this
         * case.
         *
         * @return the {@link #zoomToActualSize} state
         */
        public Boolean getZoomToActualSize() {
            return this.zoomToActualSize;
        }

        /**
         * Getter. Returns a {@link Boolean} instead of the primitive <code>boolean</code>
         * in order distinguish the 'not configured' state. Returns <code>null</code> in this
         * case.
         *
         * @return the {@link #zoomToFit} state
         */
        public Boolean getZoomToFit() {
            return this.zoomToFit;
        }

        /**
         * Getter. Returns a {@link Boolean} instead of the primitive <code>boolean</code>
         * in order distinguish the 'not configured' state. Returns <code>null</code> in this
         * case.
         *
         * @return the {@link #zoomToFocus} state
         */
        public Boolean getZoomToFocus() {
            return this.zoomToFocus;
        }

        /**
         * Getter.
         *
         * @return the {@link KNode} to focus on
         */
        public KNode getFocusNode() {
            return focus;
        }
    }
}

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
package de.cau.cs.kieler.klighd.piccolo.internal.events;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.krendering.KAction;
import de.cau.cs.kieler.core.krendering.KRendering;
import de.cau.cs.kieler.kiml.config.ILayoutConfig;
import de.cau.cs.kieler.klighd.IAction;
import de.cau.cs.kieler.klighd.IAction.ActionContext;
import de.cau.cs.kieler.klighd.IAction.ActionResult;
import de.cau.cs.kieler.klighd.KlighdDataManager;
import de.cau.cs.kieler.klighd.KlighdPlugin;
import de.cau.cs.kieler.klighd.LightDiagramServices;
import de.cau.cs.kieler.klighd.ViewContext;
import de.cau.cs.kieler.klighd.ZoomStyle;
import de.cau.cs.kieler.klighd.internal.IKlighdTrigger;
import de.cau.cs.kieler.klighd.piccolo.internal.controller.AbstractKGERenderingController;
import de.cau.cs.kieler.klighd.piccolo.internal.events.KlighdMouseEventListener.KlighdMouseEvent;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KNodeAbstractNode;
import de.cau.cs.kieler.klighd.piccolo.internal.nodes.KNodeTopNode;
import de.cau.cs.kieler.klighd.piccolo.viewer.PiccoloViewer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * Event handler that invokes actions associated with KRenderings on corresponding mouse click events.
 *
 * @author chsch
 */
public class KlighdActionEventHandler implements PInputEventListener {

    private PiccoloViewer viewer = null;

    /**
     * Constructor.
     *
     * @param theViewer
     *            the {@link PiccoloViewer} it is attached to
     */
    public KlighdActionEventHandler(final PiccoloViewer theViewer) {
        this.viewer = theViewer;
    }

    /**
     * The well-formedness criterion of {@link KAction KActions} that is used to filter
     * the actions to be examined in {@link #processEvent(PInputEvent, int)}.
     */
    private static final Predicate<KAction> WELLFORMED = new Predicate<KAction>() {
        public boolean apply(final KAction action) {
            return action.getTrigger() != null && !Strings.isNullOrEmpty(action.getActionId());
        }
    };


    /**
     * {@inheritDoc}
     */
    public void processEvent(final PInputEvent inputEvent, final int eventType) {
        // SUPPRESS CHECKSTYLE PREVIOUS MethodLength -- don't bother me,
        //  there's lots of documentation!

        // CAUTION: parts of this method and parts of
        //  KlighdActionExecutionHandler.execute(...) (klighd.ui) are symmetric,
        // In case of changes make sure to update both!

        // caution: don't modify the evaluation of the 'handled' flag in an ad-hoc way,
        //  first make sure that the scenario described below is not enabled again.
        if (inputEvent.isHandled()) {
            return;
        }

        if (this.viewer.isMagnificationLensVisible()) {
            return;
        }

        if (!(inputEvent.getSourceSwingEvent() instanceof KlighdMouseEvent)) {
            return;
        }

        final KlighdMouseEvent me = (KlighdMouseEvent) inputEvent.getSourceSwingEvent();

        if (me.getEventType() == SWT.MouseMove) {
            return;
        }

        final PNode pickedNode = inputEvent.getPickedNode();

        KRendering rendering =
                (KRendering) pickedNode.getAttribute(AbstractKGERenderingController.ATTR_KRENDERING);

        if (rendering == null) {
            // in case no KRendering has been found ...

            // ... check whether a KNode's representative has been picked,
            //  which happens if a click or double click occurred on the canvas, for example
            if (pickedNode instanceof KNodeAbstractNode) {
                final KNodeAbstractNode iNode = (KNodeAbstractNode) pickedNode;

                // if so test whether the diagram's top node has been picked ...
                if (pickedNode instanceof KNodeTopNode) {
                    // and if so reveal the represented KNode and look for a dummy KRendering element
                    //  that might contain KActions
                    rendering = iNode.getViewModelElement().getData(KRendering.class);

                } else {
                    // Otherwise we assume that a nested KNode's representative has been picked,
                    //  which may happen if the diagram has been clipped to that particular KNode.

                    // in that case ask the associated KGE rendering controller for the currently
                    //  displayed KRendering
                    rendering = iNode.getRenderingController().getCurrentRenderingReference();
                }

                if (rendering == null) {
                    return;
                }
            } else {
                return;
            }
        }

        ActionContext context = null; // construct the context lazily when it is required
        ActionResult resultOfLastAction = null;
        ActionResult resultOfLastActionRequiringLayout = null;

        // this flag is used to track the execution of actions requiring a layout update
        boolean anyActionRequiresLayout = false;

        for (final KAction action : Iterables.filter(rendering.getActions(), WELLFORMED)) {
            if (!action.getTrigger().equals(me.getTrigger()) || !guardsMatch(action, me)) {
                continue;
            }

            final IAction actionImpl =
                    KlighdDataManager.getInstance().getActionById(action.getActionId());
            if (actionImpl == null) {
                continue;
            }

            if (context == null) {
                context = new ActionContext(this.viewer, action.getTrigger(), null, rendering);
            }

            if (!anyActionRequiresLayout) {
                // in order to enable animated movements of diagram elements due to view model changes,
                //  the viewer must be informed to record view model changes before executing any action
                viewer.startRecording();

                // the related 'stopRecording(...)' will be performed below in case no layout update is
                //  required, and after the layout application, respectively
            }

            resultOfLastAction = actionImpl.execute(context);

            if (resultOfLastAction == null) {
                viewer.stopRecording(ZoomStyle.NONE, null, 0);

                final String msg = "KLighD action event handler: Execution of " + actionImpl.getClass()
                        + " returned 'null', expected an IAction.ActionResult.";
                throw new IllegalResultException(msg);
            }

            final boolean actionRequiresLayout = resultOfLastAction.getActionPerformed();
            if (actionRequiresLayout) {
                anyActionRequiresLayout = true;
                resultOfLastActionRequiringLayout = resultOfLastAction;
            }
        }

        if (resultOfLastAction == null) {
            // ... indicating that no action has been executed at all
            // skip any layout and zoom update, do not tag 'inputEvent' to be handled, and ...
            return;
        }

        // otherwise 'resultOfLastAction' is a valid ActionResult determine the requested zoom style ...

        // caution: don't modify the evaluation of the 'handled' flag in an ad-hoc way,
        //  first make sure that the scenario described below is not enabled again.
        inputEvent.setHandled(true);

        final ViewContext vc = viewer.getViewContext();
        if (!anyActionRequiresLayout) {
            // ... i.e. no action requires layout (and 'resultOfLastActionRequiringLayout == null'),
            // skip the layout update and stop recording, and finish here

            // in case 'resultOfLastAction' was created via ActionResult.create(false) without any
            //  zooming requests the resulting zoomStyle will be ZoomStyle.NONE,
            //  see implementation of ActionResult.create(...)
            vc.getLayoutRecorder().stopRecording(ZoomStyle.create(resultOfLastAction, vc),
                    resultOfLastAction.getFocusNode(), 0);

            return;
        }

        final boolean animate = resultOfLastAction.getAnimateLayout();
        final ZoomStyle zoomStyle = ZoomStyle.create(resultOfLastActionRequiringLayout, vc);
        final KNode focusNode = resultOfLastActionRequiringLayout.getFocusNode();
        final List<ILayoutConfig> layoutConfigs = resultOfLastAction.getLayoutConfigs();

        // Execute the layout asynchronously in order to let the KLighdInputManager
        //  finish the processing of 'inputEvent' quickly.
        // Otherwise if the diagram layout engine interrupts its work by calling
        //  Display.readAndDispatch() and, with that, the control flow executing this method
        //  the processing of 'inputEvent' by the input manager might get triggered a
        //  second time by some timer event causing a kind of nested/recursive (!) evaluation
        //  of 'inputEvent' and, thereby, this method.
        // In addition, this scenario is tried to avoid by setting & evaluating the 'handled'
        //  flag of 'inputEvent' properly.
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                LightDiagramServices.layoutDiagram(vc, animate, zoomStyle, focusNode, layoutConfigs);
            }
        });

        KlighdPlugin.getTrigger().triggerStatus(IKlighdTrigger.Status.UPDATE, viewer.getViewContext());
    }

    private boolean guardsMatch(final KAction action, final KlighdMouseEvent event) {
        return (!action.isAltPressed() || event.isAltDown())
                && (!action.isCtrlCmdPressed() || event.isControlDown())
                && (!action.isShiftPressed() || event.isShiftDown());
    }


    /**
     * A dedicated exception indicating an illegal result of a method.<br>
     * It is currently thrown if implementations of {@link IAction#execute(ActionContext)} returns
     * <code>null</code>.
     *
     * @author chsch
     */
    public class IllegalResultException extends RuntimeException {

        private static final long serialVersionUID = -5838587904577606037L;

        /**
         * Constructor.
         *
         * @param msg
         *            the detail message. The detail message is saved for later retrieval by the
         *            {@link #getMessage()} method.
         */
        public IllegalResultException(final String msg) {
            super(msg);
        }
    }
}

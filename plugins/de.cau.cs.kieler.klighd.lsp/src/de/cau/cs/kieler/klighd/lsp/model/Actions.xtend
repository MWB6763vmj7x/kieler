/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2018, 2020 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package de.cau.cs.kieler.klighd.lsp.model

import de.cau.cs.kieler.klighd.krendering.KImage
import java.util.List
import java.util.function.Consumer
import org.eclipse.sprotty.Action
import org.eclipse.sprotty.ElementAndBounds
import org.eclipse.sprotty.RequestAction
import org.eclipse.sprotty.ResponseAction
import org.eclipse.sprotty.SModelRoot
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.annotations.EqualsHashCode
import org.eclipse.xtend.lib.annotations.ToString
import org.eclipse.sprotty.UpdateModelAction

/**
 * Sent from the server to the client to request bounds for the given texts. The texts are rendered
 * invisibly so the bounds can derived from the DOM. The response is a {@link ComputedTextBoundsAction}.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class RequestTextBoundsAction implements RequestAction<ComputedTextBoundsAction> {
    public static val KIND = 'requestTextBounds'
    String kind = KIND
    
    SModelRoot textDiagram
    String requestId
    
    new() {}
    new(Consumer<RequestTextBoundsAction> initializer) {
        initializer.accept(this)
    }
    
    /**
     * Constructor to call when creating this. The {@code textDiagram} should contain a sprotty Diagram with all texts,
     * whose bounds should be requested.
     */
    new(SModelRoot textDiagram) {
        this.textDiagram = textDiagram
    }
}

/**
 * Sent from the server to the client to send a list of all available syntheses for the current model.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class SetSynthesesAction implements Action {
    public static val KIND = 'setSyntheses'
    String kind = KIND
    
    List<SetSynthesesActionData> syntheses
    
     new(List<SetSynthesesActionData> syntheses) {
        this.syntheses = syntheses
    }
}

/**
 * Sent from the server to the client to check if the {@link KImage}s provided in the message are cached or if they need
 * to be sent to the client again.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class CheckImagesAction implements Action {
    public static val KIND = 'checkImages'
    String kind = KIND
    
    List<KImage> images
    
    new(List<KImage> images) {
        this.images = images
    }
}

/**
 * Sent from the server to the client to store images in base64 format needed for rendering on the client.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class StoreImagesAction implements Action {
    public static val KIND = 'storeImages'
    String kind = KIND
    
    List<Pair<String, String>> images
    
    new(List<Pair<String, String>> images) {
        this.images = images
    }
}

/**
 * Sent from the client to the server to inform it whether images need to be sent to the client before accepting the next diagram.
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class CheckedImagesAction implements Action {
    public static val KIND = 'checkedImages'
    String kind = KIND
    
    List<String> notCached

    new() {}
    new(Consumer<CheckedImagesAction> initializer) {
        initializer.accept(this)
    }
}

/**
 * Sent from the client to the server to request a new diagram with the given synthesis.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class SetSynthesisAction implements Action {
    public static val KIND = 'setSynthesis'
    String kind = KIND
    
    String id
    
    new() {}
    new(Consumer<SetSynthesisAction> initializer) {
        initializer.accept(this)
    }
}

/**
 * Sent from the client to the server to transmit the result of text bounds computation as a response
 * to a {@link RequestTextBoundsAction}.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class ComputedTextBoundsAction implements ResponseAction {
    public static val KIND = 'computedTextBounds'
    String kind = KIND
    int revision
    
    List<ElementAndBounds> bounds
    String responseId
    
    new() {}
    new(Consumer<ComputedTextBoundsAction> initializer) {
        initializer.accept(this)
    }
}

/**
 * Sent from the client to the server to request a KlighD action to be performed on the current model.
 * 
 * @author nre
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
class PerformActionAction implements Action {
    public static val KIND = 'performAction'
    String kind = KIND
    
    String actionId
    String kGraphElementId
    String kRenderingId
    
    new() {}
    new(Consumer<PerformActionAction> initializer) {
        initializer.accept(this)
    }
}

/**
 * Refreshes the diagram.
 * 
 * @author sdo
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
public class RefreshDiagramAction implements Action {
    public static val KIND = 'refreshDiagram'
    String kind = KIND
    
    new() {}
    new(Consumer<RefreshDiagramAction> initializer) {
        initializer.accept(this)
    }
}

/**
 * Refreshes the layout.
 * 
 * @author sdo
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
public class RefreshLayoutAction implements Action {
    public static val KIND = 'refreshLayout'
    String kind = KIND
    
    new() {}
    new(Consumer<RefreshLayoutAction> initializer) {
        initializer.accept(this)
    }
}

/**
 * Updates the model and sends the cause to the client.
 * Extends to UpdateModelAction.
 * FIXME Remove this if the UpdateModelAction includes a cause.
 * 
 * @author sdo
 */
@Accessors
@EqualsHashCode
@ToString(skipNulls = true)
public class KeithUpdateModelAction extends UpdateModelAction {
    public static val KIND = 'updateModel'
    String kind = KIND
    
    
    SModelRoot newRoot
    Boolean animate
    Action cause
    
    new() {}
    new(Consumer<KeithUpdateModelAction> initializer) {
        initializer.accept(this)
    }
    
    new(SModelRoot newRoot, Action cause) {
        this.newRoot = newRoot
        this.cause = cause
    }
}
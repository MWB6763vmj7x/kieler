/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.klighd.piccolo.krendering;

import de.cau.cs.kieler.core.kgraph.KGraphElement;

/**
 * The interface for Piccolo nodes representing a {@code KGraphElement}.
 * 
 * Info (chsch): Since the introduction of {@link ITracingElement} this interface is basically
 *   obsolete by now. It is still kept as it may be helpful during the implementation of other
 *   intended use cases.
 * 
 * @author mri
 * 
 * @param <T>
 *            the type of the graph element
 */
public interface IGraphElement<T extends KGraphElement> extends ITracingElement<T> {

    /**
     * Returns the graph element represented by this node.
     * 
     * @return the graph element
     */
    T getGraphElement();
    
}

/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://rtsys.informatik.uni-kiel.de/kieler
 * 
 * Copyright 2018-2019 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 */
package de.cau.cs.kieler.klighd.lsp.model

import de.cau.cs.kieler.klighd.kgraph.KGraphData
import java.util.List
import org.eclipse.elk.core.math.KVectorChain
import org.eclipse.sprotty.SEdge
import org.eclipse.sprotty.SGraph
import org.eclipse.sprotty.SLabel
import org.eclipse.sprotty.SNode
import org.eclipse.sprotty.SPort
import org.eclipse.xtend.lib.annotations.Accessors

/**
 * Sprotty node with additional fields needed by the translation from a
 * {@link KNode KNode}.
 */
@Accessors
public class SKNode extends SNode {
    private List<KGraphData> data
}

/**
 * Sprotty label with additional fields needed by the translation from a
 * {@link KLabel KLabel}.
 */
@Accessors
public class SKLabel extends SLabel {
    private List<KGraphData> data
}

/**
 * Sprotty edge with additional fields needed by the translation from a
 * {@link KEdge KEdge}.
 */
@Accessors
public class SKEdge extends SEdge {
    private List<KGraphData> data
    private KVectorChain junctionPoints
}

/**
 * Sprotty port with additional fields needed by the translation from a
 * {@link KPort KPort}.
 */
@Accessors
public class SKPort extends SPort {
    private List<KGraphData> data
}

/**
 * Sprotty graph with additional fields needed by the translation from a parent 
 * {@link KNode KNode}.
 */
@Accessors
public class SKGraph extends SGraph {
    private List<KGraphData> data
}
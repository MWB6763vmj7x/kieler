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
package de.cau.cs.kieler.klighd.piccolo.internal.activities;

import de.cau.cs.kieler.klighd.piccolo.util.NodeUtil;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A custom {@link edu.umd.cs.piccolo.activities.PInterpolatingActivity PInterpolatingActivity} that
 * fades {@link PNode PNodes} representing {@link de.cau.cs.kieler.core.kgraph.KGraphElement
 * KGraphElements} (except KEdges) into a diagram.
 * 
 * @author chsch
 */
public class FadeNodeInActivity extends PInterpolatingActivity implements IStartingAndFinishingActivity {

    /** the node for this activity. */
    private final PNode node;
    
    /** the target bounds. */
    private PBounds targetBounds;
    

    /**
     * Constructs an activity that immediately applies the bounds to a Piccolo node and fades it in
     * over a duration.
     * 
     * @param node
     *            the Piccolo node
     * @param bounds
     *            the bounds
     * @param duration
     *            the duration
     */
    public FadeNodeInActivity(final PNode node, final PBounds bounds,
            final long duration) {
        super(duration);
        this.node = node;
        this.targetBounds = bounds;
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * This customization initializes the new node's position, set it transparent, and releases the
     * invisibility.
     */
    public void activityStarted() {
        NodeUtil.applySmartBounds(node, targetBounds);
        node.setTransparency(0);
        node.setVisible(true);
        super.activityStarted();
    }

    /**
     * {@inheritDoc}<br>
     * <br>
     * This customization exposes the given node according to the value of 'zeroToOne'.
     */
    @Override
    public void setRelativeTargetValue(final float zeroToOne) {        
        node.setTransparency(zeroToOne);
        super.setRelativeTargetValue(zeroToOne);
    }
    
    /**
     * {@inheritDoc}<br>
     * <br>
     * This customization fully exposes the given node.
     */
    public void activityFinished() {
        node.setTransparency(1);
        super.activityFinished();
    }
}

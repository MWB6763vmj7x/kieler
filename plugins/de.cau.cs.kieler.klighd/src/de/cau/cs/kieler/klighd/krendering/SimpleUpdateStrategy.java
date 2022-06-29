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
package de.cau.cs.kieler.klighd.krendering;

import java.util.List;

import com.google.common.collect.Lists;

import de.cau.cs.kieler.core.kgraph.KGraphData;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.krendering.KRendering;
import de.cau.cs.kieler.core.krendering.KRenderingLibrary;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import de.cau.cs.kieler.klighd.IUpdateStrategy;
import de.cau.cs.kieler.klighd.ViewContext;

/**
 * A simple update strategy for KGraph with KRendering which merges by copying the new model.<br>
 * Uses a {@link DuplicatingTransformation} in order to decouple the given model and the depicted
 * one for use with the textual KGraph editor and adds a related {@link TransformationContext} to
 * the used view context for ensuring the model-image-traceability.
 *
 * @author mri
 */
public class SimpleUpdateStrategy implements IUpdateStrategy<KNode> {

    /** the priority for this update strategy. */
    public static final int PRIORITY = 0;

    /**
     * {@inheritDoc}
     */
    public int getPriority() {
        return PRIORITY;
    }

    /**
     * {@inheritDoc}
     */
    public KNode getInitialBaseModel(final ViewContext viewContext) {
        KNode baseModel = KimlUtil.createInitializedNode();
        return baseModel;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final KNode baseModel, final KNode newModel, final ViewContext viewContext) {

        List<KNode> children = Lists.newArrayList(newModel.getChildren());
        List<KGraphData> data = Lists.newArrayList(newModel.getData());
        newModel.getChildren().clear();
        newModel.getData().clear();
        List<KGraphData> removeData = Lists.newLinkedList();
        for (KGraphData graphData : baseModel.getData()) {
            if (graphData instanceof KRendering || graphData instanceof KRenderingLibrary
                    || graphData instanceof KShapeLayout) {
                removeData.add(graphData);
            }
        }
        baseModel.getData().removeAll(removeData);
        baseModel.getChildren().clear();
        baseModel.getData().addAll(data);
        baseModel.getChildren().addAll(children);
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getModelClass() {
        return KNode.class;
    }

}

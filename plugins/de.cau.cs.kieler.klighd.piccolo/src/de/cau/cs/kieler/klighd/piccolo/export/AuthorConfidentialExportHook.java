/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 *
 * Copyright 2014 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 *
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.klighd.piccolo.export;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import de.cau.cs.kieler.klighd.DiagramExportConfig;
import de.cau.cs.kieler.klighd.KlighdConstants;
import de.cau.cs.kieler.klighd.ViewContext;
import de.cau.cs.kieler.klighd.microlayout.PlacementUtil;
import de.cau.cs.kieler.klighd.piccolo.KlighdSWTGraphics;

/**
 * Example export hook. Draws a border around the diagram, prints the author and a "Confidential"
 * watermark.
 *
 * Registration is to be done in plugin.xml like
 * <pre>
 * {@code
 * <extension
 *       point="de.cau.cs.kieler.klighd.piccolo.exportHooks">
 *    <exportHook
 *          class="de.cau.cs.kieler.klighd.piccolo.export.AuthorConfidentialExportHook"
 *          id="de.cau.cs.kieler.klighd.piccolo.export.AuthorConfidentialExportHook"
 *          supportedFormats="bmp, jpeg, png, svg, pdf, printout">
 *    </exportHook>
 * </extension>}
 * </pre>
 *
 * @author csp
 */
public class AuthorConfidentialExportHook extends AbstractExportBranding {

//    private static final int BORDER_LINE_WIDTH = 1;
    private static final int BOTTOM_MARGIN = 30;
    private static final int TOP_LEFT_RIGHT_MARGIN = 20;
//    private static final int BORDER_PADDING = 5;
    private static final int WATERMARK_ALPHA = 100;
    private static final double WATERMARK_PADDING_FACTOR = 0.8;

    // CHECKSTYLEOFF MagicNumber

    /**
     * {@inheritDoc}
     */
    @Override
    public void setViewContext(final ViewContext viewContext) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Trim getDiagramTrim(final Rectangle2D bounds) {
        return null; //new TrimData(100, 100, 200, 200);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Trim getDiagramTileTrimm(final Rectangle2D bounds, final boolean fixSizedTiles) {
        if (bounds.getWidth() > 1000 && bounds.getHeight() > 1000) {
            return new Trim(500, 500, 200, 200);
        } else {
            return new Trim(50, 50, 20, 20);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawDiagramBackground(final KlighdSWTGraphics graphics,
            final DiagramExportConfig config) {
//        graphics.setFillColor(KlighdConstants.RED);
//
//        graphics.fill(new Rectangle2D.Double(0, 0, 100, bounds.getHeight()));
//        graphics.fill(new Rectangle2D.Double(bounds.getWidth() - 100, 0, 100, bounds.getHeight()));
//
//        final Path2D p = new Path2D.Float();
//        p.moveTo(0, 0);
//        p.lineTo(bounds.getWidth(), 0);
//        p.lineTo(bounds.getWidth() - 100, 200);
//        p.lineTo(100, 200);
//        p.closePath();
//
//        graphics.setFillColor(KlighdConstants.BLUE);
//
//        graphics.fill(p);
//
//        graphics.transform(AffineTransform.getRotateInstance(Math.toRadians(180),
//                bounds.getCenterX(), bounds.getCenterY()));
//
//        graphics.fill(p);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawDiagramTileBackground(final KlighdSWTGraphics graphics,
            final DiagramExportConfig config) {
        graphics.setFillColor(KlighdConstants.YELLOW);
        if (config.tileBounds.getWidth() > 1000 && config.tileBounds.getHeight() > 1000) {
            graphics.fill(new Rectangle2D.Double(500, 200,
                    config.tileBounds.getWidth() - 1000,
                    config.tileBounds.getHeight() - 400));
        } else {
            graphics.fill(new Rectangle2D.Double(50, 20,
                    config.tileBounds.getWidth() - 100,
                    config.tileBounds.getHeight() - 40));
        }
//
//        graphics.setFillColor(KlighdConstants.GREEN);
//        graphics.setAlpha(100);
//        graphics.fill(new Rectangle2D.Double(10, 10, bounds.getWidth() - 20, bounds.getHeight() - 20));
//        graphics.setAlpha(KlighdConstants.ALPHA_FULL_OPAQUE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawDiagramOverlay(final KlighdSWTGraphics graphics, final DiagramExportConfig config) {
//
//        final double innerWidth = bounds.getWidth() - (2 * TOP_LEFT_RIGHT_MARGIN);
//        final double innerHeight = bounds.getHeight() - (TOP_LEFT_RIGHT_MARGIN + BOTTOM_MARGIN);
//
//        // make border
//        graphics.setLineAttributes(new LineAttributes(BORDER_LINE_WIDTH));
//        graphics.setStrokeColor(KlighdConstants.BLACK);
//        graphics.draw(new Rectangle2D.Double(
//                TOP_LEFT_RIGHT_MARGIN - BORDER_PADDING, TOP_LEFT_RIGHT_MARGIN - BORDER_PADDING,
//                innerWidth + 2 * BORDER_PADDING, innerHeight + 2 * BORDER_PADDING));
//
//        // draw author
//        graphics.transform(AffineTransform.getTranslateInstance(TOP_LEFT_RIGHT_MARGIN,
//                bounds.getHeight() - TOP_LEFT_RIGHT_MARGIN));
//        graphics.setFont(new FontData("Arial", KlighdConstants.DEFAULT_FONT_SIZE,
//                KlighdConstants.DEFAULT_FONT_STYLE_SWT));
//        graphics.drawText("Author: Max Mustermann");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawDiagramTileOverlay(final KlighdSWTGraphics graphics,
            final DiagramExportConfig config) {
        // draw confidential

        final Rectangle2D bounds = config.tileBounds;

        final double innerWidth = bounds.getWidth(); // - (2 * TOP_LEFT_RIGHT_MARGIN);
        final double innerHeight = bounds.getHeight(); // - (TOP_LEFT_RIGHT_MARGIN + BOTTOM_MARGIN);

        // font
        final FontData font = new FontData("Arial", KlighdConstants.DEFAULT_FONT_SIZE, SWT.BOLD);
        graphics.setFont(font);
        graphics.setAlpha(WATERMARK_ALPHA);

        // size & scale
        final String confidential = "Confidential";
        final Rectangle size =
                PlacementUtil.estimateTextSize(font, confidential).setBoundsOf(new Rectangle());

        final double scale = Math.sqrt(Math.pow(innerWidth, 2) + Math.pow(innerHeight, 2))
                / size.width * WATERMARK_PADDING_FACTOR;

        size.width *= scale;
        size.height *= scale;

        // transformations
        graphics.transform(AffineTransform.getTranslateInstance(
                bounds.getWidth() / 2d - size.width / 2d, bounds.getHeight() / 2d - size.height / 2d));
        graphics.transform(AffineTransform.getRotateInstance(
                innerWidth, -innerHeight, size.width / 2d, size.height / 2d));

        graphics.draw(size);

        graphics.transform(AffineTransform.getScaleInstance(scale, scale));
        graphics.drawText(confidential);
    }
}

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
package de.cau.cs.kieler.klighd;

import java.awt.geom.Rectangle2D;

/**
 * Interface of diagram printout/export customizers allowing to add frames, author information, and
 * confidentiality remarks to diagram printouts and exports.<br>
 * <b>Note:</b> Implementations should not directly implement this interface but subclass
 * {@link de.cau.cs.kieler.klighd.piccolo.export.AbstractExportBranding} situated in the plug-in
 * {@code de.cau.cs.kieler.klighd.piccolo}.
 *
 * @author csp
 * @author chsch
 */
public interface IExportBranding {

    /**
     * A record being used for comprising the trim data of a single diagram tile or whole diagram
     * drawings. In case of diagram printouts the printable area of a single page is reduced by
     * these data in order to preserve space for headers and footers. Besides, these data are used
     * to extend the size of images in order to create space for overall headers and footers on
     * image exports and printouts.
     */
    public final class Trim {

        // SUPPRESS CHECKSTYLE NEXT 5 Visibility|Javadoc -- these are final record data

        public final float left;
        public final float right;
        public final float top;
        public final float bottom;

        /**
         * Constructor.
         *
         * @param left required width on the left side
         * @param right required width on the right side
         * @param top required height on the top side
         * @param bottom required height on the bottom side
         */
        public Trim(final float left, final float right, final float top, final float bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        /**
         * Returns the sum of the {@link #left} and {@link #right}.
         *
         * @return the sum of the {@link #left} and {@link #right}
         */
        public float getWidth() {
            return Trim.this.left + Trim.this.right;
        }

        /**
         * Returns the sum of the {@link #top} and {@link #bottom}.
         *
         * @return the sum of the {@link #top} and {@link #bottom}
         */
        public float getHeight() {
            return Trim.this.top + Trim.this.bottom;
        }

        /**
         * Returns a copy of {@code this} {@link Trim} instance with all values being multiplied
         * with {@code scale}.
         *
         * @param scale
         *            the scale factor to apply
         * @return the desired {@link Trim}
         */
        public Trim getScaled(final float scale) {
            return new Trim(
                    scale * this.left, scale * this.right, scale * this.top, scale * this.bottom);
        }
    }


    /**
     * Setter being called by the runtime after initialization.
     *
     * @param viewContext
     *            the {@link ViewContext} providing access to the diagram' view & source model
     */
    void setViewContext(ViewContext viewContext);

    /**
     * Tests whether the implementing {@link IExportBranding} is enabled for the current diagram
     * export operation. This decision can be made based on the corresponding {@link ViewContext}
     * that is provided view {@link #setViewContext(ViewContext)} before calling this method.
     *
     * @return {@code true} if this {@link IExportBranding} shall be applied while exporting a
     *         diagram, and {@code false} if it must not be applied.
     */
    boolean isEnabled();

    /**
     * Provides {@link Trim} denoting additional space required on each side of the whole
     * diagram.<br>
     * Note: In case multiple {@link IExportBranding}s are employed the side-wise maximum of the
     * provided {@link Trim} is applied.
     *
     *
     * @param bounds
     *            the size of overall (scaled) diagram
     *
     * @return the required {@link Trim}
     */
    Trim getDiagramTrim(final Rectangle2D bounds);

    /**
     * Provides {@link Trim} denoting (additional) space required on each side of each diagram
     * tile. In case of (tiled) printouts or image exports on fixed size tiles the drawable area of
     * each tile is reduced by the amount of the returned trim data.<br>
     * Note: In case multiple {@link IExportBranding}s are employed the side-wise maximum of the
     * provided {@link Trim} is applied.
     *
     * @param bounds
     *            the absolute size of
     * @param fixSizedTiles
     *            if {@code true} the returned {@link Trim} will reduce the area being available
     *            for drawing, otherwise the tile is increased by the provided {@link Trim}
     *
     * @return the required {@link Trim}
     */
    Trim getDiagramTileTrimm(final Rectangle2D bounds, final boolean fixSizedTiles);

    /**
     * This function allows to contribute diagram-wide background drawings, like water marks for
     * example. It is called before the diagram is drawn. The bounds have the form
     * <code>(0, 0, width, height)</code> with width and height denoting the overall size of the
     * drawn diagram including the additionally required {@link Trim}.<br>
     *
     * @param graphics
     *            the graphics to draw on
     * @param config
     *            an {@link DiagramExportConfig} record containing all required data
     */
    void drawDiagramBackground(Object graphics, DiagramExportConfig config);

    /**
     * This function allows to contribute tile background drawings, like water marks for example.
     * It is called before the diagram is drawn. The bounds have the form
     * <code>(0, 0, width, height)</code> with width and height denoting the overall size of the
     * diagram tile including additionally required {@link Trim}.<br>
     *
     * @param graphics
     *            the graphics to draw on
     * @param config
     *            an {@link DiagramExportConfig} record containing all required data
     */
    void drawDiagramTileBackground(Object graphics, DiagramExportConfig config);

    /**
     * This function allows to contribute diagram-wide overlay drawings, like water marks, authoring
     * information, or symbol explanations for example. It is called before the diagram is drawn.
     * The bounds have the form <code>(0, 0, width, height)</code> with width and height denoting
     * the overall size of the drawn diagram including the additionally required {@link Trim}.<br>
     *
     * @param graphics
     *            the graphics to draw on
     * @param config
     *            an {@link DiagramExportConfig} record containing all required data
     */
    void drawDiagramOverlay(Object graphics, DiagramExportConfig config);

    /**
     * This function allows to contribute tile overlay drawings, like water marks for example or
     * <i>Confidential</i> brandings. It is called before the diagram is drawn. The bounds have the
     * form <code>(0, 0, width, height)</code> with width and height denoting the overall size of
     * the diagram tile including additionally required {@link Trim}.<br>
     *
     * @param graphics
     *            the graphics to draw on
     * @param config
     *            an {@link DiagramExportConfig} record containing all required data
     */
    void drawDiagramTileOverlay(Object graphics, DiagramExportConfig config);
}
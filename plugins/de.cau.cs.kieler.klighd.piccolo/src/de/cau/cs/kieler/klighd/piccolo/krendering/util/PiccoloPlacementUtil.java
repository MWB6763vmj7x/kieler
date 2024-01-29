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
package de.cau.cs.kieler.klighd.piccolo.krendering.util;

import java.awt.geom.Point2D;

import de.cau.cs.kieler.core.krendering.KBottomPosition;
import de.cau.cs.kieler.core.krendering.KDecoratorPlacementData;
import de.cau.cs.kieler.core.krendering.KAreaPlacementData;
import de.cau.cs.kieler.core.krendering.KLeftPosition;
import de.cau.cs.kieler.core.krendering.KPlacementData;
import de.cau.cs.kieler.core.krendering.KPointPlacementData;
import de.cau.cs.kieler.core.krendering.KPolyline;
import de.cau.cs.kieler.core.krendering.KPosition;
import de.cau.cs.kieler.core.krendering.KRendering;
import de.cau.cs.kieler.core.krendering.KRightPosition;
import de.cau.cs.kieler.core.krendering.KTopPosition;
import de.cau.cs.kieler.core.krendering.KXPosition;
import de.cau.cs.kieler.core.krendering.KYPosition;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.Property;
import de.cau.cs.kieler.core.util.Pair;
import de.cau.cs.kieler.klighd.krendering.PlacementUtil.Bounds;
import de.cau.cs.kieler.klighd.piccolo.nodes.PSWTAdvancedPath;
import de.cau.cs.kieler.klighd.piccolo.util.MathUtil;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * A utility class for evaluating the placement of KRenderings.
 * 
 * @author mri
 */
public final class PiccoloPlacementUtil {

    /**
     * A private constructor to prevent instantiation.
     */
    private PiccoloPlacementUtil() {
        // do nothing
    }

    /**
     * Returns the bounds for a direct placement data in given parent bounds.
     * 
     * @param dpd
     *            the direct placement data
     * @param parentBounds
     *            the parent bounds
     * @return the bounds
     */
    public static PBounds evaluateAreaPlacement(final KAreaPlacementData dpd,
            final PBounds parentBounds) {
        if (dpd == null) {
            return new PBounds(0, 0, parentBounds.width, parentBounds.height);
        }

        // determine the top-left
        KPosition topLeft = dpd.getTopLeft();
        Point2D topLeftPoint = evaluateDirectPosition(topLeft, parentBounds);

        // determine the bottom-right
        KPosition bottomRight = dpd.getBottomRight();
        Point2D bottomRightPoint = evaluateDirectPosition(bottomRight, parentBounds);

        return new PBounds(topLeftPoint.getX(), topLeftPoint.getY(), bottomRightPoint.getX()
                - topLeftPoint.getX(), bottomRightPoint.getY() - topLeftPoint.getY());
    }


    /**
     * Property to save xPosition of placed element.
     */
    private static IProperty<Double> pointPlacedObjectXPos = 
            new Property<Double>("PointPlacedObjectXPos");
    /**
     * Property to save yPosition of placed element.
     */
    private static IProperty<Double> pointPlacedObjectYPos = 
            new Property<Double>("PointPlacedObjectYPos");
    /**
     * Property to save width of element.
     */
    private static IProperty<Float> pointPlacedObjectWidth = 
            new Property<Float>("PointPlacedObjectWidth");
    /**
     * Property to save height of element.
     */
    private static IProperty<Float> pointPlacedObjectHeight = 
            new Property<Float>("PointPlacedObjectHeight");
    
    /**
     * Returns the bounds for a point placement data in given parent bounds.
     * 
     * @param ppd
     *            the point placement data
     * @param ownBounds
     *            the size of the object to be placed
     * @param parentBounds
     *            the parent bounds
     * @return the bounds
     */
    public static PBounds evaluatePointPlacement(final KPointPlacementData ppd, final Bounds ownBounds,
            final PBounds parentBounds) {
        if (ppd == null) {
            return new PBounds(0, 0, parentBounds.width, parentBounds.height);
        }

        float width = Math.max(ownBounds.getWidth(), ppd.getMinWidth());
        float height = Math.max(ownBounds.getHeight(), ppd.getMinHeight());
        KPosition ref = ppd.getReferencePoint();
        Point2D refPoint = evaluateDirectPosition(ref, parentBounds);

        Double x0, y0;

        switch (ppd.getHorizontalAlignment()) {
        case CENTER:
            x0 = refPoint.getX() - width / 2;
            break;
        case RIGHT:
            x0 = refPoint.getX() - width;
            break;
        default:
        case LEFT:
            x0 = refPoint.getX();
        }
        
        switch (ppd.getVerticalAlignment()) {
        case BOTTOM:
            y0 = refPoint.getY() - height;
            break;
        case CENTER:
            y0 = refPoint.getY() - height / 2;
            break;
        default:
        case TOP:
            y0 = refPoint.getY();
        }
        
        //attach the calculated data to be able to export the image later
        ((KRendering) ppd.eContainer()).setProperty(pointPlacedObjectXPos, x0);
        ((KRendering) ppd.eContainer()).setProperty(pointPlacedObjectYPos, y0);
        ((KRendering) ppd.eContainer()).setProperty(pointPlacedObjectWidth, width);
        ((KRendering) ppd.eContainer()).setProperty(pointPlacedObjectHeight, height);
        
        return new PBounds(x0, y0, width, height);
    }
    

    /**
     * Returns the points for a polyline placement data in given parent bounds.
     * 
     * @param line
     *            the polyline with its points
     * @param parentBounds
     *            the parent bounds
     * @return the points
     */
    public static Point2D[] evaluatePolylinePlacement(final KPolyline line,
            final PBounds parentBounds) {
        if (line.getPoints() == null || line.getPoints().isEmpty()) {
            return new Point2D[] { new Point2D.Float(0, 0) };
        }

        // evaluate the points of the polyline inside the parent bounds
        Point2D[] points = new Point2D[line.getPoints().size()];
        int i = 0;
        for (KPosition point : line.getPoints()) {
            points[i++] = evaluateDirectPosition(point, parentBounds);
        }

        return points;
    }

    /**
     * Returns the bounds and rotation for a decorator placement data on a given path.
     * 
     * @param dpd
     *            the decorator placement data
     * @param path
     *            the path
     * @return the origin, bounds and rotation for the decorator
     */
    public static Decoration evaluateDecoratorPlacement(final KDecoratorPlacementData dpd,
            final PSWTAdvancedPath path) {
        Decoration decoration = new Decoration();

        PSWTAdvancedPath approxPath = (PSWTAdvancedPath) path
                .getAttribute(PSWTAdvancedPath.APPROXIMATED_PATH);
        
        Point2D[] points; 
        if (approxPath != null) {
            points = approxPath.getShapePoints();
        } else {
            points = new Point2D[] {
                new Point2D.Double(0, 0),
                new Point2D.Double(0, 0)
            };
        }
        
        // default case
        if (dpd == null) {
            decoration.origin = (Point2D) points[0].clone();
            decoration.bounds = new PBounds(0.0, 0.0, 0.0, 0.0);
            decoration.rotation = 0.0;
            return decoration;
        }

        // get the segment and concrete point the location specifies on the path
        Pair<Integer, Point2D> result = MathUtil.getSegmentStartIndexAndPoint(points,
                dpd.getAbsolute(), dpd.getRelative());
        decoration.origin = result.getSecond();

        // calculate the decorator bounds
        decoration.bounds = new PBounds(dpd.getXOffset(), dpd.getYOffset(), dpd.getWidth(),
                dpd.getHeight());

        // if the decorator placement data specifies it to be relative calculate its rotation
        if (dpd.isRotateWithLine() && points.length > 1) {
            int segmentStart = result.getFirst();
            decoration.rotation = MathUtil.angle(points[segmentStart], points[segmentStart + 1]);
        } else {
            decoration.rotation = 0.0;
        }

        return decoration;
    }

    /**
     * Evaluates a position inside given parent bounds.
     * 
     * @param position
     *            the position
     * @param parentBounds
     *            the parent bounds
     * @return the evaluated position
     */
    public static Point2D.Float evaluateDirectPosition(final KPosition position,
            final PBounds parentBounds) {
        float width = (float) parentBounds.width;
        float height = (float) parentBounds.height;
        Point2D.Float point = new Point2D.Float();
        KXPosition xPos = position.getX();
        KYPosition yPos = position.getY();
        if (xPos instanceof KLeftPosition) {
            point.x = xPos.getAbsolute() + xPos.getRelative() * width;
        } else if (xPos instanceof KRightPosition) {
            point.x = width - xPos.getAbsolute() - xPos.getRelative() * width;
        } else { // SUPPRESS CHECKSTYLE EmptyBlock
            // this branch is reached in case xPos has been set to 'null', e.g. by EMF Compare
            // do nothing as the value will be re-set most certainly in near future :-)!
        }
        if (yPos instanceof KTopPosition) {
            point.y = yPos.getAbsolute() + yPos.getRelative() * height;
        } else if (yPos instanceof KBottomPosition) {
            point.y = height - yPos.getAbsolute() - yPos.getRelative() * height;
        } else { // SUPPRESS CHECKSTYLE EmptyBlock
            // this branch is reached in case yPos has been set to 'null', e.g. by EMF Compare
            // do nothing as the value will be re-set most certainly in near future :-)!
        }
        return point;
    }

       /**
     * Returns the given placement data as decorator placement data.
     * 
     * @param data
     *            the placement data
     * @return the decorator placement data or null if the placement data is no decorator placement
     *         data
     */
    public static KDecoratorPlacementData asDecoratorPlacementData(final KPlacementData data) {
        if (data instanceof KDecoratorPlacementData) {
            return (KDecoratorPlacementData) data;
        }
        return null;
    }

    /**
     * A data holder class for the result of evaluating a decorator.
     */
    public static class Decoration {

        /** the origin of the decoration. */
        private Point2D origin;
        /** the bounds of the decoration. */
        private PBounds bounds;
        /** the rotation of the decoration. */
        private double rotation;

        /**
         * Returns the origin of the decoration.
         * 
         * @return the origin
         */
        public final Point2D getOrigin() {
            return origin;
        }

        /**
         * Returns the bounds of the decoration.
         * 
         * @return the bounds
         */
        public final PBounds getBounds() {
            return bounds;
        }

        /**
         * Returns the rotation of the decoration.
         * 
         * @return the rotation
         */
        public final double getRotation() {
            return rotation;
        }
    }
}
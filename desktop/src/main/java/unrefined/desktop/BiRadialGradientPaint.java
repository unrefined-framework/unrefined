/*
 * Copyright (c) 2006, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package unrefined.desktop;

import java.awt.Color;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.beans.ConstructorProperties;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public class BiRadialGradientPaint implements Paint {

    private final int transparency;

    /** Gradient keyframe values in the range 0 to 1. */
    private final float[] fractions;

    /** Gradient colors. */
    private final Color[] colors;

    /** Transform to apply to gradient. */
    private final AffineTransform gradientTransform;

    /** The method to use when painting outside the gradient bounds. */
    private final CycleMethod cycleMethod;

    /** The color space in which to perform the gradient interpolation. */
    private final ColorSpaceType colorSpace;

    /**
     * The following fields are used only by Context
     * to cache certain values that remain constant and do not need to be
     * recalculated for each context created from this paint instance.
     */
    private ColorModel model;
    private float[] normalizedIntervals;
    private boolean isSimpleLookup;
    private SoftReference<int[][]> gradients;
    private SoftReference<int[]> gradient;
    private int fastGradientArraySize;

    /**
     * Returns a copy of the array of floats used by this gradient
     * to calculate color distribution.
     * The returned array always has 0 as its first value and 1 as its
     * last value, with increasing values in between.
     *
     * @return a copy of the array of floats used by this gradient to
     * calculate color distribution
     */
    public final float[] getFractions() {
        return Arrays.copyOf(fractions, fractions.length);
    }

    /**
     * Returns a copy of the array of colors used by this gradient.
     * The first color maps to the first value in the fractions array,
     * and the last color maps to the last value in the fractions array.
     *
     * @return a copy of the array of colors used by this gradient
     */
    public final Color[] getColors() {
        return Arrays.copyOf(colors, colors.length);
    }

    /**
     * Returns the enumerated type which specifies cycling behavior.
     *
     * @return the enumerated type which specifies cycling behavior
     */
    public final CycleMethod getCycleMethod() {
        return cycleMethod;
    }

    /**
     * Returns the enumerated type which specifies color space for
     * interpolation.
     *
     * @return the enumerated type which specifies color space for
     * interpolation
     */
    public final ColorSpaceType getColorSpace() {
        return colorSpace;
    }

    /**
     * Returns a copy of the transform applied to the gradient.
     *
     * <p>
     * Note that if no transform is applied to the gradient
     * when it is created, the identity transform is used.
     *
     * @return a copy of the transform applied to the gradient
     */
    public final AffineTransform getTransform() {
        return new AffineTransform(gradientTransform);
    }

    /**
     * Returns the transparency mode for this {@code Paint} object.
     *
     * @return {@code OPAQUE} if all colors used by this
     *         {@code Paint} object are opaque,
     *         {@code TRANSLUCENT} if at least one of the
     *         colors used by this {@code Paint} object is not opaque.
     * @see java.awt.Transparency
     */
    public final int getTransparency() {
        return transparency;
    }


    /** Focus point which defines the 0% gradient stop X coordinate. */
    private final Point2D focus;

    /** Center of the circle defining the 100% gradient stop X coordinate. */
    private final Point2D center;

    /** Radius of the innermost circle defining the 100% gradient stop. */
    private final float focusRadius;

    /** Radius of the outermost circle defining the 100% gradient stop. */
    private final float centerRadius;

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code NO_CYCLE} repeating method and {@code SRGB} color space,
     * using the center as the focus point.
     *
     * @param cx the X coordinate in user space of the center point of the
     *           circle defining the gradient.  The last color of the
     *           gradient is mapped to the perimeter of this circle.
     * @param cy the Y coordinate in user space of the center point of the
     *           circle defining the gradient.  The last color of the
     *           gradient is mapped to the perimeter of this circle.
     * @param radius the radius of the circle defining the extents of the
     *               color gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     *
     * @throws NullPointerException
     * if {@code fractions} array is null,
     * or {@code colors} array is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(float cx, float cy, float radius, float[] fractions, Color[] colors) {
        this(cx, cy,
                radius,
                cx, cy,
                radius,
                fractions,
                colors,
                CycleMethod.NO_CYCLE);
    }

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code NO_CYCLE} repeating method and {@code SRGB} color space,
     * using the center as the focus point.
     *
     * @param center the center point, in user space, of the circle defining
     *               the gradient
     * @param radius the radius of the circle defining the extents of the
     *               color gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     *
     * @throws NullPointerException
     * if {@code center} point is null,
     * or {@code fractions} array is null,
     * or {@code colors} array is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(Point2D center, float radius, float[] fractions, Color[] colors) {
        this(center,
                radius,
                center,
                radius,
                fractions,
                colors,
                CycleMethod.NO_CYCLE);
    }

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code SRGB} color space, using the center as the focus point.
     *
     * @param cx the X coordinate in user space of the center point of the
     *           circle defining the gradient.  The last color of the
     *           gradient is mapped to the perimeter of this circle.
     * @param cy the Y coordinate in user space of the center point of the
     *           circle defining the gradient.  The last color of the
     *           gradient is mapped to the perimeter of this circle.
     * @param radius the radius of the circle defining the extents of the
     *               color gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     * @param cycleMethod either {@code NO_CYCLE}, {@code REFLECT},
     *                    or {@code REPEAT}
     *
     * @throws NullPointerException
     * if {@code fractions} array is null,
     * or {@code colors} array is null,
     * or {@code cycleMethod} is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(float cx, float cy, float radius, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(cx, cy,
                radius,
                cx, cy,
                radius,
                fractions,
                colors,
                cycleMethod);
    }

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code SRGB} color space, using the center as the focus point.
     *
     * @param center the center point, in user space, of the circle defining
     *               the gradient
     * @param radius the radius of the circle defining the extents of the
     *               color gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     * @param cycleMethod either {@code NO_CYCLE}, {@code REFLECT},
     *                    or {@code REPEAT}
     *
     * @throws NullPointerException
     * if {@code center} point is null,
     * or {@code fractions} array is null,
     * or {@code colors} array is null,
     * or {@code cycleMethod} is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(Point2D center, float radius, float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(center,
                radius,
                center,
                radius,
                fractions,
                colors,
                cycleMethod);
    }

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code SRGB} color space.
     *
     * @param cx the X coordinate in user space of the center point of the
     *           circle defining the gradient.  The last color of the
     *           gradient is mapped to the perimeter of this circle.
     * @param cy the Y coordinate in user space of the center point of the
     *           circle defining the gradient.  The last color of the
     *           gradient is mapped to the perimeter of this circle.
     * @param centerRadius the radius of the circle defining the extents of the
     *               color gradient
     * @param fx the X coordinate of the point in user space to which the
     *           first color is mapped
     * @param fy the Y coordinate of the point in user space to which the
     *           first color is mapped
     * @param focusRadius the radius of the circle defining the extents of the
     *               color gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     * @param cycleMethod either {@code NO_CYCLE}, {@code REFLECT},
     *                    or {@code REPEAT}
     *
     * @throws NullPointerException
     * if {@code fractions} array is null,
     * or {@code colors} array is null,
     * or {@code cycleMethod} is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(float cx, float cy, float centerRadius, float fx, float fy, float focusRadius,
                                 float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(new Point2D.Float(cx, cy),
                centerRadius,
                new Point2D.Float(fx, fy),
                focusRadius,
                fractions,
                colors,
                cycleMethod);
    }

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code SRGB} color space.
     *
     * @param center the center point, in user space, of the circle defining
     *               the gradient.  The last color of the gradient is mapped
     *               to the perimeter of this circle.
     * @param centerRadius the radius of the circle defining the extents of the color
     *               gradient
     * @param focus the point in user space to which the first color is mapped
     * @param focusRadius the radius of the circle defining the extents of the color
     *               gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient. The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     * @param cycleMethod either {@code NO_CYCLE}, {@code REFLECT},
     *                    or {@code REPEAT}
     *
     * @throws NullPointerException
     * if one of the points is null,
     * or {@code fractions} array is null,
     * or {@code colors} array is null,
     * or {@code cycleMethod} is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(Point2D center, float centerRadius, Point2D focus, float focusRadius,
                                 float[] fractions, Color[] colors, CycleMethod cycleMethod) {
        this(center,
                centerRadius,
                focus,
                focusRadius,
                fractions,
                colors,
                cycleMethod,
                ColorSpaceType.SRGB,
                new AffineTransform());
    }

    /**
     * Constructs a {@code RadialGradientPaint}.
     *
     * @param center the center point in user space of the circle defining the
     *               gradient.  The last color of the gradient is mapped to
     *               the perimeter of this circle.
     * @param centerRadius the radius of the circle defining the extents of the
     *               color gradient
     * @param focus the point in user space to which the first color is mapped
     * @param focusRadius the radius of the circle defining the extents of the
     *               color gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     * @param cycleMethod either {@code NO_CYCLE}, {@code REFLECT},
     *                    or {@code REPEAT}
     * @param colorSpace which color space to use for interpolation,
     *                   either {@code SRGB} or {@code LINEAR_RGB}
     * @param gradientTransform transform to apply to the gradient
     *
     * @throws NullPointerException
     * if one of the points is null,
     * or {@code fractions} array is null,
     * or {@code colors} array is null,
     * or {@code cycleMethod} is null,
     * or {@code colorSpace} is null,
     * or {@code gradientTransform} is null
     * @throws IllegalArgumentException
     * if {@code radius} is non-positive,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    @ConstructorProperties({ "centerPoint", "centerRadius", "focusPoint", "focusRadius", "fractions", "colors", "cycleMethod", "colorSpace", "transform" })
    public BiRadialGradientPaint(Point2D center,
                                 float centerRadius,
                                 Point2D focus,
                                 float focusRadius,
                                 float[] fractions, Color[] colors,
                                 CycleMethod cycleMethod,
                                 ColorSpaceType colorSpace,
                                 AffineTransform gradientTransform) {
        if (fractions == null) throw new NullPointerException("Fractions array cannot be null");
        if (colors == null) throw new NullPointerException("Colors array cannot be null");
        if (cycleMethod == null) throw new NullPointerException("Cycle method cannot be null");
        if (colorSpace == null) throw new NullPointerException("Color space cannot be null");
        if (gradientTransform == null) throw new NullPointerException("Gradient transform cannot be null");
        if (fractions.length != colors.length) throw new IllegalArgumentException("Colors and fractions must have equal size");
        if (colors.length < 2) throw new IllegalArgumentException("User must specify at least 2 colors");

        // check that values are in the proper range and progress
        // in increasing order from 0 to 1
        float previousFraction = -1.0f;
        for (float currentFraction : fractions) {
            if (currentFraction < 0f || currentFraction > 1f)
                throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + currentFraction);
            if (currentFraction <= previousFraction)
                throw new IllegalArgumentException("Keyframe fractions must be increasing: " + currentFraction);
            previousFraction = currentFraction;
        }

        // We have to deal with the cases where the first gradient stop is not
        // equal to 0 and/or the last gradient stop is not equal to 1.
        // In both cases, create a new point and replicate the previous
        // extreme point's color.
        boolean fixFirst = false;
        boolean fixLast = false;
        int len = fractions.length;
        int off = 0;

        if (fractions[0] != 0f) {
            // first stop is not equal to zero, fix this condition
            fixFirst = true;
            len ++;
            off ++;
        }
        if (fractions[fractions.length-1] != 1f) {
            // last stop is not equal to one, fix this condition
            fixLast = true;
            len++;
        }

        this.fractions = new float[len];
        System.arraycopy(fractions, 0, this.fractions, off, fractions.length);
        this.colors = new Color[len];
        System.arraycopy(colors, 0, this.colors, off, colors.length);

        if (fixFirst) {
            this.fractions[0] = 0f;
            this.colors[0] = colors[0];
        }
        if (fixLast) {
            this.fractions[len-1] = 1f;
            this.colors[len-1] = colors[colors.length - 1];
        }

        // copy some flags
        this.colorSpace = colorSpace;
        this.cycleMethod = cycleMethod;

        // copy the gradient transform
        this.gradientTransform = new AffineTransform(gradientTransform);

        // determine transparency
        boolean opaque = true;
        for (Color color : colors) {
            opaque = opaque && (color.getAlpha() == 0xFF);
        }
        this.transparency = opaque ? OPAQUE : TRANSLUCENT;

        // check input arguments
        if (center == null) throw new NullPointerException("Center point must be non-null");
        if (focus == null) throw new NullPointerException("Focus point must be non-null");
        if (centerRadius <= 0) throw new IllegalArgumentException("Center radius must be greater than zero");
        if (focusRadius < 0) throw new IllegalArgumentException("Focus radius must be at least zero");

        // copy parameters
        this.center = new Point2D.Double(center.getX(), center.getY());
        this.focus = new Point2D.Double(focus.getX(), focus.getY());
        this.centerRadius = centerRadius;
        this.focusRadius = focusRadius;
    }

    /**
     * Constructs a {@code RadialGradientPaint} with a default
     * {@code SRGB} color space.
     * The gradient circle of the {@code RadialGradientPaint} is defined
     * by the given bounding box.
     * <p>
     * This constructor is a more convenient way to express the
     * following (equivalent) code:<br>
     *
     * <pre>
     *     double gw = gradientBounds.getWidth();
     *     double gh = gradientBounds.getHeight();
     *     double cx = gradientBounds.getCenterX();
     *     double cy = gradientBounds.getCenterY();
     *     Point2D center = new Point2D.Double(cx, cy);
     *
     *     AffineTransform gradientTransform = new AffineTransform();
     *     gradientTransform.translate(cx, cy);
     *     gradientTransform.scale(gw / 2, gh / 2);
     *     gradientTransform.translate(-cx, -cy);
     *
     *     RadialGradientPaint gp =
     *         new RadialGradientPaint(center, 1.0f, center,
     *                                 fractions, colors,
     *                                 cycleMethod,
     *                                 ColorSpaceType.SRGB,
     *                                 gradientTransform);
     * </pre>
     *
     * @param gradientBounds the bounding box, in user space, of the circle
     *                       defining the outermost extent of the gradient
     * @param fractions numbers ranging from 0.0 to 1.0 specifying the
     *                  distribution of colors along the gradient
     * @param colors array of colors to use in the gradient.  The first color
     *               is used at the focus point, the last color around the
     *               perimeter of the circle.
     * @param cycleMethod either {@code NO_CYCLE}, {@code REFLECT},
     *                    or {@code REPEAT}
     *
     * @throws NullPointerException
     * if {@code gradientBounds} is null,
     * or {@code fractions} array is null,
     * or {@code colors} array is null,
     * or {@code cycleMethod} is null
     * @throws IllegalArgumentException
     * if {@code gradientBounds} is empty,
     * or {@code fractions.length != colors.length},
     * or {@code colors} is less than 2 in size,
     * or a {@code fractions} value is less than 0.0 or greater than 1.0,
     * or the {@code fractions} are not provided in strictly increasing order
     */
    public BiRadialGradientPaint(Rectangle2D gradientBounds,
                                 float[] fractions, Color[] colors,
                                 CycleMethod cycleMethod) {
        // gradient center/focal point is the center of the bounding box,
        // radius is set to 1.0, and then we set a scale transform
        // to achieve an elliptical gradient defined by the bounding box
        this(new Point2D.Double(gradientBounds.getCenterX(),
                        gradientBounds.getCenterY()),
                1.0f,
                new Point2D.Double(gradientBounds.getCenterX(),
                        gradientBounds.getCenterY()),
                1.0f,
                fractions,
                colors,
                cycleMethod,
                ColorSpaceType.SRGB,
                createGradientTransform(gradientBounds));

        if (gradientBounds.isEmpty()) throw new IllegalArgumentException("Gradient bounds must be non-empty");
    }

    private static AffineTransform createGradientTransform(Rectangle2D r) {
        double cx = r.getCenterX();
        double cy = r.getCenterY();
        AffineTransform xform = AffineTransform.getTranslateInstance(cx, cy);
        xform.scale(r.getWidth()/2, r.getHeight()/2);
        xform.translate(-cx, -cy);
        return xform;
    }

    /**
     * Creates and returns a {@link PaintContext} used to
     * generate a circular radial color gradient pattern.
     * See the description of the {@link Paint#createContext createContext} method
     * for information on null parameter handling.
     *
     * @param cm the preferred {@link ColorModel} which represents the most convenient
     *           format for the caller to receive the pixel data, or {@code null}
     *           if there is no preference.
     * @param deviceBounds the device space bounding box
     *                     of the graphics primitive being rendered.
     * @param userBounds the user space bounding box
     *                   of the graphics primitive being rendered.
     * @param transform the {@link AffineTransform} from user
     *              space into device space.
     * @param hints the set of hints that the context object can use to
     *              choose between rendering alternatives.
     * @return the {@code PaintContext} for
     *         generating color patterns.
     * @see Paint
     * @see PaintContext
     * @see ColorModel
     * @see Rectangle
     * @see Rectangle2D
     * @see AffineTransform
     * @see RenderingHints
     */
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints) {
        // avoid modifying the user's transform...
        transform = new AffineTransform(transform);
        // incorporate the gradient transform
        transform.concatenate(gradientTransform);

        return new Context(this, cm,
                deviceBounds, userBounds,
                transform, hints,
                (float) center.getX(),
                (float) center.getY(),
                centerRadius,
                (float) focus.getX(),
                (float) focus.getY(),
                focusRadius,
                fractions, colors,
                cycleMethod, colorSpace);
    }

    /**
     * Returns a copy of the center point of the radial gradient.
     *
     * @return a {@code Point2D} object that is a copy of the center point
     */
    public Point2D getCenterPoint() {
        return new Point2D.Double(center.getX(), center.getY());
    }

    /**
     * Returns a copy of the focus point of the radial gradient.
     * Note that if the focus point specified when the radial gradient
     * was constructed lies outside of the radius of the circle, this
     * method will still return the original focus point even though
     * the rendering may center the rings of color on a different
     * point that lies inside the radius.
     *
     * @return a {@code Point2D} object that is a copy of the focus point
     */
    public Point2D getFocusPoint() {
        return new Point2D.Double(focus.getX(), focus.getY());
    }

    /**
     * Returns the radius of the circle defining the radial gradient.
     *
     * @return the radius of the circle defining the radial gradient
     */
    public float getFocusRadius() {
        return focusRadius;
    }

    /**
     * Returns the radius of the circle defining the radial gradient.
     *
     * @return the radius of the circle defining the radial gradient
     */
    public float getCenterRadius() {
        return centerRadius;
    }

    private static final class Context implements PaintContext {
    
        /**
         * The PaintContext's ColorModel.  This is ARGB if colors are not all
         * opaque, otherwise it is RGB.
         */
        private ColorModel model;
    
        /**
         * Color model used if gradient colors are all opaque.
         */
        private static final ColorModel xrgbmodel = new DirectColorModel(24, 0x00FF0000, 0x0000FF00, 0x000000FF);
    
        /**
         * The cached ColorModel.
         */
        private static ColorModel cachedModel;
    
        /**
         * The cached raster, which is reusable among instances.
         */
        private static WeakReference<Raster> cached;
    
        /**
         * Raster is reused whenever possible.
         */
        private Raster saved;
    
        /**
         * The method to use when painting out of the gradient bounds.
         */
        private final CycleMethod cycleMethod;
    
        /**
         * The ColorSpace in which to perform the interpolation
         */
        private final ColorSpaceType colorSpace;
    
        /**
         * Elements of the inverse transform matrix.
         */
        private final float a00;
        private final float a01;
        private final float a10;
        private final float a11;
        private final float a02;
        private final float a12;
    
        /**
         * This boolean specifies whether we are in simple lookup mode, where an
         * input value between 0 and 1 may be used to directly index into a single
         * array of gradient colors.  If this boolean value is false, then we have
         * to use a 2-step process where we have to determine which gradient array
         * we fall into, then determine the index into that array.
         */
        private boolean isSimpleLookup;
    
        /**
         * Size of gradients array for scaling the 0-1 index when looking up
         * colors the fast way.
         */
        private int fastGradientArraySize;
    
        /**
         * Array which contains the interpolated color values for each interval,
         * used by calculateSingleArrayGradient().  It is protected for possible
         * direct access by subclasses.
         */
        private int[] gradient;
    
        /**
         * Array of gradient arrays, one array for each interval.  Used by
         * calculateMultipleArrayGradient().
         */
        private int[][] gradients;
    
        /**
         * Normalized intervals array.
         */
        private float[] normalizedIntervals;
    
        /**
         * Fractions array.
         */
        private final float[] fractions;
    
        /**
         * Used to determine if gradient colors are all opaque.
         */
        private int transparencyTest;
    
        /**
         * Color space conversion lookup tables.
         */
        private static final int[] SRGBtoLinearRGB = new int[256];
        private static final int[] LinearRGBtoSRGB = new int[256];
        static {
            // build the tables
            for (int k = 0; k < 256; k++) {
                SRGBtoLinearRGB[k] = convertSRGBtoLinearRGB(k);
                LinearRGBtoSRGB[k] = convertLinearRGBtoSRGB(k);
            }
        }
    
        /**
         * Constant number of max colors between any 2 arbitrary colors.
         * Used for creating and indexing gradients arrays.
         */
        private static final int GRADIENT_SIZE = 256;
        private static final int GRADIENT_SIZE_INDEX = GRADIENT_SIZE - 1;
    
        /**
         * Maximum length of the fast single-array.  If the estimated array size
         * is greater than this, switch over to the slow lookup method.
         * No particular reason for choosing this number, but it seems to provide
         * satisfactory performance for the common case (fast lookup).
         */
        private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
    
        /**
         * This function is the meat of this class.  It calculates an array of
         * gradient colors based on an array of fractions and color values at
         * those fractions.
         */
        private void calculateLookupData(Color[] colors) {
            Color[] normalizedColors;
            if (colorSpace == ColorSpaceType.LINEAR_RGB) {
                // create a new colors array
                normalizedColors = new Color[colors.length];
                // convert the colors using the lookup table
                for (int i = 0; i < colors.length; i++) {
                    int argb = colors[i].getRGB();
                    int a = argb >>> 24;
                    int r = SRGBtoLinearRGB[(argb >> 16) & 0xff];
                    int g = SRGBtoLinearRGB[(argb >> 8) & 0xff];
                    int b = SRGBtoLinearRGB[(argb) & 0xff];
                    normalizedColors[i] = new Color(r, g, b, a);
                }
            } else {
                // we can just use this array by reference since we do not
                // modify its values in the case of SRGB
                normalizedColors = colors;
            }
    
            // this will store the intervals (distances) between gradient stops
            normalizedIntervals = new float[fractions.length - 1];
    
            // convert from fractions into intervals
            for (int i = 0; i < normalizedIntervals.length; i++) {
                // interval distance is equal to the difference in positions
                normalizedIntervals[i] = this.fractions[i + 1] - this.fractions[i];
            }
    
            // initialize to be fully opaque for ANDing with colors
            transparencyTest = 0xff000000;
    
            // array of interpolation arrays
            gradients = new int[normalizedIntervals.length][];
    
            // find smallest interval
            float Imin = 1;
            for (float normalizedInterval : normalizedIntervals) {
                Imin = Math.min(Imin, normalizedInterval);
            }
    
            // Estimate the size of the entire gradients array.
            // This is to prevent a tiny interval from causing the size of array
            // to explode.  If the estimated size is too large, break to using
            // separate arrays for each interval, and using an indexing scheme at
            // look-up time.
            int estimatedSize = 0;
            for (float normalizedInterval : normalizedIntervals) {
                estimatedSize += (normalizedInterval / Imin) * GRADIENT_SIZE;
            }
    
            if (estimatedSize > MAX_GRADIENT_ARRAY_SIZE) {
                // slow method
                calculateMultipleArrayGradient(normalizedColors);
            } else {
                // fast method
                calculateSingleArrayGradient(normalizedColors, Imin);
            }
    
            // use the most "economical" model
            if ((transparencyTest >>> 24) == 0xff) {
                model = xrgbmodel;
            } else {
                model = ColorModel.getRGBdefault();
            }
        }
    
        /**
         * FAST LOOKUP METHOD
         * <p>
         * This method calculates the gradient color values and places them in a
         * single int array, gradient[].  It does this by allocating space for
         * each interval based on its size relative to the smallest interval in
         * the array.  The smallest interval is allocated 255 interpolated values
         * (the maximum number of unique in-between colors in a 24 bit color
         * system), and all other intervals are allocated
         * size = (255 * the ratio of their size to the smallest interval).
         * <p>
         * This scheme expedites a speedy retrieval because the colors are
         * distributed along the array according to their user-specified
         * distribution.  All that is needed is a relative index from 0 to 1.
         * <p>
         * The only problem with this method is that the possibility exists for
         * the array size to balloon in the case where there is a
         * disproportionately small gradient interval.  In this case the other
         * intervals will be allocated huge space, but much of that data is
         * redundant.  We thus need to use the space conserving scheme below.
         *
         * @param Imin the size of the smallest interval
         */
        private void calculateSingleArrayGradient(Color[] colors, float Imin) {
            // set the flag so we know later it is a simple (fast) lookup
            isSimpleLookup = true;
    
            // 2 colors to interpolate
            int rgb1, rgb2;
    
            //the eventual size of the single array
            int gradientsTot = 1;
    
            // for every interval (transition between 2 colors)
            for (int i = 0; i < gradients.length; i++) {
                // create an array whose size is based on the ratio to the
                // smallest interval
                int nGradients = (int) ((normalizedIntervals[i] / Imin) * 255f);
                gradientsTot += nGradients;
                gradients[i] = new int[nGradients];
    
                // the 2 colors (keyframes) to interpolate between
                rgb1 = colors[i].getRGB();
                rgb2 = colors[i + 1].getRGB();
    
                // fill this array with the colors in between rgb1 and rgb2
                interpolate(rgb1, rgb2, gradients[i]);
    
                // if the colors are opaque, transparency should still
                // be 0xff000000
                transparencyTest &= rgb1;
                transparencyTest &= rgb2;
            }
    
            // put all gradients in a single array
            gradient = new int[gradientsTot];
            int curOffset = 0;
            for (int[] ints : gradients) {
                System.arraycopy(ints, 0, gradient, curOffset, ints.length);
                curOffset += ints.length;
            }
            gradient[gradient.length - 1] = colors[colors.length - 1].getRGB();
    
            // if interpolation occurred in Linear RGB space, convert the
            // gradients back to sRGB using the lookup table
            if (colorSpace == ColorSpaceType.LINEAR_RGB) {
                for (int i = 0; i < gradient.length; i++) {
                    gradient[i] = convertEntireColorLinearRGBtoSRGB(gradient[i]);
                }
            }
    
            fastGradientArraySize = gradient.length - 1;
        }
    
        /**
         * SLOW LOOKUP METHOD
         * <p>
         * This method calculates the gradient color values for each interval and
         * places each into its own 255 size array.  The arrays are stored in
         * gradients[][].  (255 is used because this is the maximum number of
         * unique colors between 2 arbitrary colors in a 24 bit color system.)
         * <p>
         * This method uses the minimum amount of space (only 255 * number of
         * intervals), but it aggravates the lookup procedure, because now we
         * have to find out which interval to select, then calculate the index
         * within that interval.  This causes a significant performance hit,
         * because it requires this calculation be done for every point in
         * the rendering loop.
         * <p>
         * For those of you who are interested, this is a classic example of the
         * time-space tradeoff.
         */
        private void calculateMultipleArrayGradient(Color[] colors) {
            // set the flag so we know later it is a non-simple lookup
            isSimpleLookup = false;
    
            // 2 colors to interpolate
            int rgb1, rgb2;
    
            // for every interval (transition between 2 colors)
            for (int i = 0; i < gradients.length; i++) {
                // create an array of the maximum theoretical size for
                // each interval
                gradients[i] = new int[GRADIENT_SIZE];
    
                // get the 2 colors
                rgb1 = colors[i].getRGB();
                rgb2 = colors[i + 1].getRGB();
    
                // fill this array with the colors in between rgb1 and rgb2
                interpolate(rgb1, rgb2, gradients[i]);
    
                // if the colors are opaque, transparency should still
                // be 0xff000000
                transparencyTest &= rgb1;
                transparencyTest &= rgb2;
            }
    
            // if interpolation occurred in Linear RGB space, convert the
            // gradients back to SRGB using the lookup table
            if (colorSpace == ColorSpaceType.LINEAR_RGB) {
                for (int j = 0; j < gradients.length; j++) {
                    for (int i = 0; i < gradients[j].length; i++) {
                        gradients[j][i] =
                                convertEntireColorLinearRGBtoSRGB(gradients[j][i]);
                    }
                }
            }
        }
    
        /**
         * Yet another helper function.  This one linearly interpolates between
         * 2 colors, filling up the output array.
         *
         * @param rgb1   the start color
         * @param rgb2   the end color
         * @param output the output array of colors; must not be null
         */
        private void interpolate(int rgb1, int rgb2, int[] output) {
            // color components
            int a1, r1, g1, b1, da, dr, dg, db;
    
            // step between interpolated values
            float stepSize = 1.0f / output.length;
    
            // extract color components from packed integer
            a1 = (rgb1 >> 24) & 0xff;
            r1 = (rgb1 >> 16) & 0xff;
            g1 = (rgb1 >> 8) & 0xff;
            b1 = (rgb1) & 0xff;
    
            // calculate the total change in alpha, red, green, blue
            da = ((rgb2 >> 24) & 0xff) - a1;
            dr = ((rgb2 >> 16) & 0xff) - r1;
            dg = ((rgb2 >> 8) & 0xff) - g1;
            db = ((rgb2) & 0xff) - b1;
    
            // for each step in the interval calculate the in-between color by
            // multiplying the normalized current position by the total color
            // change (0.5 is added to prevent truncation round-off error)
            for (int i = 0; i < output.length; i++) {
                output[i] =
                        (((int) ((a1 + i * da * stepSize) + 0.5) << 24)) |
                                (((int) ((r1 + i * dr * stepSize) + 0.5) << 16)) |
                                (((int) ((g1 + i * dg * stepSize) + 0.5) << 8)) |
                                (((int) ((b1 + i * db * stepSize) + 0.5)));
            }
        }
    
        /**
         * Yet another helper function.  This one extracts the color components
         * of an integer RGB triple, converts them from LinearRGB to SRGB, then
         * recompacts them into an int.
         */
        private int convertEntireColorLinearRGBtoSRGB(int rgb) {
            // color components
            int a1, r1, g1, b1;
    
            // extract red, green, blue components
            a1 = (rgb >> 24) & 0xff;
            r1 = (rgb >> 16) & 0xff;
            g1 = (rgb >> 8) & 0xff;
            b1 = (rgb) & 0xff;
    
            // use the lookup table
            r1 = LinearRGBtoSRGB[r1];
            g1 = LinearRGBtoSRGB[g1];
            b1 = LinearRGBtoSRGB[b1];
    
            // re-compact the components
            return ((a1 << 24) |
                    (r1 << 16) |
                    (g1 << 8) |
                    (b1));
        }
    
        /**
         * Helper function to index into the gradients array.  This is necessary
         * because each interval has an array of colors with uniform size 255.
         * However, the color intervals are not necessarily of uniform length, so
         * a conversion is required.
         *
         * @param position the unmanipulated position, which will be mapped
         *                 into the range 0 to 1
         * @return integer color to display
         */
        private int indexIntoGradientsArrays(float position) {
            // first, manipulate position value depending on the cycle method
            if (cycleMethod == CycleMethod.NO_CYCLE) {
                if (position > 1) {
                    // upper bound is 1
                    position = 1;
                } else if (position < 0) {
                    // lower bound is 0
                    position = 0;
                }
            } else if (cycleMethod == CycleMethod.REPEAT) {
                // get the fractional part
                // (modulo behavior discards integer component)
                position = position - (int) position;
    
                //position should now be between -1 and 1
                if (position < 0) {
                    // force it to be in the range 0-1
                    position = position + 1;
                }
            } else { // cycleMethod == CycleMethod.REFLECT
                if (position < 0) {
                    // take absolute value
                    position = -position;
                }
    
                // get the integer part
                int part = (int) position;
    
                // get the fractional part
                position = position - part;
    
                if ((part & 1) == 1) {
                    // integer part is odd, get reflected color instead
                    position = 1 - position;
                }
            }
    
            // now, get the color based on this 0-1 position...
    
            if (isSimpleLookup) {
                // easy to compute: just scale index by array size
                return gradient[(int) (position * fastGradientArraySize)];
            } else {
                // more complicated computation, to save space
    
                // for all the gradient interval arrays
                for (int i = 0; i < gradients.length; i++) {
                    if (position < fractions[i + 1]) {
                        // this is the array we want
                        float delta = position - fractions[i];
    
                        // this is the interval we want
                        int index = (int) ((delta / normalizedIntervals[i])
                                * (GRADIENT_SIZE_INDEX));
    
                        return gradients[i][index];
                    }
                }
            }
    
            return gradients[gradients.length - 1][GRADIENT_SIZE_INDEX];
        }
    
        /**
         * Helper function to convert a color component in sRGB space to linear
         * RGB space.  Used to build a static lookup table.
         */
        private static int convertSRGBtoLinearRGB(int color) {
            float input, output;
    
            input = color / 255.0f;
            if (input <= 0.04045f) {
                output = input / 12.92f;
            } else {
                output = (float) Math.pow((input + 0.055) / 1.055, 2.4);
            }
    
            return Math.round(output * 255.0f);
        }
    
        /**
         * Helper function to convert a color component in linear RGB space to
         * SRGB space.  Used to build a static lookup table.
         */
        private static int convertLinearRGBtoSRGB(int color) {
            float input, output;
    
            input = color / 255.0f;
            if (input <= 0.0031308) {
                output = input * 12.92f;
            } else {
                output = (1.055f *
                        ((float) Math.pow(input, (1.0 / 2.4)))) - 0.055f;
            }
    
            return Math.round(output * 255.0f);
        }
    
        /**
         * {@inheritDoc}
         */
        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            // If working raster is big enough, reuse it. Otherwise,
            // build a large enough new one.
            Raster raster = saved;
            if (raster == null ||
                    raster.getWidth() < w || raster.getHeight() < h) {
                raster = getCachedRaster(model, w, h);
                saved = raster;
            }
    
            // Access raster internal int array. Because we use a DirectColorModel,
            // we know the DataBuffer is of type DataBufferInt and the SampleModel
            // is SinglePixelPackedSampleModel.
            // Adjust for initial offset in DataBuffer and also for the scanline
            // stride.
            // These calls make the DataBuffer non-acceleratable, but the
            // Raster is never Stable long enough to accelerate anyway...
            DataBufferInt rasterDB = (DataBufferInt) raster.getDataBuffer();
            int[] pixels = rasterDB.getData(0);
            int off = rasterDB.getOffset();
            int scanlineStride = ((SinglePixelPackedSampleModel)
                    raster.getSampleModel()).getScanlineStride();
            int adjust = scanlineStride - w;
    
            fillRaster(pixels, off, adjust, x, y, w, h); // delegate to subclass
    
            return raster;
        }
    
    
        /**
         * Took this cacheRaster code from GradientPaint. It appears to recycle
         * rasters for use by any other instance, as long as they are sufficiently
         * large.
         */
        private static synchronized Raster getCachedRaster(ColorModel cm, int w, int h) {
            if (cm == cachedModel) {
                if (cached != null) {
                    Raster ras = cached.get();
                    if (ras != null &&
                            ras.getWidth() >= w &&
                            ras.getHeight() >= h) {
                        cached = null;
                        return ras;
                    }
                }
            }
            return cm.createCompatibleWritableRaster(w, h);
        }
    
        /**
         * Took this cacheRaster code from GradientPaint. It appears to recycle
         * rasters for use by any other instance, as long as they are sufficiently
         * large.
         */
        private static synchronized void putCachedRaster(ColorModel cm, Raster ras) {
            if (cached != null) {
                Raster cras = cached.get();
                if (cras != null) {
                    int cw = cras.getWidth();
                    int ch = cras.getHeight();
                    int iw = ras.getWidth();
                    int ih = ras.getHeight();
                    if (cw >= iw && ch >= ih) {
                        return;
                    }
                    if (cw * ch >= iw * ih) {
                        return;
                    }
                }
            }
            cachedModel = cm;
            cached = new WeakReference<>(ras);
        }
    
        /**
         * {@inheritDoc}
         */
        public void dispose() {
            if (saved != null) {
                putCachedRaster(model, saved);
                saved = null;
            }
        }
    
        /**
         * {@inheritDoc}
         */
        public ColorModel getColorModel() {
            return model;
        }
    
    
        /**
         * True when (focus == center).
         */
        private final boolean isSimpleFocus;
    
        /**
         * True when (centerRadius == focusRadius).
         */
        private final boolean isSingleRadius;
    
        /**
         * True when (cycleMethod == NO_CYCLE).
         */
        private final boolean isNonCyclic;
    
        /**
         * Radius of the outermost circle defining the 100% gradient stop.
         */
        private final float centerRadius;
    
        /**
         * Radius of the innermost circle defining the 100% gradient stop.
         */
        private final float focusRadius;
    
        /**
         * Variables representing center and focus points.
         */
        private final float centerX;
        private final float centerY;
        private float focusX, focusY;
    
        /**
         * Radius of the gradient circle squared.
         */
        private final float centerRadiusSq;
        
        /**
         * Radius of the gradient circle squared.
         */
        private final float focusRadiusSq;
    
        /**
         * Constant part of X, Y user space coordinates.
         */
        private final float constA;
        private final float constB;
    
        /**
         * Constant second order delta for simple loop.
         */
        private final float gDeltaDelta;
    
        /**
         * This value represents the solution when focusX == X.  It is called
         * trivial because it is easier to calculate than the general case.
         */
        private final float trivial;
    
        /**
         * Amount for offset when clamping focus.
         */
        private static final float SCALEBACK = .99f;
    
        /**
         * Constructor for RadialGradientPaintContext.
         *
         * @param paint        the {@code RadialGradientPaint} from which this context
         *                     is created
         * @param cm           the {@code ColorModel} that receives
         *                     the {@code Paint} data (this is used only as a hint)
         * @param deviceBounds the device space bounding box of the
         *                     graphics primitive being rendered
         * @param userBounds   the user space bounding box of the
         *                     graphics primitive being rendered
         * @param t            the {@code AffineTransform} from user
         *                     space into device space (gradientTransform should be
         *                     concatenated with this)
         * @param hints        the hints that the context object uses to choose
         *                     between rendering alternatives
         * @param cx           the center X coordinate in user space of the circle defining
         *                     the gradient.  The last color of the gradient is mapped to
         *                     the perimeter of this circle.
         * @param cy           the center Y coordinate in user space of the circle defining
         *                     the gradient.  The last color of the gradient is mapped to
         *                     the perimeter of this circle.
         * @param centerRadius the radius of the circle defining the extents of the
         *                     color gradient
         * @param fx           the X coordinate in user space to which the first color
         *                     is mapped
         * @param fy           the Y coordinate in user space to which the first color
         *                     is mapped
         * @param fractions    the fractions specifying the gradient distribution
         * @param colors       the gradient colors
         * @param cycleMethod  either NO_CYCLE, REFLECT, or REPEAT
         * @param colorSpace   which colorspace to use for interpolation,
         *                     either SRGB or LINEAR_RGB
         */
        public Context(BiRadialGradientPaint paint,
                ColorModel cm,
                Rectangle deviceBounds,
                Rectangle2D userBounds,
                AffineTransform t,
                RenderingHints hints,
                float cx, float cy,
                float centerRadius,
                float fx, float fy,
                float focusRadius,
                float[] fractions,
                Color[] colors,
                CycleMethod cycleMethod,
                ColorSpaceType colorSpace) {
            if (deviceBounds == null) throw new NullPointerException("Device bounds cannot be null");
            if (userBounds == null) throw new NullPointerException("User bounds cannot be null");
            if (t == null) throw new NullPointerException("Transform cannot be null");
            if (hints == null) throw new NullPointerException("RenderingHints cannot be null");
    
            // The inverse transform is needed to go from device to user space.
            // Get all the components of the inverse transform matrix.
            AffineTransform tInv;
            try {
                // the following assumes that the caller has copied the incoming
                // transform and is not concerned about it being modified
                t.invert();
                tInv = t;
            } catch (NoninvertibleTransformException e) {
                // just use identity transform in this case; better to show
                // (incorrect) results than to throw an exception and/or no-op
                tInv = new AffineTransform();
            }
            double[] m = new double[6];
            tInv.getMatrix(m);
            a00 = (float) m[0];
            a10 = (float) m[1];
            a01 = (float) m[2];
            a11 = (float) m[3];
            a02 = (float) m[4];
            a12 = (float) m[5];
    
            // copy some flags
            this.cycleMethod = cycleMethod;
            this.colorSpace = colorSpace;
    
            // we can avoid copying this array since we do not modify its values
            this.fractions = fractions;
    
            // note that only one of these values can ever be non-null (we either
            // store the fast gradient array or the slow one, but never both
            // at the same time)
            int[] gradient =
                    (paint.gradient != null) ? paint.gradient.get() : null;
            int[][] gradients =
                    (paint.gradients != null) ? paint.gradients.get() : null;
    
            if (gradient == null && gradients == null) {
                // we need to (re)create the appropriate values
                calculateLookupData(colors);
    
                // now cache the calculated values in the
                // MultipleGradientPaint instance for future use
                paint.model = this.model;
                paint.normalizedIntervals = this.normalizedIntervals;
                paint.isSimpleLookup = this.isSimpleLookup;
                if (isSimpleLookup) {
                    // only cache the fast array
                    paint.fastGradientArraySize = this.fastGradientArraySize;
                    paint.gradient = new SoftReference<>(this.gradient);
                } else {
                    // only cache the slow array
                    paint.gradients = new SoftReference<>(this.gradients);
                }
            } else {
                // use the values cached in the MultipleGradientPaint instance
                this.model = paint.model;
                this.normalizedIntervals = paint.normalizedIntervals;
                this.isSimpleLookup = paint.isSimpleLookup;
                this.gradient = gradient;
                this.fastGradientArraySize = paint.fastGradientArraySize;
                this.gradients = gradients;
            }
    
            // copy some parameters
            centerX = cx;
            centerY = cy;
            focusX = fx;
            focusY = fy;
            this.centerRadius = centerRadius;
            this.focusRadius = focusRadius;
    
            this.isSimpleFocus = (focusX == centerX) && (focusY == centerY);
            this.isSingleRadius = focusRadius == 0;
            this.isNonCyclic = (cycleMethod == CycleMethod.NO_CYCLE);
    
            // for use in the quadratic equation
            centerRadiusSq = this.centerRadius * this.centerRadius;
            focusRadiusSq = this.focusRadius * this.focusRadius;
    
            float dX = focusX - centerX;
            float dY = focusY - centerY;
    
            double distSq = (dX * dX) + (dY * dY);
    
            // test if distance from focus to center is greater than the radius
            if (distSq > centerRadiusSq * SCALEBACK) {
                // clamp focus to radius
                float scalefactor = (float) Math.sqrt(centerRadiusSq * SCALEBACK / distSq);
                dX = dX * scalefactor;
                dY = dY * scalefactor;
                focusX = centerX + dX;
                focusY = centerY + dY;
            }
    
            // calculate the solution to be used in the case where X == focusX
            // in cyclicCircularGradientFillRaster()
            trivial = (float) Math.sqrt(centerRadiusSq - (dX * dX));
    
            // constant parts of X, Y user space coordinates
            constA = a02 - centerX;
            constB = a12 - centerY;
    
            // constant second order delta for simple loop
            gDeltaDelta = 2 * (a00 * a00 + a10 * a10) / centerRadiusSq;
        }
    
        /**
         * Return a Raster containing the colors generated for the graphics
         * operation.
         *
         * @param x,y,w,h the area in device space for which colors are
         *                generated.
         */
        private void fillRaster(int[] pixels, int off, int adjust,
                                int x, int y, int w, int h) {
            if (isSingleRadius) {
                if (isSimpleFocus && isNonCyclic && isSimpleLookup) {
                    simpleNonCyclicFillRaster(pixels, off, adjust, x, y, w, h);
                } else {
                    cyclicCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
                }
            }
            else {
                cyclicBiCircularGradientFillRaster(pixels, off, adjust, x, y, w, h);
            }
        }
    
        /**
         * This code works in the simplest of cases, where the focus == center
         * point, the gradient is noncyclic, and the gradient lookup method is
         * fast (single array index, no conversion necessary).
         */
        private void simpleNonCyclicFillRaster(int[] pixels, int off, int adjust, int x, int y, int w, int h) {
            /* We calculate sqrt(X^2 + Y^2) relative to the radius
             * size to get the fraction for the color to use.
             *
             * Each step along the scanline adds (a00, a10) to (X, Y).
             * If we precalculate:
             *   gRel = X^2+Y^2
             * for the start of the row, then for each step we need to
             * calculate:
             *   gRel' = (X+a00)^2 + (Y+a10)^2
             *         = X^2 + 2*X*a00 + a00^2 + Y^2 + 2*Y*a10 + a10^2
             *         = (X^2+Y^2) + 2*(X*a00+Y*a10) + (a00^2+a10^2)
             *         = gRel + 2*(X*a00+Y*a10) + (a00^2+a10^2)
             *         = gRel + 2*DP + SD
             * (where DP = dot product between X,Y and a00,a10
             *  and   SD = dot product square of the delta vector)
             * For the step after that we get:
             *   gRel'' = (X+2*a00)^2 + (Y+2*a10)^2
             *          = X^2 + 4*X*a00 + 4*a00^2 + Y^2 + 4*Y*a10 + 4*a10^2
             *          = (X^2+Y^2) + 4*(X*a00+Y*a10) + 4*(a00^2+a10^2)
             *          = gRel  + 4*DP + 4*SD
             *          = gRel' + 2*DP + 3*SD
             * The increment changed by:
             *     (gRel'' - gRel') - (gRel' - gRel)
             *   = (2*DP + 3*SD) - (2*DP + SD)
             *   = 2*SD
             * Note that this value depends only on the (inverse of the)
             * transformation matrix and so is a constant for the loop.
             * To make this all relative to the unit circle, we need to
             * divide all values as follows:
             *   [XY] /= radius
             *   gRel /= radiusSq
             *   DP   /= radiusSq
             *   SD   /= radiusSq
             */
            // coordinates of UL corner in "user space" relative to center
            float rowX = (a00 * x) + (a01 * y) + constA;
            float rowY = (a10 * x) + (a11 * y) + constB;
    
            // second order delta calculated in constructor
            float gDeltaDelta = this.gDeltaDelta;
    
            // adjust is (scan-w) of pixels array, we need (scan)
            adjust += w;
    
            // rgb of the 1.0 color used when the distance exceeds gradient radius
            int rgbclip = gradient[fastGradientArraySize];
    
            for (int j = 0; j < h; j++) {
                // these values depend on the coordinates of the start of the row
                float gRel = (rowX * rowX + rowY * rowY) / centerRadiusSq;
                float gDelta = (2 * (a00 * rowX + a10 * rowY) / centerRadiusSq +
                        gDeltaDelta / 2);
    
                /* Use optimized loops for any cases where gRel >= 1.
                 * We do not need to calculate sqrt(gRel) for these
                 * values since sqrt(N>=1) == (M>=1).
                 * Note that gRel follows a parabola which can only be < 1
                 * for a small region around the center on each scanline. In
                 * particular:
                 *   gDeltaDelta is always positive
                 *   gDelta is <0 until it crosses the midpoint, then >0
                 * To the left and right of that region, it will always be
                 * >=1 out to infinity, so we can process the line in 3
                 * regions:
                 *   out to the left  - quick fill until gRel < 1, updating gRel
                 *   in the heart     - slow fraction=sqrt fill while gRel < 1
                 *   out to the right - quick fill rest of scanline, ignore gRel
                 */
                int i = 0;
                // Quick fill for "out to the left"
                while (i < w && gRel >= 1.0f) {
                    pixels[off + i] = rgbclip;
                    gRel += gDelta;
                    gDelta += gDeltaDelta;
                    i++;
                }
                // Slow fill for "in the heart"
                while (i < w && gRel < 1.0f) {
                    int gIndex;
    
                    if (gRel <= 0) {
                        gIndex = 0;
                    } else {
                        float fIndex = gRel * SQRT_LUT_SIZE;
                        int iIndex = (int) (fIndex);
                        float s0 = sqrtLut[iIndex];
                        float s1 = sqrtLut[iIndex + 1] - s0;
                        fIndex = s0 + (fIndex - iIndex) * s1;
                        gIndex = (int) (fIndex * fastGradientArraySize);
                    }
    
                    // store the color at this point
                    pixels[off + i] = gradient[gIndex];
    
                    // incremental calculation
                    gRel += gDelta;
                    gDelta += gDeltaDelta;
                    i++;
                }
                // Quick fill to end of line for "out to the right"
                while (i < w) {
                    pixels[off + i] = rgbclip;
                    i++;
                }
    
                off += adjust;
                rowX += a01;
                rowY += a11;
            }
        }
    
        // SQRT_LUT_SIZE must be a power of 2 for the test above to work.
        private static final int SQRT_LUT_SIZE = (1 << 11);
        private static final float[] sqrtLut = new float[SQRT_LUT_SIZE + 1];
        static {
            for (int i = 0; i < sqrtLut.length; i++) {
                sqrtLut[i] = (float) Math.sqrt(i / ((float) SQRT_LUT_SIZE));
            }
        }
    
        /**
         * Fill the raster, cycling the gradient colors when a point falls outside
         * of the perimeter of the 100% stop circle.
         * <p>
         * This calculation first computes the intersection point of the line
         * from the focus through the current point in the raster, and the
         * perimeter of the gradient circle.
         * <p>
         * Then it determines the percentage distance of the current point along
         * that line (focus is 0%, perimeter is 100%).
         * <p>
         * Equation of a circle centered at (a,b) with radius r:
         *     (x-a)^2 + (y-b)^2 = r^2
         * Equation of a line with slope m and y-intercept b:
         *     y = mx + b
         * Replacing y in the circle equation and solving using the quadratic
         * formula produces the following set of equations.  Constant factors have
         * been extracted out of the inner loop.
         */
        private void cyclicCircularGradientFillRaster(int[] pixels, int off, int adjust,
                                                      int x, int y,
                                                      int w, int h) {
            // constant part of the C factor of the quadratic equation
            final double constC = -centerRadiusSq + (centerX * centerX) + (centerY * centerY);
    
            // coefficients of the quadratic equation (Ax^2 + Bx + C = 0)
            double A, B, C;
    
            // slope and y-intercept of the focus-perimeter line
            double slope, yintcpt;
    
            // intersection with circle X,Y coordinate
            double solutionX, solutionY;
    
            // constant parts of X, Y coordinates
            final float constX = (a00*x) + (a01*y) + a02;
            final float constY = (a10*x) + (a11*y) + a12;
    
            // constants in inner loop quadratic formula
            final float precalc2 =  2 * centerY;
            final float precalc3 = -2 * centerX;
    
            // value between 0 and 1 specifying position in the gradient
            float g;
    
            // determinant of quadratic formula (should always be > 0)
            float det;
    
            // sq distance from the current point to focus
            float currentToFocusSq;
    
            // sq distance from the intersect point to focus
            float intersectToFocusSq;
    
            // temp variables for change in X,Y squared
            float deltaXSq, deltaYSq;
    
            // used to index pixels array
            int indexer = off;
    
            // incremental index change for pixels array
            int pixInc = w+adjust;
    
            // for every row
            for (int j = 0; j < h; j++) {
    
                // user space point; these are constant from column to column
                float X = (a01*j) + constX;
                float Y = (a11*j) + constY;
    
                // for every column (inner loop begins here)
                for (int i = 0; i < w; i++) {
    
                    if (X == focusX) {
                        // special case to avoid divide by zero
                        solutionX = focusX;
                        solutionY = centerY;
                        solutionY += (Y > focusY) ? trivial : -trivial;
                    } else {
                        // slope and y-intercept of the focus-perimeter line
                        slope = (Y - focusY) / (X - focusX);
                        yintcpt = Y - (slope * X);
    
                        // use the quadratic formula to calculate the
                        // intersection point
                        A = (slope * slope) + 1;
                        B = precalc3 + (-2 * slope * (centerY - yintcpt));
                        C = constC + (yintcpt* (yintcpt - precalc2));
    
                        det = (float)Math.sqrt((B * B) - (4 * A * C));
                        solutionX = -B;
    
                        // choose the positive or negative root depending
                        // on where the X coord lies with respect to the focus
                        solutionX += (X < focusX) ? -det : det;
                        solutionX = solutionX / (2 * A); // divisor
                        solutionY = (slope * solutionX) + yintcpt;
                    }
    
                    // Calculate the square of the distance from the current point
                    // to the focus and the square of the distance from the
                    // intersection point to the focus. Want the squares so we can
                    // do 1 square root after division instead of 2 before.
    
                    deltaXSq = X - focusX;
                    deltaXSq = deltaXSq * deltaXSq;
    
                    deltaYSq = Y - focusY;
                    deltaYSq = deltaYSq * deltaYSq;
    
                    currentToFocusSq = deltaXSq + deltaYSq;
    
                    deltaXSq = (float) solutionX - focusX;
                    deltaXSq = deltaXSq * deltaXSq;
    
                    deltaYSq = (float) solutionY - focusY;
                    deltaYSq = deltaYSq * deltaYSq;
    
                    intersectToFocusSq = deltaXSq + deltaYSq;
    
                    // get the percentage (0-1) of the current point along the
                    // focus-circumference line
                    g = (float) Math.sqrt(currentToFocusSq / intersectToFocusSq);
    
                    // store the color at this point
                    pixels[indexer + i] = indexIntoGradientsArrays(g);
    
                    // incremental change in X, Y
                    X += a00;
                    Y += a10;
                } //end inner loop
    
                indexer += pixInc;
            } //end outer loop
        }
    
        // ported from https://github.com/foo123/Gradient/blob/master/src/Gradient.js
        private void cyclicBiCircularGradientFillRaster(int[] pixels, int off, int adjust,
                                                        int x, int y,
                                                        int w, int h) {
    
            // constant parts of X, Y coordinates
            final float constX = (a00 * x) + (a01 * y) + a02;
            final float constY = (a10 * x) + (a11 * y) + a12;
    
            // used to index pixels array
            int indexer = off;
    
            // incremental index change for pixels array
            int pixInc = w + adjust;


            final float focusXSq = focusX * focusX;
            final float focusYSq = focusY * focusY;
    
            final double A = focusRadiusSq - 2 * focusRadius * centerRadius + centerRadiusSq - focusXSq + 2 * focusX * centerX - centerX * centerX - focusYSq + 2 * focusY * centerY - centerY * centerY;
            final double B = -2 * focusRadiusSq + 2 * focusRadius * centerRadius + 2 * focusXSq - 2 * focusX * centerX + 2 * focusYSq - 2 * focusY * centerY;
            final double C = -focusXSq - focusYSq + focusRadiusSq;
    
            float a, b, c;
    
            // value between 0 and 1 specifying position in the gradient
            float t;
    
            // for every row
            for (int j = 0; j < h; j ++) {
    
                // user space point; these are constant from column to column
                float X = (a01 * j) + constX;
                float Y = (a11 * j) + constY;
    
                // for every column (inner loop begins here)
                for (int i = 0; i < w; i ++) {

                    a = (float) A;
                    b = (float) (B - 2 * X * focusX + 2 * X * centerX - 2 * Y * focusY + 2 * Y * centerY);
                    c = (float) (C - X * X + 2 * X * focusX - Y * Y + 2 * Y * focusY);
    
                    if (strictlyEquals(a, 0)) {
                        if (strictlyEquals(b, 0)) t = -1;
                        else t = -c / b;
                    } else {
                        final float a2 = 2 * a;

                        float d = b * b - 4 * a * c;
                        if (almostEquals(d, 0, 1e-6)) t = -b / a2;
                        else if (d < 0) t = -1;
                        else {
                            float dsqrt = (float) Math.sqrt(d);
                            float s0 = (-b - dsqrt) / a2;
                            float s1 = (-b + dsqrt) / a2;
                            if (0 <= s0 && s0 <= 1 && 0 <= s1 && s1 <= 1) t = Math.min(s0, s1);
                            else if (0 <= s0 && s0 <= 1) t = s0;
                            else if (0 <= s1 && s1 <= 1) t = s1;
                            else t = Math.min(s0, s1);
                        }
                    }
    
                    if (t < 0 || t > 1) {
                        float px = X - focusX, py = Y - focusY, pr = (float) Math.sqrt(px * px + py * py);
                        if (pr < focusRadius) t = 0;
                        else t = 1;
                    }
    
                    pixels[indexer + i] = indexIntoGradientsArrays(t);
    
                    // incremental change in X, Y
                    X += a00;
                    Y += a10;
                } //end inner loop
    
                indexer += pixInc;
            } //end outer loop
        }
    
    }

    private static final double EPSILON = 2.2204460492503130808472633361816E-16;
    private static boolean strictlyEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }
    private static boolean almostEquals(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

}

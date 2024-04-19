/*
 * ┌──┐
 * │  │
 * │Eh│ony
 * └──┘
 */
package unrefined.desktop;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Double pass convolution-based Gaussian blur filter.
 * <p>Implementation is not thread safe.</p>
 */
public class GaussianBlur {

    private static final float[] IDENTITY_KERNEL = new float[] { 1 };

    private float radius;
    private float[] kernel;

    /**
     * Create normalized to range [0, 1] Gaussian distribution in range
     * <tt>[-spread &times; sigma, spread &times; sigma]</tt>.
     * @param sigma standard deviation.
     * @param spread distribution cutoff.
     * @return Array of size <tt>2 &times; ceil(spread &times; sigma) + 1</tt>.
     */
    private static float[] createGaussianDistribution(float sigma, float spread) {
        float[] comp = new float[2 * (int) Math.ceil(spread * sigma) + 1];
        float m = Float.MIN_VALUE;
        for (int i = 0; i < comp.length; i++) {
            // See http://en.wikipedia.org/wiki/Normal_distribution for more info.
            // JIT would introduce constant-related optimizations here,
            // so no excessive variables are required.
            float n = (float) (1 / (sigma * Math.sqrt(2 * Math.PI)) * Math.pow(Math.E, Math.pow(i - comp.length / 2, 2) / -(2 * sigma * sigma)));
            comp[i] = n;
            m = Math.max(n, m);
        }
        for (int i = 0; i < comp.length; i++) {
            comp[i] /= m;
        }
        return comp;
    }

    /**
     * Create 1D normalized to range <tt>[0, 1]</tt> linear convolution kernel for
     * <a href="http://en.wikipedia.org/wiki/Gaussian_blur">Gaussian blur</a>.
     * <p>Rank of kernel is <tt>2 &times; ceil(radius) + 1</tt>.</p>
     * <p>For non-positive radius identity kernel is returned.</p>
     * @param radius required blur radius.
     * @return Linear Gaussian blur kernel.
     */
    private static float[] createGaussianBlurKernel(float radius) {
        if (Float.compare(radius, 0) <= 0) {
            return IDENTITY_KERNEL;
        } else {
            // Spread is chosen to provide best perception experience.
            // Less radius causes neighbouring pixels to have greater weight
            // to make blur effect stronger.
            float spread = Math.min(.5f + radius / 4f, 2.25f);
            return createGaussianDistribution(radius / spread, spread);
        }
    }

    /**
     * Get user-defined blur radius.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Set blur radius.
     * <p>For non-positive radius transformation is omitted.</p>
     * @param radius required blur radius.
     */
    public void setRadius(float radius) {
        this.radius = radius;
        this.kernel = null; // Kernel recalculation required.
    }

    public BufferedImage filter(BufferedImage source, BufferedImage target) {
        int width = source.getWidth();
        int height = source.getHeight();
        if (target == null) {
            target = new BufferedImage(width, height, source.getType());
        }
        if (kernel == null) {
            kernel = createGaussianBlurKernel(radius);
        }
        if (kernel == IDENTITY_KERNEL) {
            Graphics2D g = target.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
        } else {
            int[] a = source.getRGB(0, 0, width, height, null, 0, width);
            int[] b = new int[a.length];
            boolean opaque = !source.getColorModel().hasAlpha();

            int[] offsets = new int[kernel.length];
            for (int i = 0; i < kernel.length; i++) {
                offsets[i] = i - kernel.length / 2;
            }
            convolve(a, b, 0, a.length, kernel, offsets, opaque);
            for (int i = 0; i < kernel.length; i++) {
                offsets[i] *= width;
            }
            convolve(b, a, 0, a.length, kernel, offsets, opaque);
            target.setRGB(0, 0, width, height, a, 0, width);
        }
        return target;
    }

    private static void convolve(int[] source, int[] target, int offset, int length, float[] kernel, int[] offsets, boolean opaque) {
        int end = min(offset + length, source.length);
        for (int i = offset; i < end; i++) {
            float a = 0,
                    r = 0,
                    g = 0,
                    b = 0,
                    alphaRank = 0,
                    colorRank = 0;

            for (int j = 0; j < kernel.length; j++) {
                float k = kernel[j];

                int index = i + offsets[j];
                if (index >= 0 && index < source.length) {
                    int color = source[index];
                    if (!opaque) {
                        alphaRank += k;
                        int alpha = color >> 24 & 0xff;
                        if (alpha == 0) {
                            // Totally transparent colors create undesired moire pattern
                            // during color blending, so they are explicitly excluded.
                            continue;
                        }
                        a += k * alpha;
                    }
                    colorRank += k;
                    r += k * (color >> 16 & 0xff);
                    g += k * (color >> 8  & 0xff);
                    b += k * (color >> 0  & 0xff);
                }
            }
            target[i] = 0xff << 24; // Opaque black.

            if (alphaRank != 0) {
                target[i] = clamp(a / alphaRank) << 24;
            }
            if (colorRank != 0) {
                target[i] = target[i]
                        | clamp(r / colorRank) << 16
                        | clamp(g / colorRank) << 8
                        | clamp(b / colorRank) << 0;
            }
        }
    }

    private static int clamp(float channel) {
        return min(255, max(0, (int) channel)) & 0xff;
    }

}

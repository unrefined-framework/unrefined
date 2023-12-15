package unrefined.desktop;

import unrefined.util.function.IntBiOperator;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;
import java.util.Objects;

import static unrefined.media.graphics.Composite.Mode.*;

public class ExtendedComposite implements Composite {

    private static boolean isIntRGB(ColorModel model) {
        if (model instanceof DirectColorModel directColorModel &&
                model.getTransferType() == DataBuffer.TYPE_INT) {
            return directColorModel.getRedMask() == 0x00FF0000 &&
                    directColorModel.getGreenMask() == 0x0000FF00 &&
                    directColorModel.getBlueMask() == 0x000000FF &&
                    (directColorModel.getNumComponents() == 3 ||
                            directColorModel.getAlphaMask() == 0xFF000000);
        }

        return false;
    }

    private static boolean isIntBGR(ColorModel model) {
        if (model instanceof DirectColorModel directColorModel &&
                model.getTransferType() == DataBuffer.TYPE_INT) {
            return directColorModel.getRedMask() == 0x000000FF &&
                    directColorModel.getGreenMask() == 0x0000FF00 &&
                    directColorModel.getBlueMask() == 0x00FF0000 &&
                    (directColorModel.getNumComponents() == 3 ||
                            directColorModel.getAlphaMask() == 0xFF000000);
        }

        return false;
    }

    private static abstract class BlendingContext implements CompositeContext {
        protected final ExtendedComposite composite;
        public BlendingContext(ExtendedComposite composite) {
            this.composite = composite;
        }
        @Override
        public void dispose() {}
    }

    private static class BlendingIntRGBContext extends BlendingContext {
        public BlendingIntRGBContext(ExtendedComposite composite) {
            super(composite);
        }
        @Override
        public void compose(Raster srcIn, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(srcIn.getWidth(), dstIn.getWidth());
            int height = Math.min(srcIn.getHeight(), dstIn.getHeight());

            float alpha = composite.alpha;

            int[] srcPixels = new int[width];
            int[] dstPixels = new int[width];

            for (int y = 0; y < height; y ++) {
                srcIn.getDataElements(0, y, width, 1, srcPixels);
                dstIn.getDataElements(0, y, width, 1, dstPixels);
                for (int x = 0; x < width; x ++) {

                    int dstPixel = dstPixels[x];
                    dstPixels[x] = blendAlphaIntRGB(dstPixel, composite.composeProc.apply(srcPixels[x], dstPixel), alpha);
                }
                dstOut.setDataElements(0, y, width, 1, dstPixels);
            }
        }
    }

    private static int blendAlphaIntRGB(int dstIn, int dstOut, float alpha) {
        int a = (dstIn >> 24) & 0xFF;
        int r = (dstIn >> 16) & 0xFF;
        int g = (dstIn >> 8) & 0xFF;
        int b = dstIn & 0xFF;
        return ((int) (a + (((dstOut >> 24) & 0xFF) - a) * alpha) & 0xFF) << 24 |
               ((int) (r + (((dstOut >> 16) & 0xFF) - r) * alpha) & 0xFF) << 16 |
               ((int) (g + (((dstOut >> 8) & 0xFF)  - g) * alpha) & 0xFF) << 8  |
               ((int) (b + ((dstOut & 0xFF)         - b) * alpha) & 0xFF);
    }

    private static class BlendingIntBGRContext extends BlendingContext {
        public BlendingIntBGRContext(ExtendedComposite composite) {
            super(composite);
        }
        @Override
        public void compose(Raster srcIn, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(srcIn.getWidth(), dstIn.getWidth());
            int height = Math.min(srcIn.getHeight(), dstIn.getHeight());

            float alpha = composite.alpha;

            int[] srcPixels = new int[width];
            int[] dstPixels = new int[width];

            for (int y = 0; y < height; y ++) {
                srcIn.getDataElements(0, y, width, 1, srcPixels);
                dstIn.getDataElements(0, y, width, 1, dstPixels);
                for (int x = 0; x < width; x ++) {

                    int dstPixel = toIntRGB(dstPixels[x]);
                    dstPixels[x] = blendAlphaIntBGR(dstPixel, composite.composeProc.apply(srcPixels[x], dstPixel), alpha);
                }
                dstOut.setDataElements(0, y, width, 1, dstPixels);
            }
        }
    }

    private static int toIntRGB(int bgr) {
        return (bgr >> 24) & 0xFF |
               bgr & 0xFF |
               (bgr >> 8) & 0xFF |
               (bgr >> 16) & 0xFF;
    }

    private static int blendAlphaIntBGR(int dstIn, int dstOut, float alpha) {
        int a = (dstIn >> 24) & 0xFF;
        int r = (dstIn >> 16) & 0xFF;
        int g = (dstIn >> 8) & 0xFF;
        int b = dstIn & 0xFF;
        return ((int) (a + (((dstOut >> 24) & 0xFF) - a) * alpha) & 0xFF) << 24 |
               ((int) (b + ((dstOut & 0xFF)         - b) * alpha) & 0xFF) << 16 |
               ((int) (g + (((dstOut >> 8) & 0xFF)  - g) * alpha) & 0xFF) << 8  |
               ((int) (r + (((dstOut >> 16) & 0xFF) - r) * alpha) & 0xFF);
    }

    private final IntBiOperator composeProc;
    private final int mode;
    private final float alpha;

    private ExtendedComposite(int mode, float alpha) {
        this.mode = mode;
        this.composeProc = getComposeProc(mode);
        this.alpha = alpha;
    }

    private ExtendedComposite(int mode) {
        this(mode, 1.0f);
    }

    private ExtendedComposite(IntBiOperator composeProc, float alpha) {
        this.mode = CUSTOM;
        this.composeProc = Objects.requireNonNull(composeProc);
        this.alpha = alpha;
    }

    public int getMode() {
        return mode;
    }

    public float getAlpha() {
        return alpha;
    }

    private static IntBiOperator getComposeProc(int mode) {
        return switch (mode) {
            case PLUS -> PlusProc;
            case MULTIPLY -> MultiplyProc;
            case SCREEN -> ScreenProc;
            case OVERLAY -> OverlayProc;
            case DARKEN -> DarkenProc;
            case LIGHTEN -> LightenProc;
            case COLOR_DODGE -> ColorDodgeProc;
            case COLOR_BURN -> ColorBurnProc;
            case HARD_LIGHT -> HardlightProc;
            case SOFT_LIGHT -> SoftlightProc;
            case DIFFERENCE -> DifferenceProc;
            case EXCLUSION -> ExclusionProc;
            case HUE -> HueProc;
            case SATURATION -> SaturationProc;
            case COLOR -> ColorProc;
            case LUMINOSITY -> LuminosityProc;
            default -> throw new IllegalArgumentException("Illegal composite mode: " + mode);
        };
    }

    private static final IntBiOperator PlusProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int ra = Math.min(255, aa + ba);
        int rr = Math.min(255, ar + br);
        int rg = Math.min(255, ag + bg);
        int rb = Math.min(255, ab + bb);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator MultiplyProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        int rr = (ar * br + 2) >> 8;
        int rg = (ag * bg + 2) >> 8;
        int rb = (ab * bb + 2) >> 8;
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator ScreenProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        int rr = 255 - ((255 - ar) * (255 - br) >> 8);
        int rg = 255 - ((255 - ag) * (255 - bg) >> 8);
        int rb = 255 - ((255 - ab) * (255 - bb) >> 8);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator OverlayProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = br < 128 ? br * ar >> 7
                : 255 - ((255 - br) * (255 - ar) >> 7);
        int rg = bg < 128 ? bg * ag >> 7
                : 255 - ((255 - bg) * (255 - ag) >> 7);
        int rb = bb < 128 ? bb * ab >> 7
                : 255 - ((255 - bb) * (255 - ab) >> 7);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator DarkenProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = Math.min(ar, br);
        int rg = Math.min(ag, bg);
        int rb = Math.min(ab, bb);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator LightenProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = Math.max(ar, br);
        int rg = Math.max(ag, bg);
        int rb = Math.max(ab, bb);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator ColorDodgeProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = ar == 255 ? 255 : Math.min((br << 8) / (255 - ar), 255);
        int rg = ag == 255 ? 255 : Math.min((bg << 8) / (255 - ag), 255);
        int rb = ab == 255 ? 255 : Math.min((bb << 8) / (255 - ab), 255);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator ColorBurnProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = ar == 0 ? 0 : Math.max(0, 255 - (((255 - br) << 8) / ar));
        int rg = ag == 0 ? 0 : Math.max(0, 255 - (((255 - bg) << 8) / ag));
        int rb = ab == 0 ? 0 : Math.max(0, 255 - (((255 - bb) << 8) / ab));
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator HardlightProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = ar < 128 ? br * ar >> 7
                : 255 - ((255 - ar) * (255 - br) >> 7);
        int rg = ag < 128 ? bg * ag >> 7
                : 255 - ((255 - ag) * (255 - bg) >> 7);
        int rb = ab < 128 ? bb * ab >> 7
                : 255 - ((255 - ab) * (255 - bb) >> 7);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator SoftlightProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int mRed = ar * br / 255;
        int mGreen = ag * bg / 255;
        int mBlue = ab * bb / 255;
        int rr = mRed + br * (255 - ((255 - br) * (255 - ar) / 255) - mRed) / 255;
        int rg = mGreen + bg * (255 - ((255 - bg) * (255 - ag) / 255) - mGreen) / 255;
        int rb = mBlue + bb * (255 - ((255 - bb) * (255 - ab) / 255) - mBlue) / 255;
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator DifferenceProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = Math.abs(br - ar);
        int rg = Math.abs(bg - ag);
        int rb = Math.abs(bb - ab);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator ExclusionProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int rr = br + ar - (br * ar >> 7);
        int rg = bg + ag - (bg * ag >> 7);
        int rb = bb + ab - (bb * ab >> 7);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rr << 16) | (rg << 8) | rb;
    };
    private static final IntBiOperator HueProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        float[] srcHSL = new float[3];
        unrefined.media.graphics.Color.hsl(ar, ag, ab, srcHSL);
        float[] dstHSL = new float[3];
        unrefined.media.graphics.Color.hsl(br, bg, bb, dstHSL);

        int[] rrgb = new int[3];
        unrefined.media.graphics.Color.hsl(srcHSL[0], dstHSL[1], dstHSL[2], rrgb);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rrgb[0] << 16) | (rrgb[1] << 8) | rrgb[2];
    };
    private static final IntBiOperator SaturationProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        float[] srcHSL = new float[3];
        unrefined.media.graphics.Color.hsl(ar, ag, ab, srcHSL);
        float[] dstHSL = new float[3];
        unrefined.media.graphics.Color.hsl(br, bg, bb, dstHSL);

        int[] rrgb = new int[3];
        unrefined.media.graphics.Color.hsl(dstHSL[0], srcHSL[1], dstHSL[2], rrgb);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rrgb[0] << 16) | (rrgb[1] << 8) | rrgb[2];
    };
    private static final IntBiOperator ColorProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        float[] srcHSL = new float[3];
        unrefined.media.graphics.Color.hsl(ar, ag, ab, srcHSL);
        float[] dstHSL = new float[3];
        unrefined.media.graphics.Color.hsl(br, bg, bb, dstHSL);

        int[] rrgb = new int[3];
        unrefined.media.graphics.Color.hsl(srcHSL[0], srcHSL[1], dstHSL[2], rrgb);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rrgb[0] << 16) | (rrgb[1] << 8) | rrgb[2];
    };
    private static final IntBiOperator LuminosityProc = (a, b) -> {
        int aa = (a >> 24) & 0xFF;
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int ba = (b >> 24) & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;

        float[] srcHSL = new float[3];
        unrefined.media.graphics.Color.hsl(ar, ag, ab, srcHSL);
        float[] dstHSL = new float[3];
        unrefined.media.graphics.Color.hsl(br, bg, bb, dstHSL);

        int[] rrgb = new int[3];
        unrefined.media.graphics.Color.hsl(dstHSL[0], dstHSL[1], srcHSL[2], rrgb);
        int ra = Math.min(255, aa + ba - (aa * ba) / 255);
        return (ra << 24) | (rrgb[0] << 16) | (rrgb[1] << 8) | rrgb[2];
    };

    public static final ExtendedComposite Plus = new ExtendedComposite(PLUS);
    public static final ExtendedComposite Multiply = new ExtendedComposite(MULTIPLY);
    public static final ExtendedComposite Screen = new ExtendedComposite(SCREEN);
    public static final ExtendedComposite Overlay = new ExtendedComposite(OVERLAY);
    public static final ExtendedComposite Darken = new ExtendedComposite(DARKEN);
    public static final ExtendedComposite Lighten = new ExtendedComposite(LIGHTEN);
    public static final ExtendedComposite ColorDodge = new ExtendedComposite(COLOR_DODGE);
    public static final ExtendedComposite ColorBurn = new ExtendedComposite(COLOR_BURN);
    public static final ExtendedComposite HardLight = new ExtendedComposite(HARD_LIGHT);
    public static final ExtendedComposite SoftLight = new ExtendedComposite(SOFT_LIGHT);
    public static final ExtendedComposite Difference = new ExtendedComposite(DIFFERENCE);
    public static final ExtendedComposite Exclusion = new ExtendedComposite(EXCLUSION);
    public static final ExtendedComposite Hue = new ExtendedComposite(HUE);
    public static final ExtendedComposite Saturation = new ExtendedComposite(SATURATION);
    public static final ExtendedComposite Color = new ExtendedComposite(COLOR);
    public static final ExtendedComposite Luminosity = new ExtendedComposite(LUMINOSITY);

    public static Composite getInstance(int mode) {
        return switch (mode) {
            case SRC_OVER -> AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
            case SRC_IN -> AlphaComposite.getInstance(AlphaComposite.SRC_IN);
            case SRC_OUT -> AlphaComposite.getInstance(AlphaComposite.SRC_OUT);
            case SRC_ATOP -> AlphaComposite.getInstance(AlphaComposite.SRC_ATOP);
            case DST_OVER -> AlphaComposite.getInstance(AlphaComposite.DST_OVER);
            case DST_IN -> AlphaComposite.getInstance(AlphaComposite.DST_IN);
            case DST_OUT -> AlphaComposite.getInstance(AlphaComposite.DST_OUT);
            case DST_ATOP -> AlphaComposite.getInstance(AlphaComposite.DST_ATOP);
            case PLUS -> Plus;
            case COPY -> AlphaComposite.getInstance(AlphaComposite.DST);
            case XOR -> AlphaComposite.getInstance(AlphaComposite.XOR);
            case MULTIPLY -> Multiply;
            case SCREEN -> Screen;
            case OVERLAY -> Overlay;
            case DARKEN -> Darken;
            case LIGHTEN -> Lighten;
            case COLOR_DODGE -> ColorDodge;
            case COLOR_BURN -> ColorBurn;
            case HARD_LIGHT -> HardLight;
            case SOFT_LIGHT -> SoftLight;
            case DIFFERENCE -> Difference;
            case EXCLUSION -> Exclusion;
            case HUE -> Hue;
            case SATURATION -> Saturation;
            case COLOR -> Color;
            case LUMINOSITY -> Luminosity;
            case DISCARD -> AlphaComposite.getInstance(AlphaComposite.SRC);
            case CLEAR -> AlphaComposite.getInstance(AlphaComposite.CLEAR);
            default -> throw new IllegalArgumentException("Illegal composite mode: " + mode);
        };
    }

    public static Composite getInstance(int mode, float alpha) {
        alpha = Math.clamp(alpha, 0, 1);
        if (alpha == 1.0f) return getInstance(mode);
        else return switch (mode) {
            case SRC_OVER -> AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            case SRC_IN -> AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha);
            case SRC_OUT -> AlphaComposite.getInstance(AlphaComposite.SRC_OUT, alpha);
            case SRC_ATOP -> AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha);
            case DST_OVER -> AlphaComposite.getInstance(AlphaComposite.DST_OVER, alpha);
            case DST_IN -> AlphaComposite.getInstance(AlphaComposite.DST_IN, alpha);
            case DST_OUT -> AlphaComposite.getInstance(AlphaComposite.DST_OUT, alpha);
            case DST_ATOP -> AlphaComposite.getInstance(AlphaComposite.DST_ATOP, alpha);
            case PLUS -> new ExtendedComposite(PLUS, alpha);
            case COPY -> AlphaComposite.getInstance(AlphaComposite.DST, alpha);
            case XOR -> AlphaComposite.getInstance(AlphaComposite.XOR, alpha);
            case MULTIPLY -> new ExtendedComposite(MULTIPLY, alpha);
            case SCREEN -> new ExtendedComposite(SCREEN, alpha);
            case OVERLAY -> new ExtendedComposite(OVERLAY, alpha);
            case DARKEN -> new ExtendedComposite(DARKEN, alpha);
            case LIGHTEN -> new ExtendedComposite(LIGHTEN, alpha);
            case COLOR_DODGE -> new ExtendedComposite(COLOR_DODGE, alpha);
            case COLOR_BURN -> new ExtendedComposite(COLOR_BURN, alpha);
            case HARD_LIGHT -> new ExtendedComposite(HARD_LIGHT, alpha);
            case SOFT_LIGHT -> new ExtendedComposite(SOFT_LIGHT, alpha);
            case DIFFERENCE -> new ExtendedComposite(DIFFERENCE, alpha);
            case EXCLUSION -> new ExtendedComposite(EXCLUSION, alpha);
            case HUE -> new ExtendedComposite(HUE, alpha);
            case SATURATION -> new ExtendedComposite(SATURATION, alpha);
            case COLOR -> new ExtendedComposite(COLOR, alpha);
            case LUMINOSITY -> new ExtendedComposite(LUMINOSITY, alpha);
            case DISCARD -> AlphaComposite.getInstance(AlphaComposite.SRC, alpha);
            case CLEAR -> AlphaComposite.getInstance(AlphaComposite.CLEAR, alpha);
            default -> throw new IllegalArgumentException("Illegal composite mode: " + mode);
        };
    }

    public static ExtendedComposite getInstance(IntBiOperator composeProc, float alpha) {
        return new ExtendedComposite(composeProc, Math.clamp(alpha, 0, 1));
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        if (isIntRGB(srcColorModel) && isIntRGB(dstColorModel)) return new BlendingIntRGBContext(this);
        else if (isIntBGR(srcColorModel) && isIntBGR(dstColorModel)) return new BlendingIntBGRContext(this);
        else throw new RasterFormatException("Incompatible color models:\n  " + srcColorModel + "\n  " + dstColorModel);
    }

    public Composite derive(int mode) {
        return mode == this.mode ? this : getInstance(mode, alpha);
    }

    public Composite derive(float alpha) {
        return alpha == this.alpha ? this : getInstance(mode, alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExtendedComposite that = (ExtendedComposite) o;

        if (mode != that.mode) return false;
        if (Float.compare(that.alpha, alpha) != 0) return false;
        return composeProc.equals(that.composeProc);
    }

    @Override
    public int hashCode() {
        int result = composeProc.hashCode();
        result = 31 * result + mode;
        result = 31 * result + (alpha != +0.0f ? Float.floatToIntBits(alpha) : 0);
        return result;
    }

}

package unrefined.desktop;

import unrefined.internal.AWTUtils;
import unrefined.util.NotInstantiableError;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;

import static unrefined.media.graphics.Bitmap.Type.*;

public final class BitmapImageFactory {

    private BitmapImageFactory() {
        throw new NotInstantiableError(BitmapImageFactory.class);
    }

    public static BufferedImage deriveImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        WritableRaster raster = bufferedImage.getRaster().getWritableParent();
        if (raster == null) return bufferedImage.getSubimage(x, y, width, height);
        else return new BufferedImage(bufferedImage.getColorModel(),
                raster.createWritableChild(raster.getMinX() + x, raster.getMinY() + y,
                        width, height, 0, 0, null),
                bufferedImage.isAlphaPremultiplied(),
                AWTUtils.getProperties(bufferedImage));
    }

    public static BufferedImage deriveImage(BufferedImage bufferedImage) {
        return deriveImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public static BufferedImage copyImage(BufferedImage bufferedImage) {
        WritableRaster raster = bufferedImage.getRaster().getWritableParent();
        if (raster == null) return new BufferedImage(bufferedImage.getColorModel(),
                bufferedImage.copyData(null), bufferedImage.isAlphaPremultiplied(),
                AWTUtils.getProperties(bufferedImage));
        else {
            WritableRaster writableRaster = bufferedImage.getRaster();
            return new BufferedImage(bufferedImage.getColorModel(),
                    raster.createWritableChild(raster.getMinX(), raster.getMinY(),
                            writableRaster.getWidth(), writableRaster.getHeight(),
                            writableRaster.getMinX(), writableRaster.getMinY(), null),
                    bufferedImage.isAlphaPremultiplied(),
                    AWTUtils.getProperties(bufferedImage));
        }
    }

    private static final ColorSpace COLOR_SPACE_SRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);

    public static BufferedImage createImage(int width, int height, int bitmapType) {
        switch (bitmapType) {
            case RGBA_8888 -> {
                return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }
            case RGBA_4444 -> {
                ColorModel model = new DirectColorModel(COLOR_SPACE_SRGB, 16,
                        0x0F00, 0x00F0, 0x000F, 0xF000,
                        false, DataBuffer.TYPE_USHORT);
                return new BufferedImage(model, model.createCompatibleWritableRaster(width, height), false, null);
            }
            case RGB_565 -> {
                return new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
            }
            case ALPHA_8 -> {
                ColorModel model = new DirectColorModel(COLOR_SPACE_SRGB, 8,
                        0, 0, 0, 0xFF,
                        false, DataBuffer.TYPE_BYTE);
                return new BufferedImage(model, model.createCompatibleWritableRaster(width, height), false, null);
            }
            default -> throw new IllegalArgumentException("Illegal bitmap type: " + bitmapType);
        }
    }

    public static int getBitmapType(BufferedImage bufferedImage) {
        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_INT_ARGB -> {
                return RGBA_8888;
            }
            case BufferedImage.TYPE_USHORT_565_RGB -> {
                return RGB_565;
            }
            case BufferedImage.TYPE_CUSTOM -> {
                ColorModel model = bufferedImage.getColorModel();
                if (model instanceof DirectColorModel directColorModel
                        && model.getColorSpace().equals(COLOR_SPACE_SRGB)
                        && !model.isAlphaPremultiplied()) {
                    switch (model.getTransferType()) {
                        case DataBuffer.TYPE_BYTE -> {
                            if (directColorModel.getRedMask() == 0
                                    && directColorModel.getGreenMask() == 0
                                    && directColorModel.getBlueMask() == 0
                                    && directColorModel.getAlphaMask() == 0xFF) return ALPHA_8;
                        }
                        case DataBuffer.TYPE_USHORT -> {
                            if (directColorModel.getRedMask() == 0x0F00
                                    && directColorModel.getGreenMask() == 0x00F0
                                    && directColorModel.getBlueMask() == 0x000F
                                    && directColorModel.getAlphaMask() == 0xF000) return RGBA_4444;
                        }
                    }
                }
                throw new IllegalArgumentException("Illegal color model: " + model);
            }
            default -> throw new IllegalArgumentException("Illegal image type: " + bufferedImage.getType());
        }
    }

    public static int getBufferedImageType(int bitmapType) {
        return switch (bitmapType) {
            case RGBA_8888, RGBA_4444, ALPHA_8 -> BufferedImage.TYPE_INT_ARGB;
            case RGB_565 -> BufferedImage.TYPE_USHORT_565_RGB;
            default -> throw new IllegalArgumentException("Illegal bitmap type: " + bitmapType);
        };
    }

    public static BufferedImage getImage(BufferedImage bufferedImage, int bitmapType, boolean dispose) {
        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_INT_ARGB -> {
                if (bitmapType == RGBA_8888) return bufferedImage;
            }
            case BufferedImage.TYPE_USHORT_565_RGB -> {
                if (bitmapType == RGB_565) return bufferedImage;
            }
            case BufferedImage.TYPE_CUSTOM -> {
                ColorModel model = bufferedImage.getColorModel();
                if (model instanceof DirectColorModel directColorModel
                        && model.getColorSpace().equals(COLOR_SPACE_SRGB)
                        && !model.isAlphaPremultiplied()) {
                    switch (model.getTransferType()) {
                        case DataBuffer.TYPE_BYTE -> {
                            if (directColorModel.getRedMask() == 0
                                    && directColorModel.getGreenMask() == 0
                                    && directColorModel.getBlueMask() == 0
                                    && directColorModel.getAlphaMask() == 0xFF) {
                                if (bitmapType == ALPHA_8) return bufferedImage;
                            }
                        }
                        case DataBuffer.TYPE_USHORT -> {
                            if (directColorModel.getRedMask() == 0x0F00
                                    && directColorModel.getGreenMask() == 0x00F0
                                    && directColorModel.getBlueMask() == 0x000F
                                    && directColorModel.getAlphaMask() == 0xF000) {
                                if (bitmapType == RGBA_4444) return bufferedImage;
                            }
                        }
                    }
                }
            }
        }
        if (bitmapType == -1) bitmapType = bufferedImage.getColorModel().hasAlpha() ? RGBA_8888 : RGB_565;
        BufferedImage image = createImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bitmapType);
        Graphics2D graphics2D = image.createGraphics();
        try {
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            graphics2D.drawImage(bufferedImage, 0, 0, null);
        }
        finally {
            graphics2D.dispose();
            if (dispose) bufferedImage.flush();
        }
        return image;
    }

    public static BufferedImage getBufferedImage(BufferedImage bufferedImage, int type, boolean dispose) {
        if (type == BufferedImage.TYPE_CUSTOM) throw new IllegalArgumentException("Illegal image type: " + type);
        else if (bufferedImage.getType() == type) return bufferedImage;
        else {
            BufferedImage image = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), type);
            Graphics2D graphics2D = image.createGraphics();
            try {
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
                graphics2D.drawImage(bufferedImage, 0, 0, null);
            }
            finally {
                graphics2D.dispose();
                if (dispose) bufferedImage.flush();
            }
            return image;
        }
    }

}

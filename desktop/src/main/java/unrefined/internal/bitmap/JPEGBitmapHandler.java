package unrefined.internal.bitmap;

import unrefined.desktop.BitmapImageFactory;
import unrefined.desktop.IIOBitmapHandler;
import unrefined.internal.IOUtils;
import unrefined.media.graphics.Bitmap;
import unrefined.runtime.DesktopBitmap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class JPEGBitmapHandler extends IIOBitmapHandler {

    public static final JPEGBitmapHandler INSTANCE = new JPEGBitmapHandler();

    private static final Set<String> READER_FORMAT_NAMES = Set.of( "jpg", "jpeg" );
    private static final Set<String> WRITER_FORMAT_NAMES = Set.of( "jpg", "jpeg" );

    private static final int JPEG_SOI = 0xD8;
    private static boolean isJPEG(ImageInputStream input) throws IOException {
        int b0, b1;
        input.mark();
        try {
            b0 = input.read();
            b1 = input.read();
        }
        catch (EOFException e) {
            return false;
        }
        finally {
            input.reset();
        }
        return b0 == 0xFF && b1 == JPEG_SOI;
    }

    private static ImageReader getJPEGImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpeg");
        if (it.hasNext()) return it.next();
        else return null;
    }

    private static ImageWriter getJPEGImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("jpeg");
        if (it.hasNext()) return it.next();
        else return null;
    }

    @Override
    public Set<String> readerFormats() {
        return READER_FORMAT_NAMES;
    }

    @Override
    public Set<String> writerFormats() {
        return WRITER_FORMAT_NAMES;
    }

    @Override
    public Bitmap read(ImageInputStream input, int type) throws IOException {
        if (!isJPEG(input)) return null;
        ImageReader reader = getJPEGImageReader();
        if (reader == null) return null;
        reader.setInput(input, false, true);
        try {
            return new DesktopBitmap(BitmapImageFactory.getImage(reader.read(0), type, true));
        }
        finally {
            reader.dispose();
            IOUtils.closeQuietly(input);
        }
    }

    @Override
    public boolean write(Bitmap bitmap, ImageOutputStream output, String format, float quality) throws IOException {
        if (!WRITER_FORMAT_NAMES.contains(format.toLowerCase(Locale.ENGLISH))) return false;
        ImageWriter writer = getJPEGImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("JPEG");
            param.setCompressionQuality(Math.clamp(quality, 0, 1));
        }
        BufferedImage raw = ((DesktopBitmap) bitmap).getBufferedImage();
        BufferedImage image = switch (bitmap.getType()) {
            case Bitmap.Type.ALPHA_8, Bitmap.Type.RGBA_4444, Bitmap.Type.RGBA_8888 ->
                    BitmapImageFactory.getBufferedImage(raw, BufferedImage.TYPE_3BYTE_BGR, false);
            default -> raw;
        };
        try {
            writer.write(null, new IIOImage(image, null, null), param);
        }
        finally {
            output.flush();
            writer.dispose();
            if (image != raw) image.flush();
        }
        return true;
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int type) throws IOException {
        return null;
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int[] types, int typesOffset) throws IOException {
        return null;
    }

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float quality) throws IOException {
        return false;
    }

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float[] quality, int qualityOffset) throws IOException {
        return false;
    }

}

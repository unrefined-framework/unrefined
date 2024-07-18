package unrefined.desktop.bitmap;

import unrefined.desktop.BitmapSupport;
import unrefined.desktop.IIOBitmapHandler;
import unrefined.math.FastMath;
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
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class PNGBitmapHandler extends IIOBitmapHandler {

    public static final PNGBitmapHandler INSTANCE = new PNGBitmapHandler();

    private static final Set<String> READER_FORMAT_NAMES = Collections.singleton( "png" );
    private static final Set<String> WRITER_FORMAT_NAMES = Collections.singleton( "png" );

    private static boolean isPNG(ImageInputStream input) throws IOException {
        byte[] b = new byte[8];
        input.mark();
        try {
            input.readFully(b);
        }
        catch (EOFException e) {
            return false;
        }
        finally {
            input.reset();
        }
        return (b[0] == (byte) 137 &&
                b[1] == (byte) 80  &&
                b[2] == (byte) 78  &&
                b[3] == (byte) 71  &&
                b[4] == (byte) 13  &&
                b[5] == (byte) 10  &&
                b[6] == (byte) 26  &&
                b[7] == (byte) 10);
    }

    private static ImageReader getPNGImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("png");
        if (it.hasNext()) return it.next();
        else return null;
    }

    private static ImageWriter getPNGImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
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
        if (!isPNG(input)) return null;
        ImageReader reader = getPNGImageReader();
        if (reader == null) return null;
        reader.setInput(input, false, true);
        try {
            return new DesktopBitmap(BitmapSupport.getImage(reader.read(0), type, true));
        }
        finally {
            reader.dispose();
            try {
                input.close();
            }
            catch (IOException ignored) {
            }
        }
    }

    @Override
    public boolean write(Bitmap bitmap, ImageOutputStream output, String format, float quality) throws IOException {
        if (!WRITER_FORMAT_NAMES.contains(format.toLowerCase(Locale.ENGLISH))) return false;
        ImageWriter writer = getPNGImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("Deflate");
            param.setCompressionQuality(FastMath.clamp(quality, 0, 1));
        }
        BufferedImage raw = ((DesktopBitmap) bitmap).getBufferedImage();
        BufferedImage image;
        switch (bitmap.getType()) {
            case Bitmap.Type.ALPHA_8:
            case Bitmap.Type.RGBA_4444:
                image = BitmapSupport.getBufferedImage(raw, BufferedImage.TYPE_INT_ARGB, false);
                break;
            default:
                image = raw;
                break;
        }
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

package unrefined.internal.bitmap;

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

public class BMPBitmapHandler extends IIOBitmapHandler {

    public static final BMPBitmapHandler INSTANCE = new BMPBitmapHandler();

    private static final Set<String> READER_FORMAT_NAMES = Collections.singleton( "bmp" );
    private static final Set<String> WRITER_FORMAT_NAMES = Collections.singleton( "bmp" );

    private static boolean isBMP(ImageInputStream input) throws IOException {
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
        return b[0] == 0x42 && b[1] == 0x4d;
    }

    private static ImageReader getBMPImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("bmp");
        if (it.hasNext()) return it.next();
        else return null;
    }

    private static ImageWriter getBMPImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("bmp");
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
        if (!isBMP(input)) return null;
        ImageReader reader = getBMPImageReader();
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
        ImageWriter writer = getBMPImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("BI_RGB");
            param.setCompressionQuality(FastMath.clamp(quality, 0, 1));
        }
        BufferedImage image = BitmapSupport.getBufferedImage(((DesktopBitmap) bitmap).getBufferedImage(),
                BufferedImage.TYPE_3BYTE_BGR, false);
        try {
            writer.write(null, new IIOImage(image, null, null), param);
        }
        finally {
            output.flush();
            writer.dispose();
            image.flush();
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

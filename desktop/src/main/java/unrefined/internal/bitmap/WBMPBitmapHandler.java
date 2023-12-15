package unrefined.internal.bitmap;

import unrefined.desktop.BitmapImageFactory;
import unrefined.desktop.IIOBitmapHandler;
import unrefined.internal.IOUtils;
import unrefined.media.graphics.Bitmap;
import unrefined.runtime.DesktopBitmap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class WBMPBitmapHandler extends IIOBitmapHandler {

    public static final WBMPBitmapHandler INSTANCE = new WBMPBitmapHandler();

    private static final int MAX_WBMP_WIDTH = 1024;
    private static final int MAX_WBMP_HEIGHT = 768;

    private static final Set<String> READER_FORMAT_NAMES = Set.of( "wbmp" );
    private static final Set<String> WRITER_FORMAT_NAMES = Set.of( "wbmp" );

    private static boolean isWBMP(ImageInputStream input) throws IOException {
        input.mark();
        try {
            int type = input.readByte();
            int fixedHeader= input.readByte();

            if (type != 0 || fixedHeader != 0) return false;

            int width = readVInt(input);
            int height = readVInt(input);

            if (width <= 0 || height <= 0) return false;

            long dataLength = input.length();

            if (dataLength == -1) return (width < MAX_WBMP_WIDTH) && (height < MAX_WBMP_HEIGHT);

            dataLength -= input.getStreamPosition();

            long scanSize = (width / 8) + ((width % 8) == 0 ? 0 : 1);

            return (dataLength == scanSize * height);
        }
        catch (EOFException e) {
            return false;
        }
        finally {
            input.reset();
        }
    }

    private static int readVInt(ImageInputStream input) throws IOException {
        int value = input.readByte();
        int result = value & 0x7F;
        while ((value & 0x80) == 0x80) {
            result <<= 7;
            value = input.readByte();
            result |= (value & 0x7F);
        }
        return result;
    }

    private static ImageReader getWBMPImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("wbmp");
        if (it.hasNext()) return it.next();
        else return null;
    }

    private static ImageWriter getWBMPImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("wbmp");
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
        if (!isWBMP(input)) return null;
        ImageReader reader = getWBMPImageReader();
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
        ImageWriter writer = getWBMPImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        BufferedImage image = BitmapImageFactory.getBufferedImage(((DesktopBitmap) bitmap).getBufferedImage(),
                BufferedImage.TYPE_BYTE_BINARY, false);
        try {
            writer.write(null, new IIOImage(image, null, null), null);
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

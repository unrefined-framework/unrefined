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
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.plugins.tiff.TIFFField;
import javax.imageio.plugins.tiff.TIFFTag;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class TIFFBitmapHandler extends IIOBitmapHandler {

    public static final TIFFBitmapHandler INSTANCE = new TIFFBitmapHandler();

    private static final int NUMBER_X_RESOLUTION = 282;
    private static final TIFFTag TAG_X_RESOLUTION = new TIFFTag("TIFFRationals", NUMBER_X_RESOLUTION, 1 << TIFFTag.TIFF_RATIONAL, 1);
    private static final int NUMBER_Y_RESOLUTION = 283;
    private static final TIFFTag TAG_Y_RESOLUTION = new TIFFTag("TIFFRationals", NUMBER_Y_RESOLUTION, 1 << TIFFTag.TIFF_RATIONAL, 1);
    private static final int NUMBER_X_POSITION = 286;
    private static final TIFFTag TAG_X_POSITION = new TIFFTag("TIFFRationals", NUMBER_X_POSITION, 1 << TIFFTag.TIFF_RATIONAL, 1);
    private static final int NUMBER_Y_POSITION = 287;
    private static final TIFFTag TAG_Y_POSITION = new TIFFTag("TIFFRationals", NUMBER_Y_POSITION, 1 << TIFFTag.TIFF_RATIONAL, 1);

    private static final Set<String> READER_FORMAT_NAMES = Set.of( "tiff" );
    private static final Set<String> WRITER_FORMAT_NAMES = Set.of( "tiff" );

    private static boolean isTIFF(ImageInputStream input) throws IOException {
        byte[] b = new byte[4];
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
        return ((
                b[0] == (byte) 0x49 &&
                        b[1] == (byte) 0x49 &&
                        b[2] == (byte) 0x2a &&
                        b[3] == (byte) 0x00
        ) || (
                b[0] == (byte) 0x4d &&
                        b[1] == (byte) 0x4d &&
                        b[2] == (byte) 0x00 &&
                        b[3] == (byte) 0x2a
        ));
    }

    private static ImageReader getTIFFImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("tiff");
        if (it.hasNext()) return it.next();
        else return null;
    }

    private static ImageWriter getTIFFImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("tiff");
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
        return null;
    }

    @Override
    public boolean write(Bitmap bitmap, ImageOutputStream output, String format, float quality) throws IOException {
        return false;
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int type) throws IOException {
        if (!isTIFF(input)) return null;
        ImageReader reader = getTIFFImageReader();
        if (reader == null) return null;
        reader.setInput(input);
        try {
            Bitmap.Frame[] frames = new Bitmap.Frame[reader.getNumImages(true)];
            for (int i = 0; i < frames.length; i ++) {
                TIFFDirectory tiffDirectory = TIFFDirectory.createFromMetadata(reader.getImageMetadata(i));
                double resolutionX = tiffDirectory.getTIFFField(NUMBER_X_RESOLUTION).getAsDouble(0);
                double resolutionY = tiffDirectory.getTIFFField(NUMBER_Y_RESOLUTION).getAsDouble(0);
                double positionX = tiffDirectory.getTIFFField(NUMBER_X_POSITION).getAsDouble(0);
                double positionY = tiffDirectory.getTIFFField(NUMBER_Y_POSITION).getAsDouble(0);
                frames[i] = new Bitmap.Frame(new DesktopBitmap(BitmapImageFactory.getImage(reader.read(i), type, true)),
                        (int) (positionX * resolutionX), (int) (positionY * resolutionY),
                        0, Bitmap.DisposalMode.NONE, Bitmap.BlendMode.SOURCE);
            }
            return new Bitmap.MultiFrame(frames);
        }
        finally {
            reader.dispose();
            IOUtils.closeQuietly(input);
        }
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int[] types, int typesOffset) throws IOException {
        if (!isTIFF(input)) return null;
        ImageReader reader = getTIFFImageReader();
        if (reader == null) return null;
        reader.setInput(input);
        try {
            Bitmap.Frame[] frames = new Bitmap.Frame[reader.getNumImages(true)];
            for (int i = 0; i < frames.length; i ++) {
                TIFFDirectory IFD = TIFFDirectory.createFromMetadata(reader.getImageMetadata(i));
                double resolutionX = IFD.getTIFFField(NUMBER_X_RESOLUTION).getAsDouble(0);
                double resolutionY = IFD.getTIFFField(NUMBER_Y_RESOLUTION).getAsDouble(0);
                double positionX = IFD.getTIFFField(NUMBER_X_POSITION).getAsDouble(0);
                double positionY = IFD.getTIFFField(NUMBER_Y_POSITION).getAsDouble(0);
                frames[i] = new Bitmap.Frame(new DesktopBitmap(BitmapImageFactory.getImage(reader.read(i), types[typesOffset + i], true)),
                        (int) (positionX * resolutionX), (int) (positionY * resolutionY),
                        0, Bitmap.DisposalMode.NONE, Bitmap.BlendMode.SOURCE);
            }
            return new Bitmap.MultiFrame(frames);
        }
        finally {
            reader.dispose();
            IOUtils.closeQuietly(input);
        }
    }

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float quality) throws IOException {
        if (!WRITER_FORMAT_NAMES.contains(format.toLowerCase(Locale.ENGLISH))) return false;
        ImageWriter writer = getTIFFImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(quality < 1.0f ? "Deflate" : "LZW");
            param.setCompressionQuality(Math.clamp(quality, 0, 1));
        }
        try {
            writer.prepareWriteSequence(null);
            try {
                TIFFDirectory IFD = TIFFDirectory.createFromMetadata(writer.getDefaultImageMetadata(null, param));
                for (Bitmap.Frame frame : frames) {
                    int bufferedImageType = BitmapImageFactory.getBufferedImageType(frame.getBitmap().getType());
                    BufferedImage raw = ((DesktopBitmap) frame.getBitmap()).getBufferedImage();
                    BufferedImage image = BitmapImageFactory.getBufferedImage(raw, bufferedImageType, false);
                    IFD.addTIFFField(new TIFFField(TAG_X_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ 96, 1 }}));
                    IFD.addTIFFField(new TIFFField(TAG_Y_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ 96, 1 }}));
                    IFD.addTIFFField(new TIFFField(TAG_X_POSITION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ frame.getHotSpotX(), 96 }}));
                    IFD.addTIFFField(new TIFFField(TAG_Y_POSITION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ frame.getHotSpotY(), 96 }}));
                    try {
                        writer.writeToSequence(new IIOImage(image, null, IFD.getAsMetadata()), param);
                    }
                    finally {
                        if (image != raw) image.flush();
                    }
                }
            }
            finally {
                writer.endWriteSequence();
            }
        }
        finally {
            writer.dispose();
            output.flush();
        }
        return true;
    }

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float[] quality, int qualityOffset) throws IOException {
        if (!WRITER_FORMAT_NAMES.contains(format.toLowerCase(Locale.ENGLISH))) return false;
        ImageWriter writer = getTIFFImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        try {
            writer.prepareWriteSequence(null);
            try {
                TIFFDirectory IFD = TIFFDirectory.createFromMetadata(writer.getDefaultImageMetadata(null, param));
                for (int i = 0; i < frames.size(); i ++) {
                    Bitmap.Frame frame = frames.get(i);
                    int bufferedImageType = BitmapImageFactory.getBufferedImageType(frame.getBitmap().getType());
                    BufferedImage raw = ((DesktopBitmap) frame.getBitmap()).getBufferedImage();
                    BufferedImage image = BitmapImageFactory.getBufferedImage(raw, bufferedImageType, false);
                    IFD.addTIFFField(new TIFFField(TAG_X_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ 96, 1 }}));
                    IFD.addTIFFField(new TIFFField(TAG_Y_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ 96, 1 }}));
                    IFD.addTIFFField(new TIFFField(TAG_X_POSITION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ frame.getHotSpotX(), 96 }}));
                    IFD.addTIFFField(new TIFFField(TAG_Y_POSITION, TIFFTag.TIFF_RATIONAL, 1, new long[][] {{ frame.getHotSpotY(), 96 }}));
                    if (param.canWriteCompressed()) {
                        float q = quality[qualityOffset + i];
                        param.setCompressionType(q < 1.0f ? "Deflate" : "LZW");
                        param.setCompressionQuality(Math.clamp(q, 0, 1));
                    }
                    try {
                        writer.writeToSequence(new IIOImage(image, null, IFD.getAsMetadata()), param);
                    }
                    finally {
                        if (image != raw) image.flush();
                    }
                }
            }
            finally {
                writer.endWriteSequence();
            }
        }
        finally {
            writer.dispose();
            output.flush();
        }
        return true;
    }

}

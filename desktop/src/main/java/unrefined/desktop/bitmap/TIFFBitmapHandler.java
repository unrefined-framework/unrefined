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
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class TIFFBitmapHandler extends IIOBitmapHandler {

    public static final TIFFBitmapHandler INSTANCE = new TIFFBitmapHandler();

    private static final String NATIVE_FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_image_1.0";

    private static final int TAG_X_RESOLUTION = 282;
    private static final int TAG_Y_RESOLUTION = 283;
    private static final int TAG_X_POSITION = 286;
    private static final int TAG_Y_POSITION = 287;

    private static final Set<String> READER_FORMAT_NAMES = Collections.singleton( "tiff" );
    private static final Set<String> WRITER_FORMAT_NAMES = Collections.singleton( "tiff" );

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

    private static IIOMetadataNode generateMetadata(Bitmap.Frame frame) {
        IIOMetadataNode root = new IIOMetadataNode(NATIVE_FORMAT_NAME);
        IIOMetadataNode ifd = new IIOMetadataNode("TIFFIFD");
        ifd.appendChild(generateTIFFRational(TAG_X_RESOLUTION, 96, 1));
        ifd.appendChild(generateTIFFRational(TAG_Y_RESOLUTION, 96, 1));
        ifd.appendChild(generateTIFFRational(TAG_X_POSITION, frame.getHotSpotX(), 96));
        ifd.appendChild(generateTIFFRational(TAG_Y_POSITION, frame.getHotSpotY(), 96));
        root.appendChild(ifd);
        return root;
    }

    private static IIOMetadataNode generateTIFFRational(int number, long fraction, long denominator) {
        IIOMetadataNode field = new IIOMetadataNode("TIFFField");
        field.setAttribute("number", Integer.toString(number));
        IIOMetadataNode type = new IIOMetadataNode("TIFFRationals");
        IIOMetadataNode value = new IIOMetadataNode("TIFFRational");
        value.setAttribute("value", fraction + "/" + denominator);
        type.appendChild(value);
        field.appendChild(type);
        return field;
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
            IIOMetadataNode ifd;
            IIOMetadataNode node;
            String[] tmp;
            long xrf, xrd, xpf, xpd, yrf, yrd, ypf, ypd;
            xrf = xrd = xpf = xpd = yrf = yrd = ypf = ypd = 1;
            for (int i = 0; i < frames.length; i ++) {
                ifd = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(NATIVE_FORMAT_NAME).getFirstChild();
                node = (IIOMetadataNode) ifd.getFirstChild();
                while (node != null) {
                    if ("TIFFField".equals(node.getNodeName())) {
                        switch (Integer.parseInt(node.getAttribute("number"))) {
                            case TAG_X_RESOLUTION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                xrf = Long.parseLong(tmp[0]);
                                xrd = Long.parseLong(tmp[1]);
                                break;
                            case TAG_Y_RESOLUTION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                yrf = Long.parseLong(tmp[0]);
                                yrd = Long.parseLong(tmp[1]);
                                break;
                            case TAG_X_POSITION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                xpf = Long.parseLong(tmp[0]);
                                xpd = Long.parseLong(tmp[1]);
                                break;
                            case TAG_Y_POSITION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                ypf = Long.parseLong(tmp[0]);
                                ypd = Long.parseLong(tmp[1]);
                                break;
                        }
                    }
                    node = (IIOMetadataNode) node.getNextSibling();
                }
                frames[i] = new Bitmap.Frame(new DesktopBitmap(BitmapSupport.getImage(reader.read(i), type, true)),
                        (int) (xpf * xrf / xpd / xrd), (int) (ypf * yrf / ypd / yrd), 0, Bitmap.DisposalMode.NONE, Bitmap.BlendMode.SOURCE);
            }
            return new Bitmap.MultiFrame(frames);
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
    public Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int[] types, int typesOffset) throws IOException {
        if (!isTIFF(input)) return null;
        ImageReader reader = getTIFFImageReader();
        if (reader == null) return null;
        reader.setInput(input);
        try {
            Bitmap.Frame[] frames = new Bitmap.Frame[reader.getNumImages(true)];
            IIOMetadataNode ifd;
            IIOMetadataNode node;
            String[] tmp;
            long xrf, xrd, xpf, xpd, yrf, yrd, ypf, ypd;
            xrf = xrd = xpf = xpd = yrf = yrd = ypf = ypd = 1;
            for (int i = 0; i < frames.length; i ++) {
                ifd = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(NATIVE_FORMAT_NAME).getFirstChild();
                node = (IIOMetadataNode) ifd.getFirstChild();
                while (node != null) {
                    if ("TIFFField".equals(node.getNodeName())) {
                        switch (Integer.parseInt(node.getAttribute("number"))) {
                            case TAG_X_RESOLUTION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                xrf = Long.parseLong(tmp[0]);
                                xrd = Long.parseLong(tmp[1]);
                                break;
                            case TAG_Y_RESOLUTION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                yrf = Long.parseLong(tmp[0]);
                                yrd = Long.parseLong(tmp[1]);
                                break;
                            case TAG_X_POSITION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                xpf = Long.parseLong(tmp[0]);
                                xpd = Long.parseLong(tmp[1]);
                                break;
                            case TAG_Y_POSITION:
                                tmp = ((IIOMetadataNode) node.getFirstChild().getFirstChild()).getAttribute("value").split("/");
                                ypf = Long.parseLong(tmp[0]);
                                ypd = Long.parseLong(tmp[1]);
                                break;
                        }
                    }
                    node = (IIOMetadataNode) node.getNextSibling();
                }
                frames[i] = new Bitmap.Frame(new DesktopBitmap(BitmapSupport.getImage(reader.read(i), types[typesOffset + i], true)),
                        (int) (xpf * xrf / xpd / xrd), (int) (ypf * yrf / ypd / yrd), 0, Bitmap.DisposalMode.NONE, Bitmap.BlendMode.SOURCE);
            }
            return new Bitmap.MultiFrame(frames);
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
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float quality) throws IOException {
        if (!WRITER_FORMAT_NAMES.contains(format.toLowerCase(Locale.ENGLISH))) return false;
        ImageWriter writer = getTIFFImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType(quality < 1.0f ? "Deflate" : "LZW");
            param.setCompressionQuality(FastMath.clamp(quality, 0, 1));
        }
        try {
            writer.prepareWriteSequence(null);
            try {
                IIOMetadata metadata = writer.getDefaultImageMetadata(null, param);
                for (Bitmap.Frame frame : frames) {
                    int bufferedImageType = BitmapSupport.getBufferedImageType(frame.getBitmap().getType());
                    BufferedImage raw = ((DesktopBitmap) frame.getBitmap()).getBufferedImage();
                    BufferedImage image = BitmapSupport.getBufferedImage(raw, bufferedImageType, false);
                    metadata.mergeTree(NATIVE_FORMAT_NAME, generateMetadata(frame));
                    try {
                        writer.writeToSequence(new IIOImage(image, null, metadata), param);
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
                IIOMetadata metadata = writer.getDefaultImageMetadata(null, param);
                for (int i = 0; i < frames.size(); i ++) {
                    Bitmap.Frame frame = frames.get(i);
                    int bufferedImageType = BitmapSupport.getBufferedImageType(frame.getBitmap().getType());
                    BufferedImage raw = ((DesktopBitmap) frame.getBitmap()).getBufferedImage();
                    BufferedImage image = BitmapSupport.getBufferedImage(raw, bufferedImageType, false);
                    metadata.mergeTree(NATIVE_FORMAT_NAME, generateMetadata(frame));
                    if (param.canWriteCompressed()) {
                        float q = quality[qualityOffset + i];
                        param.setCompressionType(q < 1.0f ? "Deflate" : "LZW");
                        param.setCompressionQuality(FastMath.clamp(q, 0, 1));
                    }
                    try {
                        writer.writeToSequence(new IIOImage(image, null, metadata), param);
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

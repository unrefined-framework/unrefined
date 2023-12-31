package unrefined.internal.bitmap;

import unrefined.desktop.BitmapSupport;
import unrefined.desktop.IIOBitmapHandler;
import unrefined.math.FastMath;
import unrefined.media.graphics.Bitmap;
import unrefined.runtime.DesktopBitmap;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class GIFBitmapHandler extends IIOBitmapHandler {

    public static final GIFBitmapHandler INSTANCE = new GIFBitmapHandler();

    private static final String METADATA_FORMAT_NAME = "javax_imageio_gif_image_1.0";

    private static final Set<String> READER_FORMAT_NAMES = Collections.singleton( "gif" );
    private static final Set<String> WRITER_FORMAT_NAMES = Collections.singleton( "gif" );

    private static ImageReader getGIFImageReader() {
        Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("gif");
        if (it.hasNext()) return it.next();
        return null;
    }

    private static ImageWriter getGIFImageWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("gif");
        if (it.hasNext()) return it.next();
        else return null;
    }

    private static boolean isGIF(ImageInputStream input) throws IOException {
        StringBuilder id = new StringBuilder();
        input.mark();
        try {
            for (int i = 0; i < 6; i ++) {
                id.append((char) input.read());
            }
        }
        catch (EOFException e) {
            return false;
        }
        finally {
            input.reset();
        }
        return id.toString().startsWith("GIF");
    }

    private static int parseDisposalMethod(String disposalMethod) {
        if ("restoreToBackgroundColor".equals(disposalMethod)) return Bitmap.DisposalMode.BACKGROUND;
        else if ("restoreToPrevious".equals(disposalMethod)) return Bitmap.DisposalMode.PREVIOUS;
        else return Bitmap.DisposalMode.NONE;
    }

    private static String getDisposalMethod(int disposalMode) {
        switch (disposalMode) {
            case Bitmap.DisposalMode.BACKGROUND: return "restoreToBackgroundColor";
            case Bitmap.DisposalMode.PREVIOUS: return "restoreToPrevious";
            case Bitmap.DisposalMode.NONE: return null;
            default: throw new IllegalArgumentException("Illegal disposal mode: " + disposalMode);
        }
    }

    private static void mergeMetadata(IIOMetadataNode root, Bitmap.MultiFrame frames) {
        IIOMetadataNode node = new IIOMetadataNode("ApplicationExtensions");
        IIOMetadataNode appExtNode = new IIOMetadataNode("ApplicationExtension");
        appExtNode.setAttribute("applicationID", "NETSCAPE");
        appExtNode.setAttribute("authenticationCode", "2.0");
        appExtNode.setUserObject(Integer.toString(frames.getLooping() + 1).getBytes(StandardCharsets.UTF_8));
        node.appendChild(appExtNode);
        root.appendChild(node);
    }

    private static void mergeMetadata(IIOMetadataNode root, Bitmap.Frame frame) {
        Bitmap bitmap = frame.getBitmap();
        IIOMetadataNode node = (IIOMetadataNode) root.getFirstChild();
        String name;
        while (node != null) {
            name = node.getNodeName();
            if (name.equals("ImageDescriptor")) {
                node.setAttribute("imageLeftPosition",
                        Integer.toString(frame.getHotSpotX()));
                node.setAttribute("imageTopPosition",
                        Integer.toString(frame.getHotSpotY()));
                node.setAttribute("imageWidth", Integer.toString(bitmap.getWidth()));
                node.setAttribute("imageHeight", Integer.toString(bitmap.getHeight()));
            }
            else if (name.equals("GraphicControlExtension")) {
                String disposalMethod = getDisposalMethod(frame.getDisposalMode());
                if (disposalMethod != null) node.setAttribute("disposalMethod",disposalMethod);
                node.setAttribute("delayTime",
                        Integer.toString(FastMath.clamp(frame.getDuration() / 10L, Integer.MIN_VALUE, Integer.MAX_VALUE)));
            }
            node = (IIOMetadataNode) node.getNextSibling();
        }
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
        if (!isGIF(input)) return null;
        ImageReader reader = getGIFImageReader();
        if (reader == null) return null;
        reader.setInput(input);
        try {
            Bitmap.Frame[] frames = new Bitmap.Frame[reader.getNumImages(true)];
            IIOMetadataNode node;
            int hotSpotX = 0, hotSpotY = 0, loops = 0;
            long duration = 0;
            int disposalMode = Bitmap.DisposalMode.NONE;
            for (int i = 0; i < frames.length; i ++) {
                node = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(METADATA_FORMAT_NAME).getFirstChild();
                String name;
                while (node != null) {
                    name = node.getNodeName();
                    switch (name) {
                        case "ImageDescriptor":
                            hotSpotX = Integer.parseInt(node.getAttribute("imageLeftPosition"));
                            hotSpotY = Integer.parseInt(node.getAttribute("imageTopPosition"));
                            break;
                        case "GraphicControlExtension":
                            duration = Integer.parseInt(node.getAttribute("delayTime")) * 10L;
                            disposalMode = parseDisposalMethod(node.getAttribute("disposalMethod"));
                            break;
                        case "ApplicationExtensions":
                            IIOMetadataNode appExtNode = (IIOMetadataNode) node.getFirstChild();
                            while (appExtNode != null) {
                                if (appExtNode.getNodeName().equals("ApplicationExtension") &&
                                        appExtNode.getAttribute("applicationID").equals("NETSCAPE") &&
                                        appExtNode.getAttribute("authenticationCode").equals("2.0")) {
                                    Object userObject = appExtNode.getUserObject();
                                    if (userObject instanceof byte[]) {
                                        loops = ByteBuffer.wrap((byte[]) userObject).getShort() - 1;
                                    }
                                }
                                appExtNode = (IIOMetadataNode) appExtNode.getNextSibling();
                            }
                            break;
                    }
                    node = (IIOMetadataNode) node.getNextSibling();
                }
                frames[i] = new Bitmap.Frame(
                        new DesktopBitmap(BitmapSupport.getImage(reader.read(i), type, true)),
                        hotSpotX, hotSpotY, duration, disposalMode, Bitmap.BlendMode.SOURCE);
            }
            Bitmap.MultiFrame result = new Bitmap.MultiFrame(frames);
            result.setLooping(loops);
            return result;
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
        if (!isGIF(input)) return null;
        ImageReader reader = getGIFImageReader();
        if (reader == null) return null;
        reader.setInput(input);
        try {
            Bitmap.Frame[] frames = new Bitmap.Frame[reader.getNumImages(true)];
            IIOMetadataNode node;
            int hotSpotX = 0, hotSpotY = 0, loops = 0;
            long duration = 0;
            int disposalMode = Bitmap.DisposalMode.NONE;
            for (int i = 0; i < frames.length; i ++) {
                node = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(METADATA_FORMAT_NAME).getFirstChild();
                String name;
                while (node != null) {
                    name = node.getNodeName();
                    switch (name) {
                        case "ImageDescriptor":
                            hotSpotX = Integer.parseInt(node.getAttribute("imageLeftPosition"));
                            hotSpotY = Integer.parseInt(node.getAttribute("imageTopPosition"));
                            break;
                        case "GraphicControlExtension":
                            duration = Integer.parseInt(node.getAttribute("delayTime")) * 10L;
                            disposalMode = parseDisposalMethod(node.getAttribute("disposalMethod"));
                            break;
                        case "ApplicationExtensions":
                            IIOMetadataNode appExtNode = (IIOMetadataNode) node.getFirstChild();
                            while (appExtNode != null) {
                                if (appExtNode.getNodeName().equals("ApplicationExtension") &&
                                        appExtNode.getAttribute("applicationID").equals("NETSCAPE") &&
                                        appExtNode.getAttribute("authenticationCode").equals("2.0")) {
                                    Object userObject = appExtNode.getUserObject();
                                    if (userObject instanceof byte[]) {
                                        loops = ByteBuffer.wrap((byte[]) userObject).getShort() - 1;
                                    }
                                }
                                appExtNode = (IIOMetadataNode) appExtNode.getNextSibling();
                            }
                            break;
                    }
                    node = (IIOMetadataNode) node.getNextSibling();
                }
                frames[i] = new Bitmap.Frame(
                        new DesktopBitmap(BitmapSupport.getImage(reader.read(i), types[i + typesOffset], true)),
                        hotSpotX, hotSpotY, duration, disposalMode, Bitmap.BlendMode.SOURCE);
            }
            Bitmap.MultiFrame result = new Bitmap.MultiFrame(frames);
            result.setLooping(loops);
            return result;
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
        ImageWriter writer = getGIFImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("LZW");
            param.setCompressionQuality(FastMath.clamp(quality, 0, 1));
        }
        try {
            writer.prepareWriteSequence(null);
            try {
                ImageTypeSpecifier specifier = ImageTypeSpecifier.createFromBufferedImageType(
                        BitmapSupport.getBufferedImageType(frames.getCompatibleType()));
                IIOMetadata metadata = writer.getDefaultImageMetadata(specifier, param);
                IIOMetadataNode metadataTree = (IIOMetadataNode) metadata.getAsTree( METADATA_FORMAT_NAME );
                mergeMetadata(metadataTree, frames);
                for (Bitmap.Frame frame : frames) {
                    mergeMetadata(metadataTree, frame);
                    metadata.mergeTree( METADATA_FORMAT_NAME, metadataTree );
                    writer.writeToSequence(new IIOImage(((DesktopBitmap) frame.getBitmap()).getBufferedImage(),
                            null, metadata), param);
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
        ImageWriter writer = getGIFImageWriter();
        if (writer == null) return false;
        writer.setOutput(output);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("LZW");
        }
        try {
            writer.prepareWriteSequence(null);
            try {
                ImageTypeSpecifier specifier = ImageTypeSpecifier.createFromBufferedImageType(
                        BitmapSupport.getBufferedImageType(frames.getCompatibleType()));
                IIOMetadata metadata = writer.getDefaultImageMetadata(specifier, param);
                IIOMetadataNode metadataTree = (IIOMetadataNode) metadata.getAsTree(METADATA_FORMAT_NAME);
                mergeMetadata(metadataTree, frames);
                for (int i = 0; i < frames.size(); i ++) {
                    Bitmap.Frame frame = frames.get(i);
                    mergeMetadata(metadataTree, frame);
                    metadata.mergeTree(METADATA_FORMAT_NAME, metadataTree);
                    if (param.canWriteCompressed()) param.setCompressionQuality(FastMath.clamp(quality[qualityOffset + i], 0, 1));
                    writer.writeToSequence(new IIOImage(((DesktopBitmap) frame.getBitmap()).getBufferedImage(),
                            null, metadata), param);
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

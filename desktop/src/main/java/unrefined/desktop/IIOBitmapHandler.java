package unrefined.desktop;

import unrefined.io.asset.Asset;
import unrefined.media.graphics.Bitmap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class IIOBitmapHandler extends Bitmap.Handler {

    public abstract Bitmap read(ImageInputStream input, int type) throws IOException;

    @Override
    public Bitmap read(File input, int type) throws IOException {
        return read(ImageIO.createImageInputStream(input), type);
    }

    @Override
    public Bitmap read(InputStream input, int type) throws IOException {
        return read(ImageIO.createImageInputStream(input), type);
    }

    @Override
    public Bitmap read(Asset input, int type) throws IOException {
        return read(ImageIO.createImageInputStream(input.openStream()), type);
    }

    public abstract boolean write(Bitmap bitmap, ImageOutputStream output, String format, float quality) throws IOException;

    @Override
    public boolean write(Bitmap bitmap, File output, String format, float quality) throws IOException {
        return write(bitmap, ImageIO.createImageOutputStream(output), format, quality);
    }

    @Override
    public boolean write(Bitmap bitmap, OutputStream output, String format, float quality) throws IOException {
        return write(bitmap, ImageIO.createImageOutputStream(output), format, quality);
    }

    public abstract Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int type) throws IOException;

    @Override
    public Bitmap.MultiFrame readMultiFrame(File input, int type) throws IOException {
        return readMultiFrame(ImageIO.createImageInputStream(input), type);
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(InputStream input, int type) throws IOException {
        return readMultiFrame(ImageIO.createImageInputStream(input), type);
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(Asset input, int type) throws IOException {
        return readMultiFrame(ImageIO.createImageInputStream(input.openStream()), type);
    }

    public abstract Bitmap.MultiFrame readMultiFrame(ImageInputStream input, int[] types, int typesOffset) throws IOException;

    @Override
    public Bitmap.MultiFrame readMultiFrame(File input, int[] types, int typesOffset) throws IOException {
        return readMultiFrame(ImageIO.createImageInputStream(input), types, typesOffset);
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(InputStream input, int[] types, int typesOffset) throws IOException {
        return readMultiFrame(ImageIO.createImageInputStream(input), types, typesOffset);
    }

    @Override
    public Bitmap.MultiFrame readMultiFrame(Asset input, int[] types, int typesOffset) throws IOException {
        return readMultiFrame(ImageIO.createImageInputStream(input.openStream()), types, typesOffset);
    }

    public abstract boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float quality) throws IOException;

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, File output, String format, float quality) throws IOException {
        return writeMultiFrame(frames, ImageIO.createImageOutputStream(output), format, quality);
    }

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float quality) throws IOException {
        return writeMultiFrame(frames, ImageIO.createImageOutputStream(output), format, quality);
    }

    public abstract boolean writeMultiFrame(Bitmap.MultiFrame frames, ImageOutputStream output, String format, float[] quality, int qualityOffset) throws IOException;

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, File output, String format, float[] quality, int qualityOffset) throws IOException {
        return writeMultiFrame(frames, ImageIO.createImageOutputStream(output), format, quality, qualityOffset);
    }

    @Override
    public boolean writeMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float[] quality, int qualityOffset) throws IOException {
        return writeMultiFrame(frames, ImageIO.createImageOutputStream(output), format, quality, qualityOffset);
    }

}

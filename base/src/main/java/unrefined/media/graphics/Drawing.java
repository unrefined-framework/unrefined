package unrefined.media.graphics;

import unrefined.context.Environment;
import unrefined.io.UnsupportedFormatException;
import unrefined.io.asset.Asset;
import unrefined.runtime.AnimatedCursor;
import unrefined.util.concurrent.ConcurrentHashSet;
import unrefined.util.function.IntBiOperator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Drawing {

    private static volatile Drawing INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static Drawing getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global().get("unrefined.runtime.drawing", Drawing.class);
        }
        return INSTANCE;
    }

    private final Set<Bitmap.Handler> bitmapHandlers = new ConcurrentHashSet<>();
    public Set<Bitmap.Handler> bitmapHandlers() {
        return bitmapHandlers;
    }

    public abstract Composite createComposite(int mode, float alpha);
    public Composite createComposite(int mode) {
        return createComposite(mode, 1);
    }
    public abstract Composite createComposite(IntBiOperator composeProc, float alpha);
    public Composite createComposite(IntBiOperator composeProc) {
        return createComposite(composeProc, 1);
    }

    public abstract Brush createBrush(int color);
    public abstract Brush createBrush(float x1, float y1, float x2, float y2,
                                      float[] stops, int stopsOffset, int[] colors, int colorsOffset, int length,
                                      int tileMode);
    public Brush createBrush(float x1, float y1, float x2, float y2,
                                      float[] stops, int[] colors,
                                      int tileMode) {
        if (stops.length != colors.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return createBrush(x1, y1, x2, y2, stops, 0, colors, 0, stops.length, tileMode);
    }
    public abstract Brush createBrush(float x1, float y1, float r1, float x2, float y2, float r2,
                                      float[] stops, int stopsOffset, int[] colors, int colorsOffset, int length,
                                      int tileMode);
    public Brush createBrush(float x1, float y1, float r1, float x2, float y2, float r2,
                                      float[] stops, int[] colors,
                                      int tileMode) {
        if (stops.length != colors.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return createBrush(x1, y1, r1, x2, y2, r2, stops, 0, colors, 0, stops.length, tileMode);
    }
    public abstract Brush createBrush(Bitmap bitmap, Transform transform);
    public abstract Brush createBrush(IntBiOperator paintProc);

    public abstract Path createPath(int fillRule);
    public Path createPath() {
        return createPath(Path.FillRule.NON_ZERO);
    }
    public Path createPath(Path path) {
        Path result = createPath();
        result.from(path);
        return result;
    }

    public abstract Transform createTransform();
    public Transform createTransform(Transform transform) {
        Transform result = createTransform();
        result.from(transform);
        return result;
    }
    public Transform createTransform(float sx, float kx, float dx, float ky, float sy, float dy) {
        Transform result = createTransform();
        result.setTransform(sx, kx, dx, ky, sy, dy);
        return result;
    }
    public Transform createTransform(float... values) {
        Transform result = createTransform();
        result.setValues(values);
        return result;
    }
    public Transform createScaleTransform(float sx, float sy) {
        Transform result = createTransform();
        result.setScale(sx, sy);
        return result;
    }
    public Transform createSkewTransform(float kx, float ky) {
        Transform result = createTransform();
        result.setSkew(kx, ky);
        return result;
    }
    public Transform createTranslateTransform(float dx, float dy) {
        Transform result = createTransform();
        result.setTranslate(dx, dy);
        return result;
    }
    public Transform createRotateTransform(float radians, float px, float py) {
        Transform result = createTransform();
        result.setRotate(radians, px, py);
        return result;
    }
    public Transform createRotateTransform(float radians) {
        Transform result = createTransform();
        result.setRotate(radians);
        return result;
    }

    public abstract Bitmap createBitmap(int width, int height, int type);

    public Bitmap readBitmap(File input) throws IOException {
        return readBitmap(input, -1);
    }
    public Bitmap readBitmap(InputStream input) throws IOException {
        return readBitmap(input, -1);
    }
    public Bitmap readBitmap(Asset input) throws IOException {
        return readBitmap(input, -1);
    }
    public Bitmap readBitmap(File input, int type) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap bitmap = handler.read(input, type);
            if (bitmap != null) return bitmap;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap readBitmap(InputStream input, int type) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap bitmap = handler.read(input, type);
            if (bitmap != null) return bitmap;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap readBitmap(Asset input, int type) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap bitmap = handler.read(input, type);
            if (bitmap != null) return bitmap;
        }
        throw new UnsupportedFormatException();
    }
    public void writeBitmap(Bitmap bitmap, File output, String format, float quality) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            if (handler.write(bitmap, output, format, quality)) return;
        }
        throw new UnsupportedFormatException();
    }
    public void writeBitmap(Bitmap bitmap, OutputStream output, String format, float quality) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            if (handler.write(bitmap, output, format, quality)) return;
        }
        throw new UnsupportedFormatException();
    }

    public Bitmap.MultiFrame readBitmapMultiFrame(File input) throws IOException {
        return readBitmapMultiFrame(input, -1);
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(InputStream input) throws IOException {
        return readBitmapMultiFrame(input, -1);
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(Asset input) throws IOException {
        return readBitmapMultiFrame(input, -1);
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(File input, int type) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap.MultiFrame frames = handler.readMultiFrame(input, type);
            if (frames != null) return frames;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(InputStream input, int type) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap.MultiFrame frames = handler.readMultiFrame(input, type);
            if (frames != null) return frames;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(Asset input, int type) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap.MultiFrame frames = handler.readMultiFrame(input, type);
            if (frames != null) return frames;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(File input, int[] types, int typesOffset) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap.MultiFrame frames = handler.readMultiFrame(input, types, typesOffset);
            if (frames != null) return frames;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(InputStream input, int[] types, int typesOffset) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap.MultiFrame frames = handler.readMultiFrame(input, types, typesOffset);
            if (frames != null) return frames;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(Asset input, int[] types, int typesOffset) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            Bitmap.MultiFrame frames = handler.readMultiFrame(input, types, typesOffset);
            if (frames != null) return frames;
        }
        throw new UnsupportedFormatException();
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(File input, int[] types) throws IOException {
        return readBitmapMultiFrame(input, types, 0);
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(InputStream input, int[] types) throws IOException {
        return readBitmapMultiFrame(input, types, 0);
    }
    public Bitmap.MultiFrame readBitmapMultiFrame(Asset input, int[] types) throws IOException {
        return readBitmapMultiFrame(input, types, 0);
    }
    public void writeBitmapMultiFrame(Bitmap.MultiFrame frames, File output, String format, float quality) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            if (handler.writeMultiFrame(frames, output, format, quality)) return;
        }
        throw new UnsupportedFormatException();
    }
    public void writeBitmapMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float quality) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            if (handler.writeMultiFrame(frames, output, format, quality)) return;
        }
        throw new UnsupportedFormatException();
    }
    public void writeBitmapMultiFrame(Bitmap.MultiFrame frames, File output, String format, float[] quality, int qualityOffset) throws IOException {
        for (Bitmap.Handler handler : bitmapHandlers()) {
            if (handler.writeMultiFrame(frames, output, format, quality, qualityOffset)) return;
        }
        throw new UnsupportedFormatException();
    }
    public void writeBitmapMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float[] quality, int qualityOffset) throws IOException {
        if (frames.size() != (quality.length - qualityOffset)) throw new IndexOutOfBoundsException("Array length mismatch");
        for (Bitmap.Handler handler : bitmapHandlers()) {
            if (handler.writeMultiFrame(frames, output, format, quality, qualityOffset)) return;
        }
        throw new UnsupportedFormatException();
    }
    public void writeBitmapMultiFrame(Bitmap.MultiFrame frames, File output, String format, float[] quality) throws IOException {
        writeBitmapMultiFrame(frames, output, format, quality, 0);
    }
    public void writeBitmapMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float[] quality) throws IOException {
        writeBitmapMultiFrame(frames, output, format, quality, 0);
    }

    public Set<String> getBitmapReaderFormats() {
        Set<String> formats = new HashSet<>();
        for (Bitmap.Handler handler : bitmapHandlers()) {
            formats.addAll(handler.readerFormats());
        }
        return Collections.unmodifiableSet(formats);
    }
    public Set<String> getBitmapWriterFormats() {
        Set<String> formats = new HashSet<>();
        for (Bitmap.Handler handler : bitmapHandlers()) {
            formats.addAll(handler.writerFormats());
        }
        return Collections.unmodifiableSet(formats);
    }

    public abstract Font getFont(String family, int style) throws FontNotFoundException;
    public abstract Font readFont(File input) throws IOException;
    public abstract Font readFont(Asset input) throws IOException;

    public abstract Cursor getCursor(int type) throws CursorNotFoundException;
    public abstract Cursor createCursor(Bitmap bitmap, int hotSpotX, int hotSpotY);
    public Cursor createCursor(Cursor[] cursors, int cursorsOffset, long[] durations, int durationsOffset, int length) {
        return new AnimatedCursor(cursors, cursorsOffset, durations, durationsOffset, length);
    }
    public Cursor createCursor(Cursor[] cursors, long[] durations) {
        if (cursors.length != durations.length) throw new IndexOutOfBoundsException("Array length mismatch");
        return createCursor(cursors, 0, durations, 0, cursors.length);
    }
    public Cursor createCursor(Bitmap.MultiFrame frames) {
        Cursor[] cursors = new Cursor[frames.size()];
        long[] durations = new long[frames.size()];
        Bitmap background = createBitmap(frames.getCompatibleWidth(), frames.getCompatibleHeight(), frames.getCompatibleType());
        Drawing drawing = Drawing.getInstance();
        try (Graphics graphics = background.getGraphics()) {
            Composite composite = null;
            for (int i = 0; i < frames.size(); i ++) {
                Bitmap.Frame frame = frames.get(i);
                switch (frame.getDisposalMode()) {
                    case Bitmap.DisposalMode.BACKGROUND:
                        graphics.drawColor();
                        break;
                    case Bitmap.DisposalMode.PREVIOUS:
                        if (i - 1 >= 0) graphics.drawBitmapFrame(frames.get(i - 1));
                        break;
                }
                if (frame.getBlendMode() == Bitmap.BlendMode.OVER)
                    graphics.setComposite(composite == null ? (composite = drawing.createComposite(Composite.Mode.DST_OVER)) : composite);
                graphics.drawBitmapFrame(frame);
                cursors[i] = createCursor(background, 0, 0);
                durations[i] = frame.getDuration();
            }
            return createCursor(cursors, durations);
        }
    }
    public abstract int getMaximumCursorColors();

}

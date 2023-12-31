package unrefined.media.graphics;

import unrefined.io.Disposable;
import unrefined.io.asset.Asset;
import unrefined.util.Copyable;
import unrefined.util.Duplicatable;
import unrefined.util.NotInstantiableError;
import unrefined.util.Swappable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public abstract class Bitmap implements Disposable, Copyable, Duplicatable {

    public static Bitmap of(int width, int height, int type) {
        return Drawing.getInstance().createBitmap(width, height, type);
    }

    public static Bitmap read(File input) throws IOException {
        return Drawing.getInstance().readBitmap(input);
    }
    public static Bitmap read(InputStream input) throws IOException {
        return Drawing.getInstance().readBitmap(input);
    }
    public static Bitmap read(Asset input) throws IOException {
        return Drawing.getInstance().readBitmap(input);
    }
    public static Bitmap read(File input, int type) throws IOException {
        return Drawing.getInstance().readBitmap(input, type);
    }
    public static Bitmap read(InputStream input, int type) throws IOException {
        return Drawing.getInstance().readBitmap(input, type);
    }
    public static Bitmap read(Asset input, int type) throws IOException {
        return Drawing.getInstance().readBitmap(input, type);
    }
    public static void writeBitmap(Bitmap bitmap, File output, String format, float quality) throws IOException {
        Drawing.getInstance().writeBitmap(bitmap, output, format, quality);
    }
    public static void writeBitmap(Bitmap bitmap, OutputStream output, String format, float quality) throws IOException {
        Drawing.getInstance().writeBitmap(bitmap, output, format, quality);
    }

    public static Bitmap.MultiFrame readMultiFrame(File input) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input);
    }
    public static Bitmap.MultiFrame readMultiFrame(InputStream input) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input);
    }
    public static Bitmap.MultiFrame readMultiFrame(Asset input) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input);
    }
    public static Bitmap.MultiFrame readMultiFrame(File input, int type) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, type);
    }
    public static Bitmap.MultiFrame readMultiFrame(InputStream input, int type) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, type);
    }
    public static Bitmap.MultiFrame readMultiFrame(Asset input, int type) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, type);
    }
    public static Bitmap.MultiFrame readMultiFrame(File input, int[] types, int typesOffset) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, types, typesOffset);
    }
    public static Bitmap.MultiFrame readMultiFrame(InputStream input, int[] types, int typesOffset) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, types, typesOffset);
    }
    public static Bitmap.MultiFrame readMultiFrame(Asset input, int[] types, int typesOffset) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, types, typesOffset);
    }
    public static Bitmap.MultiFrame readMultiFrame(File input, int[] types) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, types);
    }
    public static Bitmap.MultiFrame readMultiFrame(InputStream input, int[] types) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, types);
    }
    public static Bitmap.MultiFrame readMultiFrame(Asset input, int[] types) throws IOException {
        return Drawing.getInstance().readBitmapMultiFrame(input, types);
    }
    public static void writeMultiFrame(Bitmap.MultiFrame frames, File output, String format, float quality) throws IOException {
        Drawing.getInstance().writeBitmapMultiFrame(frames, output, format, quality);
    }
    public static void writeMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float quality) throws IOException {
        Drawing.getInstance().writeBitmapMultiFrame(frames, output, format, quality);
    }
    public static void writeMultiFrame(Bitmap.MultiFrame frames, File output, String format, float[] quality, int qualityOffset) throws IOException {
        Drawing.getInstance().writeBitmapMultiFrame(frames, output, format, quality, qualityOffset);
    }
    public static void writeMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float[] quality, int qualityOffset) throws IOException {
        Drawing.getInstance().writeBitmapMultiFrame(frames, output, format, quality, qualityOffset);
    }
    public static void writeMultiFrame(Bitmap.MultiFrame frames, File output, String format, float[] quality) throws IOException {
        Drawing.getInstance().writeBitmapMultiFrame(frames, output, format, quality);
    }
    public static void writeMultiFrame(Bitmap.MultiFrame frames, OutputStream output, String format, float[] quality) throws IOException {
        Drawing.getInstance().writeBitmapMultiFrame(frames, output, format, quality);
    }

    public static Set<String> getReaderFormats() {
        return Drawing.getInstance().getBitmapReaderFormats();
    }
    public static Set<String> getWriterFormats() {
        return Drawing.getInstance().getBitmapWriterFormats();
    }

    public static abstract class Handler {
        public abstract Bitmap read(File input, int type) throws IOException;
        public abstract Bitmap read(InputStream input, int type) throws IOException;
        public abstract Bitmap read(Asset input, int type) throws IOException;
        public abstract boolean write(Bitmap bitmap, File output, String format, float quality) throws IOException;
        public abstract boolean write(Bitmap bitmap, OutputStream output, String format, float quality) throws IOException;
        public abstract MultiFrame readMultiFrame(File input, int type) throws IOException;
        public abstract MultiFrame readMultiFrame(InputStream input, int type) throws IOException;
        public abstract MultiFrame readMultiFrame(Asset input, int type) throws IOException;
        public abstract MultiFrame readMultiFrame(File input, int[] types, int typesOffset) throws IOException;
        public abstract MultiFrame readMultiFrame(InputStream input, int[] types, int typesOffset) throws IOException;
        public abstract MultiFrame readMultiFrame(Asset input, int[] types, int typesOffset) throws IOException;
        public abstract boolean writeMultiFrame(MultiFrame frames, File output, String format, float quality) throws IOException;
        public abstract boolean writeMultiFrame(MultiFrame frames, OutputStream output, String format, float quality) throws IOException;
        public abstract boolean writeMultiFrame(MultiFrame frames, File output, String format, float[] quality, int qualityOffset) throws IOException;
        public abstract boolean writeMultiFrame(MultiFrame frames, OutputStream output, String format, float[] quality, int qualityOffset) throws IOException;
        public abstract Set<String> readerFormats();
        public abstract Set<String> writerFormats();
    }

    public static final class DisposalMode {
        private DisposalMode() {
            throw new NotInstantiableError(DisposalMode.class);
        }
        public static final int NONE = 0;
        public static final int BACKGROUND = 1;
        public static final int PREVIOUS = 2;
        public static int checkValid(int mode) {
            if (mode < NONE || mode > PREVIOUS) throw new IllegalArgumentException("Illegal disposal mode: " + mode);
            else return mode;
        }
        public static boolean isValid(int mode) {
            return mode >= NONE && mode <= PREVIOUS;
        }
        public static String toString(int mode) {
            switch (mode) {
                case NONE: return "NONE";
                case BACKGROUND: return "BACKGROUND";
                case PREVIOUS: return "PREVIOUS";
                default: throw new IllegalArgumentException("Illegal disposal mode: " + mode);
            }
        }
    }

    public static final class BlendMode {
        private BlendMode() {
            throw new NotInstantiableError(BlendMode.class);
        }
        public static final int SOURCE = 0;
        public static final int OVER = 1;
        public static int checkValid(int mode) {
            if (mode < SOURCE || mode > OVER) throw new IllegalArgumentException("Illegal blend mode: " + mode);
            else return mode;
        }
        public static boolean isValid(int mode) {
            return mode >= SOURCE && mode <= OVER;
        }
        public static String toString(int mode) {
            switch (mode) {
                case SOURCE: return "SOURCE";
                case OVER: return "OVER";
                default: throw new IllegalArgumentException("Illegal blend mode: " + mode);
            }
        }
    }

    public static class Frame implements Copyable, Swappable {

        private long duration;
        private transient Bitmap bitmap;
        private int hotSpotX, hotSpotY;
        private int disposalMode;
        private int blendMode;

        public Frame(Bitmap bitmap, int hotSpotX, int hotSpotY, long duration, int disposalMode, int blendMode) {
            setBitmap(bitmap);
            this.hotSpotX = hotSpotX;
            this.hotSpotY = hotSpotY;
            this.duration = duration;
            setDisposalMode(disposalMode);
            setBlendMode(blendMode);
        }

        public Frame(Bitmap bitmap) {
            setBitmap(bitmap);
        }

        public Frame(Bitmap bitmap, int hotSpotX, int hotSpotY) {
            this(bitmap);
            setHotSpot(hotSpotX, hotSpotY);
        }

        public Frame(Frame frame) {
            this(frame.getBitmap(), frame.getHotSpotX(), frame.getHotSpotY(), frame.getDuration(), frame.getDisposalMode(), frame.getBlendMode());
        }

        public void setFrame(Frame frame) {
            setFrame(frame.getBitmap(), frame.getHotSpotX(), frame.getHotSpotY(), frame.getDuration(), frame.getDisposalMode(), frame.getBlendMode());
        }

        public void setFrame(Bitmap bitmap, int hotSpotX, int hotSpotY, long duration, int disposalMode, int blendMode) {
            setBitmap(bitmap);
            setHotSpot(hotSpotX, hotSpotY);
            setDuration(duration);
            setDisposalMode(disposalMode);
            setBlendMode(blendMode);
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = Objects.requireNonNull(bitmap);
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setDuration(long duration) {
            this.duration = Math.max(0, duration);
        }

        public long getDuration() {
            return duration;
        }

        public void setHotSpotX(int hotSpotX) {
            this.hotSpotX = hotSpotX;
        }

        public int getHotSpotX() {
            return hotSpotX;
        }

        public void setHotSpotY(int hotSpotY) {
            this.hotSpotY = hotSpotY;
        }

        public int getHotSpotY() {
            return hotSpotY;
        }

        public void setHotSpot(int hotSpotX, int hotSpotY) {
            this.hotSpotX = hotSpotX;
            this.hotSpotY = hotSpotY;
        }

        public void getHotSpot(Point hotSpot) {
            hotSpot.setPoint(hotSpotX, hotSpotY);
        }

        public int getDisposalMode() {
            return disposalMode;
        }

        public void setDisposalMode(int disposalMode) {
            this.disposalMode = DisposalMode.checkValid(disposalMode);
        }

        public int getBlendMode() {
            return blendMode;
        }

        public void setBlendMode(int blendMode) {
            this.blendMode = BlendMode.checkValid(blendMode);
        }

        @Override
        public Frame clone() {
            try {
                return (Frame) super.clone();
            }
            catch (CloneNotSupportedException e) {
                return copy();
            }
        }

        @Override
        public Frame copy() {
            return new Frame(this);
        }

        @Override
        public void to(Object dst) {
            ((Frame) dst).setFrame(this);
        }

        @Override
        public void from(Object src) {
            setFrame((Frame) src);
        }

        @Override
        public void swap(Object o) {
            Frame that = (Frame) o;

            Bitmap bitmap = that.bitmap;
            int hotSpotX = that.hotSpotX;
            int hotSpotY = that.hotSpotY;
            long duration = that.duration;
            int disposalMode = that.disposalMode;
            int blendMode = that.blendMode;

            that.setFrame(this);
            setFrame(bitmap, hotSpotX, hotSpotY, duration, disposalMode, blendMode);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Frame frame = (Frame) o;

            if (duration != frame.duration) return false;
            if (hotSpotX != frame.hotSpotX) return false;
            if (hotSpotY != frame.hotSpotY) return false;
            if (disposalMode != frame.disposalMode) return false;
            if (blendMode != frame.blendMode) return false;
            return bitmap.equals(frame.bitmap);
        }

        @Override
        public int hashCode() {
            int result = (int) (duration ^ (duration >>> 32));
            result = 31 * result + bitmap.hashCode();
            result = 31 * result + hotSpotX;
            result = 31 * result + hotSpotY;
            result = 31 * result + disposalMode;
            result = 31 * result + blendMode;
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "bitmap=" + bitmap +
                    ", hotSpotX=" + hotSpotX +
                    ", hotSpotY=" + hotSpotY +
                    ", duration=" + duration +
                    ", disposalMode=" + DisposalMode.toString(disposalMode) +
                    ", blendMode=" + BlendMode.toString(blendMode) +
                    '}';
        }

    }

    public static class MultiFrame extends ArrayList<Frame> implements Copyable, Swappable {

        private static final long serialVersionUID = 3834067739734342833L;

        private int loops = 0;

        public MultiFrame(Collection<Frame> frames) {
            for (Frame frame : frames) {
                add(frame.clone());
            }
        }

        public MultiFrame(Frame... frames) {
            for (Frame frame : frames) {
                add(frame.clone());
            }
        }

        public void setFrames(Collection<Frame> frames) {
            clear();
            for (Frame frame : frames) {
                add(frame.clone());
            }
        }

        public void setFrames(Frame[] frames, int framesOffset, int length) {
            clear();
            for (int i = 0; i < length; i ++) {
                add(frames[framesOffset + i].clone());
            }
        }

        public void setFrames(Frame... frames) {
            clear();
            for (Frame frame : frames) {
                add(frame.clone());
            }
        }

        public int getLooping() {
            return loops;
        }

        public void setLooping(int loops) {
            this.loops = loops;
        }

        public int getCompatibleWidth() {
            int result = -1;
            for (Frame frame : this) {
                result = Math.max(result, frame.getBitmap().getWidth() + frame.getHotSpotX());
            }
            return result;
        }

        public int getCompatibleHeight() {
            int result = -1;
            for (Frame frame : this) {
                result = Math.max(result, frame.getBitmap().getHeight() + frame.getHotSpotY());
            }
            return result;
        }

        public int getCompatibleType() {
            boolean alpha8 = false;
            boolean alpha4 = false;
            boolean rgb888 = false;
            boolean rgb565 = false;
            boolean rgb444 = false;
            for (Frame frame : this) {
                switch (frame.getBitmap().getType()) {
                    case Type.RGBA_8888:
                        rgb888 = true;
                    case Type.ALPHA_8:
                        alpha8 = true;
                        break;
                    case Type.RGBA_4444:
                        rgb444 = true;
                        alpha4 = true;
                        break;
                    case Type.RGB_565:
                        rgb565 = true;
                        break;
                }
            }
            if (alpha8) return (rgb444 || rgb565 || rgb888) ? Type.RGBA_8888 : Type.ALPHA_8;
            else if (alpha4) return rgb565 ? Type.RGBA_8888 : Type.RGBA_4444;
            else return rgb565 ? Type.RGB_565 : -1;
        }

        @Override
        public MultiFrame clone() {
            MultiFrame clone = (MultiFrame) super.clone();
            clone.clear();
            for (Frame frame : this) {
                clone.add(frame.clone());
            }
            return clone;
        }

        @Override
        public MultiFrame copy() {
            return new MultiFrame(this);
        }

        @Override
        public void to(Object dst) {
            ((MultiFrame) dst).setFrames(this);
        }

        @Override
        public void from(Object src) {
            setFrames((MultiFrame) src);
        }

        @Override
        public void swap(Object o) {
            MultiFrame that = (MultiFrame) o;
            MultiFrame tmp = (MultiFrame) super.clone();
            clear();
            addAll(that);
            that.clear();
            that.addAll(tmp);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MultiFrame frames = (MultiFrame) o;

            return loops == frames.loops;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + loops;
            return result;
        }

        @Override
        public String toString() {
            return getClass().getName()
                    + '{' +
                    "looping=" + loops +
                    ", frames=" + super.toString() +
                    '}';
        }

    }

    public static final class Type {
        private Type() {
            throw new NotInstantiableError(Type.class);
        }
        public static final int RGBA_8888 = 0;
        public static final int RGBA_4444 = 1;
        public static final int RGB_565   = 2;
        public static final int ALPHA_8   = 3;
        public static int checkValid(int type) {
            if (type < RGBA_8888 || type > ALPHA_8) throw new IllegalArgumentException("Illegal bitmap type: " + type);
            else return type;
        }
        public static boolean isValid(int type) {
            return type >= RGBA_8888 && type <= ALPHA_8;
        }
        public static String toString(int type) {
            switch (type) {
                case RGBA_8888: return "RGBA_8888";
                case RGBA_4444: return "RGBA_4444";
                case RGB_565: return "RGB_565";
                case ALPHA_8: return "ALPHA_8";
                default: throw new IllegalArgumentException("Illegal bitmap type: " + type);
            }
        }
    }

    public boolean hasAlpha() {
        return getType() != Type.RGB_565;
    }

    public abstract Bitmap slice(int x, int y, int width, int height);
    public abstract Bitmap duplicate();
    public abstract Bitmap attachment();

    public boolean hasAttachment() {
        return attachment() != null;
    }

    public abstract Graphics getGraphics();

    public abstract int getWidth();
    public abstract int getHeight();

    public abstract int getPixel(int x, int y);
    public abstract void setPixel(int x, int y, int color);

    public abstract void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height);
    public abstract void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height);

    public abstract int getType();

    @Override
    public Bitmap clone() {
        try {
            return (Bitmap) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
    @Override
    public abstract Bitmap copy();

    @Override
    public String toString() {
        if (isDisposed()) return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=true" +
                '}';
        else return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + '{' +
                "disposed=false" +
                ", type=" + Type.toString(getType()) +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
    }

}

package unrefined.runtime;

import unrefined.desktop.BitmapSupport;
import unrefined.desktop.CleanerSupport;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Graphics;
import unrefined.util.AlreadyDisposedException;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DesktopBitmap extends Bitmap {

    private volatile BufferedImage bufferedImage;
    private final int bitmapType;

    public DesktopBitmap(BufferedImage bufferedImage) {
        this.bufferedImage = Objects.requireNonNull(bufferedImage);
        bitmapType = BitmapSupport.getBitmapType(bufferedImage);
        CleanerSupport.register(this, this::dispose);
    }

    public DesktopBitmap(int width, int height, int type) {
        this.bufferedImage = BitmapSupport.createImage(width, height, type);
        attachment = null;
        bitmapType = BitmapSupport.getBitmapType(bufferedImage);
        CleanerSupport.register(this, this::dispose);
    }

    private volatile DesktopBitmap attachment;

    protected DesktopBitmap(DesktopBitmap attachment, int x, int y, int width, int height) {
        this.attachment = Objects.requireNonNull(attachment);
        this.bufferedImage = BitmapSupport.deriveImage(attachment.getBufferedImage(), x, y, width, height);
        bitmapType = BitmapSupport.getBitmapType(bufferedImage);
    }

    protected DesktopBitmap(DesktopBitmap attachment) {
        this.attachment = Objects.requireNonNull(attachment);
        this.bufferedImage = BitmapSupport.deriveImage(attachment.getBufferedImage());
        bitmapType = BitmapSupport.getBitmapType(bufferedImage);
    }

    public BufferedImage getBufferedImage() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return bufferedImage;
    }

    @Override
    public Bitmap slice(int x, int y, int width, int height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        return new DesktopBitmap(this, x, y, width, height);
    }

    @Override
    public Bitmap duplicate() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return new DesktopBitmap(this);
    }

    @Override
    public Bitmap attachment() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return attachment;
    }

    @Override
    public boolean hasMemory() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return bufferedImage.getRaster().getParent() == null;
    }

    private volatile Graphics graphics;
    private final Object graphicsLock = new Object();
    @Override
    public Graphics getGraphics() {
        if (isDisposed()) throw new AlreadyDisposedException();
        if (graphics == null || graphics.isDisposed()) synchronized (graphicsLock) {
            if (graphics == null || graphics.isDisposed()) graphics = new DesktopGraphics(bufferedImage);
        }
        return graphics;
    }

    @Override
    public int getWidth() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return bufferedImage.getWidth();
    }

    @Override
    public int getHeight() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return bufferedImage.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        if (isDisposed()) throw new AlreadyDisposedException();
        return bufferedImage.getRGB(x, y);
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (isDisposed()) throw new AlreadyDisposedException();
        bufferedImage.setRGB(x, y, color);
    }

    @Override
    public void getPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        bufferedImage.getRGB(x, y, width, height, pixels, offset, stride);
    }

    @Override
    public void setPixels(int[] pixels, int offset, int stride, int x, int y, int width, int height) {
        if (isDisposed()) throw new AlreadyDisposedException();
        bufferedImage.setRGB(x, y, width, height, pixels, offset, stride);
    }

    @Override
    public int getType() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return bitmapType;
    }

    @Override
    public Bitmap clone() {
        if (isDisposed()) throw new AlreadyDisposedException();
        DesktopBitmap clone = (DesktopBitmap) super.clone();
        if (clone == null) return copy();
        else {
            clone.bufferedImage = bufferedImage == null ? null : BitmapSupport.copyImage(bufferedImage);
            return clone;
        }
    }

    @Override
    public Bitmap copy() {
        if (isDisposed()) throw new AlreadyDisposedException();
        return new DesktopBitmap(BitmapSupport.copyImage(bufferedImage));
    }

    private final AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) {
            if (graphics != null) {
                graphics.dispose();
                graphics = null;
            }
            if (bufferedImage.getRaster().getParent() == null) {
                bufferedImage.flush();
                bufferedImage = null;
                System.gc();
            }
            else {
                attachment = null;
                bufferedImage = null;
            }
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        DesktopBitmap that = (DesktopBitmap) object;

        if (!Objects.equals(bufferedImage, that.bufferedImage)) return false;
        return Objects.equals(attachment, that.attachment);
    }

    @Override
    public int hashCode() {
        int result = bufferedImage != null ? bufferedImage.hashCode() : 0;
        result = 31 * result + (attachment != null ? attachment.hashCode() : 0);
        return result;
    }

}

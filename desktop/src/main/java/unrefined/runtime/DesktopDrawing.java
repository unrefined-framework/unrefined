package unrefined.runtime;

import unrefined.desktop.AWTSupport;
import unrefined.desktop.BiRadialGradientPaint;
import unrefined.desktop.CursorSupport;
import unrefined.desktop.ExtendedComposite;
import unrefined.desktop.ExtendedPaint;
import unrefined.desktop.FontSupport;
import unrefined.desktop.TransformedTexturePaint;
import unrefined.desktop.bitmap.BMPBitmapHandler;
import unrefined.desktop.bitmap.GIFBitmapHandler;
import unrefined.desktop.bitmap.JPEGBitmapHandler;
import unrefined.desktop.bitmap.PNGBitmapHandler;
import unrefined.desktop.bitmap.TIFFBitmapHandler;
import unrefined.desktop.bitmap.WBMPBitmapHandler;
import unrefined.io.asset.Asset;
import unrefined.media.graphics.Bitmap;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Composite;
import unrefined.media.graphics.Cursor;
import unrefined.media.graphics.CursorNotFoundException;
import unrefined.media.graphics.Drawing;
import unrefined.media.graphics.Font;
import unrefined.media.graphics.FontNotFoundException;
import unrefined.media.graphics.Path;
import unrefined.media.graphics.Transform;
import unrefined.util.function.IntBiOperator;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DesktopDrawing extends Drawing {

    {
        bitmapHandlers().add(BMPBitmapHandler.INSTANCE);
        bitmapHandlers().add(PNGBitmapHandler.INSTANCE);
        bitmapHandlers().add(JPEGBitmapHandler.INSTANCE);
        bitmapHandlers().add(WBMPBitmapHandler.INSTANCE);
        bitmapHandlers().add(GIFBitmapHandler.INSTANCE);
        bitmapHandlers().add(TIFFBitmapHandler.INSTANCE);
    }

    @Override
    public Composite createComposite(int mode, float alpha) {
        return new DesktopComposite(ExtendedComposite.getInstance(mode, alpha), alpha);
    }

    @Override
    public Composite createComposite(IntBiOperator composeProc, float alpha) {
        return new DesktopComposite(ExtendedComposite.getInstance(composeProc, alpha));
    }

    @Override
    public Brush createBrush(int color) {
        return new DesktopBrush(new Color(color, true));
    }

    @Override
    public Brush createBrush(float x1, float y1, float x2, float y2,
                             float[] stops, int stopsOffset, int[] colors, int colorsOffset, int length,
                             int tileMode) {
        return new DesktopBrush(new LinearGradientPaint(x1, y1, x2, y2,
                Arrays.copyOfRange(stops, stopsOffset, stopsOffset + length),
                AWTSupport.toColors(colors, colorsOffset, length),
                AWTSupport.toCycleMethod(tileMode)));
    }

    @Override
    public Brush createBrush(float x1, float y1, float x2, float y2, float[] stops, int[] colors, int tileMode) {
        return new DesktopBrush(new LinearGradientPaint(x1, y1, x2, y2, stops, AWTSupport.toColors(colors), AWTSupport.toCycleMethod(tileMode)));
    }

    @Override
    public Brush createBrush(float x1, float y1, float r1, float x2, float y2, float r2,
                             float[] stops, int stopsOffset, int[] colors, int colorsOffset, int length,
                             int tileMode) {
        return new DesktopBrush(new BiRadialGradientPaint(x2, y2, r2, x1, y1, r1,
                Arrays.copyOfRange(stops, stopsOffset, stopsOffset + length),
                AWTSupport.toColors(colors, colorsOffset, length),
                AWTSupport.toCycleMethod(tileMode)));
    }

    @Override
    public Brush createBrush(float x1, float y1, float r1, float x2, float y2, float r2, float[] stops, int[] colors, int tileMode) {
        return new DesktopBrush(new BiRadialGradientPaint(x2, y2, r2, x1, y1, r1, stops, AWTSupport.toColors(colors), AWTSupport.toCycleMethod(tileMode)));
    }

    @Override
    public Brush createBrush(Bitmap bitmap, Transform transform) {
        return new DesktopBrush(new TransformedTexturePaint(((DesktopBitmap) bitmap).getBufferedImage(), ((DesktopTransform) transform).getAffineTransform()));
    }

    @Override
    public Brush createBrush(IntBiOperator paintProc) {
        return new DesktopBrush(new ExtendedPaint(paintProc));
    }

    @Override
    public Path createPath(int fillRule) {
        return new DesktopPath(fillRule);
    }

    @Override
    public Transform createTransform() {
        return new DesktopTransform();
    }

    @Override
    public Bitmap createBitmap(int width, int height, int type) {
        return new DesktopBitmap(width, height, type);
    }

    @Override
    public Font getFont(String family, int style) throws FontNotFoundException {
        return new DesktopFont(FontSupport.getFont(family, style));
    }

    @Override
    public Font readFont(File input) throws IOException {
        return new DesktopFont(FontSupport.readFont(input));
    }

    @Override
    public Font readFont(Asset input) throws IOException {
        return new DesktopFont(FontSupport.readFont(input.openStream()));
    }

    @Override
    public Cursor getCursor(int type) throws CursorNotFoundException {
        if (GraphicsEnvironment.isHeadless()) return null;
        else return DesktopCursor.getSystemCursor(type);
    }

    @Override
    public Cursor createCursor(Bitmap bitmap, int hotSpotX, int hotSpotY) {
        if (GraphicsEnvironment.isHeadless()) return null;
        else return new DesktopCursor(bitmap, new Point(hotSpotX, hotSpotY));
    }

    @Override
    public int getMaximumCursorColors() {
        if (GraphicsEnvironment.isHeadless()) return 0;
        else return CursorSupport.getMaximumCursorColors();
    }

}

package unrefined.desktop;

import unrefined.util.function.IntBiOperator;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class ExtendedPaint implements Paint {

    private static class Context implements PaintContext {

        private final IntBiOperator paintProc;

        public Context(IntBiOperator paintProc) {
            this.paintProc = paintProc;
        }

        @Override
        public void dispose() {
            if (saved != null) {
                putCachedRaster(saved);
                saved = null;
            }
        }

        @Override
        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        private static WeakReference<Raster> cached;
        private Raster saved;

        private static synchronized Raster getCachedRaster(int w, int h) {
            if (cached != null) {
                Raster ras = cached.get();
                if (ras != null && ras.getWidth() >= w && ras.getHeight() >= h) {
                    cached = null;
                    return ras;
                }
            }
            return ColorModel.getRGBdefault().createCompatibleWritableRaster(w, h);
        }

        private static synchronized void putCachedRaster(Raster ras) {
            if (cached != null) {
                Raster cras = cached.get();
                if (cras != null) {
                    int cw = cras.getWidth();
                    int ch = cras.getHeight();
                    int iw = ras.getWidth();
                    int ih = ras.getHeight();
                    if (cw >= iw && ch >= ih) return;
                    if (cw * ch >= iw * ih) return;
                }
            }
            cached = new WeakReference<>(ras);
        }

        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            // If working raster is big enough, reuse it. Otherwise,
            // build a large enough new one.
            Raster raster = saved;
            if (raster == null || raster.getWidth() < w || raster.getHeight() < h) {
                raster = getCachedRaster(w, h);
                saved = raster;
            }

            // Access raster internal int array. Because we use a DirectColorModel,
            // we know the DataBuffer is of type DataBufferInt and the SampleModel
            // is SinglePixelPackedSampleModel.
            // Adjust for initial offset in DataBuffer and also for the scanline
            // stride.
            // These calls make the DataBuffer non-acceleratable, but the
            // Raster is never Stable long enough to accelerate anyway...
            DataBufferInt rasterDB = (DataBufferInt) raster.getDataBuffer();
            int[] pixels = rasterDB.getData(0);
            int off = rasterDB.getOffset();

            for (int dy = 0; dy < h; dy ++) {
                for (int dx = 0; dx < w; dx ++) {
                    pixels[off + dy * raster.getWidth() + dx] = paintProc.apply(x + dx, y + dy);
                }
            }

            return raster;
        }
    }

    private final IntBiOperator paintProc;

    public ExtendedPaint(IntBiOperator paintProc) {
        this.paintProc = Objects.requireNonNull(paintProc);
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        return new Context(paintProc);
    }

    @Override
    public int getTransparency() {
        return TRANSLUCENT;
    }

}

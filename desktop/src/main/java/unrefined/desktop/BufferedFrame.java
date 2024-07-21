package unrefined.desktop;

import java.awt.AlphaComposite;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;

public class BufferedFrame extends Frame {

    public BufferedFrame() throws HeadlessException {
    }

    public BufferedFrame(GraphicsConfiguration gc) {
        super(gc);
    }

    public BufferedFrame(String title) throws HeadlessException {
        super(title);
    }

    public BufferedFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
    }

    private volatile Image buffer;

    @Override
    public void update(Graphics g) {
        if (isShowing()) {
            ((Graphics2D) g).setComposite(AlphaComposite.Src);
            int width = getWidth();
            int height = getHeight();
            if (buffer == null) buffer = createImage(width, height);
            else if (buffer.getWidth(null) < width || buffer.getHeight(null) < height) {
                buffer.flush();
                buffer = createImage(width, height);
            }
            Graphics gg = buffer.getGraphics();
            if (isOpaque()) {
                gg.setColor(getBackground());
                gg.fillRect(0, 0, width, height);
            }
            try {
                g.clipRect(0, 0, width, height);
                paint(gg);
                g.drawImage(buffer, 0, 0, null);
            }
            finally {
                gg.dispose();
            }
        }
    }

    @Override
    public void removeNotify() {
        if (buffer != null) {
            buffer.flush();
            buffer = null;
        }
        super.removeNotify();
    }

}

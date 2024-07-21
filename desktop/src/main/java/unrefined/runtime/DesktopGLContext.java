package unrefined.runtime;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import unrefined.context.Container;
import unrefined.context.ContextListener;
import unrefined.desktop.AWTSupport;
import unrefined.media.opengl.GL;
import unrefined.util.UnexpectedError;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DesktopGLContext extends DesktopContext {

    public DesktopGLContext(Container container, GLCapabilities capabilities, ContextListener contextListener) {
        super(container, contextListener);
        JPanel panel = getJPanel();
        GLJPanel content = new GLJPanel(capabilities) {
            @Override
            public void print(Graphics graphics) {
                setupPrint(1, 1, 0, -1, -1);
                try {
                    super.print(graphics);
                }
                finally {
                    releasePrint();
                }
            }
        };
        content.setFocusable(false);
        content.setOpaque(false);
        //content.setIgnoreRepaint(true);
        content.setBackground(AWTSupport.TRANSPARENT);
        content.enableInputMethods(false);
        content.setLocation(0, 0);
        content.addGLEventListener(new GLEventListener() {
            private volatile GL gl;
            @Override
            public void init(GLAutoDrawable drawable) {
                if (capabilities.getGLProfile().isGL2()) gl = new DesktopGL20(content);
                else if (capabilities.getGLProfile().isGL3()) gl = new DesktopGL30(content);
                else throw new UnexpectedError();
                drawable.getGL().setSwapInterval(0);
            }
            @Override
            public void dispose(GLAutoDrawable drawable) {
                gl = null;
            }
            @Override
            public void display(GLAutoDrawable drawable) {
                listener().onPaint(DesktopGLContext.this, gl, content.isPaintingForPrint());
            }
            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
        });
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                content.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
            }
        });
        panel.add(content);
    }

}

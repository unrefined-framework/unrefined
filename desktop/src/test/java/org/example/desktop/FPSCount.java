package org.example.desktop;

import unrefined.Lifecycle;
import unrefined.app.Runtime;
import unrefined.context.Container;
import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.context.SingleContextAdapter;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Text;
import unrefined.media.opengl.GL;
import unrefined.util.FPSCounter;
import unrefined.util.SharedTimer;

import java.util.concurrent.TimeUnit;

public class FPSCount {

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Container container = Runtime.getInstance().createContainer(new SingleContextAdapter(new ContextAdapter() {
            private Brush brush;
            private FPSCounter counter;
            @Override
            public void onCreate(Context context) {
                brush = Brush.ofColor(Color.WHITE);
                counter = new FPSCounter(60);
            }
            @Override
            public void onDispose(Context context) {
                brush = null;
            }
            @Override
            public void onPaint(Context context, Graphics graphics, boolean snapshot) {
                counter.update();
                graphics.save();
                try {
                    graphics.setBrush(brush);
                    graphics.setTextSize(32);
                    Text.Metrics metrics = new Text.Metrics();
                    String averageFPS = "FPS: " + counter.getAverage();
                    graphics.measureText(averageFPS, metrics);
                    graphics.drawText(averageFPS, 5, metrics.getAscent());
                }
                finally {
                    graphics.restore();
                }
            }
            @Override
            public void onPaint(Context context, GL gl, boolean snapshot) {}
        }) {
            private SharedTimer timer;
            @Override
            public void onCreate(Container container) {
                super.onCreate(container);

                getContext().setSize(640, 480);

                timer = SharedTimer.newBuilder()
                        .repeat(true)
                        .period(1_000_000_000 / 60)
                        .timeUnit(TimeUnit.NANOSECONDS)
                        .onPerform(signal -> signal.connect(event -> container.requestPaint()))
                        .start();
            }
            @Override
            public void onDispose(Container container) {
                super.onDispose(container);

                timer.stop();
                timer = null;
            }
        });

        container.requestLaunch();
    }

}

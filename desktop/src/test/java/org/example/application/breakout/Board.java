package org.example.application.breakout;

import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.io.asset.Asset;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;
import unrefined.media.graphics.Point;
import unrefined.media.sound.Sound;
import unrefined.util.SharedTimer;
import unrefined.util.UnexpectedError;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Board extends ContextAdapter {

    private SharedTimer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick[] bricks;
    private boolean inGame = true;
    private Sound gameOver;
    private Sound collision;
    private Sound victory;

    @Override
    public void onCreate(Context context) {
        context.setBackgroundColor(Color.WHITE);

        try {
            collision = Sound.read(new Asset("brick.ogg"));
            gameOver = Sound.read(new Asset("game_over.ogg"));
            victory = Sound.read(new Asset("victory.ogg"));
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }

        gameInit(context);
    }

    @Override
    public void onDispose(Context context) {

        collision.dispose();
        gameOver.dispose();
        victory.dispose();
    }

    void playCollisionSound() {

        Random random = ThreadLocalRandom.current();
        float volume = random.nextFloat(0.9f, 1.1f);
        float speed = random.nextFloat(0.9f, 1.1f);
        collision.start(volume, volume, speed);
    }

    private void gameInit(Context context) {

        bricks = new Brick[Constant.N_OF_BRICKS];

        ball = new Ball();
        paddle = new Paddle();

        int k = 0;

        for (int i = 0; i < 5; i++) {

            for (int j = 0; j < 6; j++) {

                bricks[k] = new Brick(j * 40 + 30, i * 10 + 50);
                k++;
            }
        }

        timer = SharedTimer.newBuilder()
                .period(Constant.PERIOD)
                .repeat(true)
                .onPerform(signal -> signal.connect(performEvent -> doGameCycle(context)))
                .start();
    }

    @Override
    public void onFocusGain(Context context) {
        if (!inGame) context.requestPaint();
    }

    @Override
    public void onPaint(Context context, Graphics graphics, boolean snapshot) {

        if (inGame) drawObjects(graphics);
        else gameOver(graphics);

        graphics.setStyle(Graphics.Style.FILL);
        graphics.setBrush(TEXT_COLOR);
        graphics.drawRectangle(0, Constant.BOTTOM_EDGE, graphics.getWidth(), graphics.getHeight() - Constant.BOTTOM_EDGE);

    }

    private void drawObjects(Graphics graphics) {

        if (ball == null || paddle == null) return;

        graphics.clearRectangle(0, 0, 200, 200);

        ball.onPaint(graphics);
        paddle.onPaint(graphics);

        for (int i = 0; i < Constant.N_OF_BRICKS; i++) {

            if (!bricks[i].isDestroyed()) {
                bricks[i].onPaint(graphics);
            }
        }

    }

    private static final Brush TEXT_COLOR = Brush.ofColor(Color.BLACK);

    private void gameOver(Graphics graphics) {

        graphics.setBrush(TEXT_COLOR);
        graphics.setStyle(Graphics.Style.FILL);
        graphics.setTextSize(18);
        float textWidth = graphics.measureText(message);
        graphics.drawText(message,
                (Constant.WIDTH - textWidth) / 2.f,
                Constant.WIDTH / 2.f);
    }

    @Override
    public boolean onKeyDown(Context context, String key, String code, int location, int modifiers) {
        return paddle.onKeyDown(code);
    }

    @Override
    public boolean onKeyUp(Context context, String key, String code, int location, int modifiers) {
        return paddle.onKeyUp(code);
    }

    private void doGameCycle(Context context) {

        ball.move(this);
        paddle.move();
        checkCollision();

        context.requestPaint();

    }

    private void stopGame() {
        inGame = false;
        timer.stop();
    }

    private void checkCollision() {

        if (ball.getRect().getBottom() > Constant.BOTTOM_EDGE) {
            gameOver.start();
            stopGame();
        }

        for (int i = 0, j = 0; i < Constant.N_OF_BRICKS; i++) {

            if (bricks[i].isDestroyed()) {
                j++;
            }

            if (j == Constant.N_OF_BRICKS) {
                message = "Victory";
                victory.start();
                stopGame();
            }
        }

        if ((ball.getRect()).intersects(paddle.x, paddle.y, paddle.width, paddle.height)) {

            int paddleLPos = paddle.getRect().getLeft();
            int ballLPos = ball.getRect().getLeft();

            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;

            if (ballLPos < first) {

                playCollisionSound();

                ball.setXDir(-1);
                ball.setYDir(-1);
            }

            if (ballLPos >= first && ballLPos < second) {

                playCollisionSound();

                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos >= second && ballLPos < third) {

                playCollisionSound();

                ball.setXDir(0);
                ball.setYDir(-1);
            }

            if (ballLPos >= third && ballLPos < fourth) {

                playCollisionSound();

                ball.setXDir(1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos > fourth) {

                playCollisionSound();

                ball.setXDir(1);
                ball.setYDir(-1);
            }
        }

        for (int i = 0; i < Constant.N_OF_BRICKS; i++) {

            if ((ball.getRect()).intersects(bricks[i].x, bricks[i].y, bricks[i].width, bricks[i].height)) {

                int ballLeft = ball.getRect().getLeft();
                int ballHeight = ball.getRect().getHeight();
                int ballWidth = ball.getRect().getWidth();
                int ballTop = ball.getRect().getTop();

                Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                Point pointLeft = new Point(ballLeft - 1, ballTop);
                Point pointTop = new Point(ballLeft, ballTop - 1);
                Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                if (!bricks[i].isDestroyed()) {

                    if (bricks[i].getRect().contains(pointRight.getX(), pointRight.getY())) {

                        playCollisionSound();

                        ball.setXDir(-1);
                    } else if (bricks[i].getRect().contains(pointLeft.getX(), pointLeft.getY())) {

                        playCollisionSound();

                        ball.setXDir(1);
                    }

                    if (bricks[i].getRect().contains(pointTop.getX(), pointTop.getY())) {

                        playCollisionSound();

                        ball.setYDir(1);
                    } else if (bricks[i].getRect().contains(pointBottom.getX(), pointBottom.getY())) {

                        playCollisionSound();

                        ball.setYDir(-1);
                    }

                    bricks[i].setDestroyed(true);
                }
            }
        }
    }

}

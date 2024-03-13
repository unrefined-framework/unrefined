package org.example.application.snake;

import unrefined.context.Context;
import unrefined.context.ContextAdapter;
import unrefined.io.asset.Asset;
import unrefined.media.graphics.Brush;
import unrefined.media.graphics.Color;
import unrefined.media.graphics.Graphics;
import unrefined.media.sound.Sound;
import unrefined.util.SharedTimer;
import unrefined.util.UnexpectedError;

import java.io.IOException;

public class Board extends ContextAdapter {

    private final int[] x = new int[Constant.ALL_DOTS];
    private final int[] y = new int[Constant.ALL_DOTS];

    private int dots;
    private int foodX;
    private int foodY;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    private SharedTimer timer;

    private Sound gameOver;

    @Override
    public void onCreate(Context context) {
        context.setBackgroundColor(Color.WHITE);

        try {
            gameOver = Sound.read(new Asset("game_over.ogg"));
        } catch (IOException e) {
            throw new UnexpectedError(e);
        }

        gameInit(context);
    }

    private void gameInit(Context context) {

        dots = 3;

        for (int z = 0; z < dots; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        placeFood();

        timer = SharedTimer.build()
                .period(Constant.DELAY)
                .repeat(true)
                .onPerform(signal -> signal.connect(event -> doGameCycle(context)))
                .start();
    }

    @Override
    public void onDispose(Context context) {

        gameOver.dispose();
    }

    @Override
    public void onPaint(Context context, Graphics graphics, boolean snapshot) {
        doDrawing(graphics);
    }

    private static final Brush FOOD_COLOR = Brush.ofColor(Color.RED);
    private static final Brush SNAKE_COLOR = Brush.ofColor(Color.BLACK);

    private void doDrawing(Graphics graphics) {
        
        if (inGame) {

            graphics.setStyle(Graphics.Style.FILL);
            graphics.setBrush(FOOD_COLOR);

            graphics.drawRectangle(foodX, foodY, Constant.DOT_SIZE, Constant.DOT_SIZE);

            for (int z = 0; z < dots; z++) {
                graphics.setBrush(SNAKE_COLOR);
                graphics.drawRectangle(x[z], y[z],
                        Constant.DOT_SIZE, Constant.DOT_SIZE);
            }

        } else {
            gameOver(graphics);
        }

    }

    @Override
    public void onFocusGain(Context context) {
        if (!inGame) context.requestPaint();
    }

    private static final Brush TEXT_COLOR = Brush.ofColor(Color.BLACK);

    private void gameOver(Graphics graphics) {
        
        String msg = "Game Over";

        graphics.setBrush(TEXT_COLOR);
        graphics.setStyle(Graphics.Style.FILL);
        graphics.setTextSize(18);
        float textWidth = graphics.measureText(msg);

        graphics.drawText(msg, (Constant.WIDTH - textWidth) / 2, Constant.HEIGHT / 2.f);

    }

    private void checkFood() {

        if ((x[0] == foodX) && (y[0] == foodY)) {

            dots++;
            placeFood();
        }

    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= Constant.DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += Constant.DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= Constant.DOT_SIZE;
        }

        if (downDirection) {
            y[0] += Constant.DOT_SIZE;
        }
    }

    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= Constant.HEIGHT) {
            inGame = false;
        }

        if (y[0] < 0) {
            inGame = false;
        }

        if (x[0] >= Constant.WIDTH) {
            inGame = false;
        }

        if (x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            gameOver.start();
            timer.stop();
        }

    }

    private void placeFood() {

        int r = (int) (Math.random() * Constant.RAND_POS);
        foodX = ((r * Constant.DOT_SIZE));

        r = (int) (Math.random() * Constant.RAND_POS);
        foodY = ((r * Constant.DOT_SIZE));

    }

    public void doGameCycle(Context context) {
        if (inGame) {

            checkFood();
            checkCollision();
            move();
        }

        context.requestPaint();
    }

    @Override
    public boolean onKeyDown(Context context, String key, String code, int location, int modifiers) {

        if (("KeyA".equals(code) || "ArrowLeft".equals(code)) && (!rightDirection)) {
            leftDirection = true;
            upDirection = false;
            downDirection = false;
            return true;
        }

        if (("KeyD".equals(code) || "ArrowRight".equals(code)) && (!leftDirection)) {
            rightDirection = true;
            upDirection = false;
            downDirection = false;
            return true;
        }

        if (("KeyW".equals(code) || "ArrowUp".equals(code)) && (!downDirection)) {
            upDirection = true;
            rightDirection = false;
            leftDirection = false;
            return true;
        }

        if (("KeyS".equals(code) || "ArrowDown".equals(code)) && (!upDirection)) {
            downDirection = true;
            rightDirection = false;
            leftDirection = false;
            return true;
        }

        return false;
    }

}

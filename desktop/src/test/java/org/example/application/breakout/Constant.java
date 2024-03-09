package org.example.application.breakout;

import unrefined.util.NotInstantiableError;

public class Constant {

    private Constant() {
        throw new NotInstantiableError(Constant.class);
    }
    
    public static final int WIDTH = 300;
    public static final int HEIGHT = 400;
    public static final int BOTTOM_EDGE = 390;
    public static final int N_OF_BRICKS = 30;
    public static final int INIT_PADDLE_X = 200;
    public static final int INIT_PADDLE_Y = 360;
    public static final int INIT_BALL_X = 230;
    public static final int INIT_BALL_Y = 355;
    public static final int PERIOD = 10;

}

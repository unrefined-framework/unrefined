package org.example.application.snake;

import unrefined.util.NotInstantiableError;

public class Constant {

    private Constant() {
        throw new NotInstantiableError(Constant.class);
    }

    public static final int WIDTH = 300;
    public static final int HEIGHT = 300;
    public static final int DOT_SIZE = 10;
    public static final int ALL_DOTS = 900;
    public static final int RAND_POS = 29;
    public static final int DELAY = 140;

}

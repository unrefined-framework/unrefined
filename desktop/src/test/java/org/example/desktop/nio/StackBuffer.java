package org.example.desktop.nio;

import unrefined.Lifecycle;
import unrefined.app.Log;

import java.io.IOException;

public class StackBuffer {

    private static volatile unrefined.nio.StackBuffer STACK;

    public static void use(unrefined.nio.StackBuffer stack) {
        STACK = stack;
    }

    public static void cleanup() {
        STACK = null;
    }

    public static void add() {
        STACK.pushInt(STACK.popInt() + STACK.popInt());
    }

    public static void main(String[] args) {
        Lifecycle.onMain(args);
        Log log = Log.defaultInstance();

        try (unrefined.nio.StackBuffer stack = unrefined.nio.StackBuffer.allocate(1024)) {
            log.info("Unrefined NIO", "STACK PUSH INT 1");
            stack.pushInt(1);
            log.info("Unrefined NIO", "STACK PUSH INT 2");
            stack.pushInt(2);
            log.info("Unrefined NIO", "STACK PUSH INT (STACK POP INT + STACK POP INT)");
            use(stack);
            add();
            log.info("Unrefined NIO", "STACK POP INT = " + stack.popInt());
        } catch (IOException ignored) {
        } finally {
            cleanup();
        }

    }

}

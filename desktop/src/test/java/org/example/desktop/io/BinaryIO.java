package org.example.desktop.io;

import unrefined.app.Logger;
import unrefined.io.BinaryInput;
import unrefined.io.BinaryInputStream;
import unrefined.io.BinaryOutput;
import unrefined.io.BinaryOutputStream;
import unrefined.io.Portable;
import unrefined.runtime.DesktopRuntime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BinaryIO {

    public static class Fruit implements Portable {

        private String name;

        public Fruit() {
            setName(null);
        }

        public Fruit(String name) {
            setName(name);
        }

        public void setName(String name) {
            this.name = name == null ? "Unknown Fruit" : name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void writePortable(BinaryOutput out) throws IOException {
            out.writeString(name);
        }

        @Override
        public void readPortable(BinaryInput in) throws IOException {
            setName(in.readString());
        }

    }

    public static void main(String[] args) {
        DesktopRuntime.initialize(args);

        Logger logger = Logger.defaultInstance();

        Fruit write = new Fruit("Apple");

        logger.info("Fruit", write.getName());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (BinaryOutputStream stream = new BinaryOutputStream(out)) {
            stream.writePortable(write);
        } catch (IOException ignored) {
        }

        Fruit read = new Fruit();
        logger.info("Fruit", read.getName());

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        try (BinaryInputStream stream = new BinaryInputStream(in)) {
            stream.readPortable(read);
            logger.info("Fruit", read.getName());
        } catch (IOException ignored) {
        }

    }

}

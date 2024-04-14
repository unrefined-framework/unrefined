package org.example.desktop.io;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.io.BundleInput;
import unrefined.io.BundleInputStream;
import unrefined.io.BundleOutput;
import unrefined.io.BundleOutputStream;
import unrefined.io.Bundleable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BundleIO {

    public static class Fruit implements Bundleable {

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
        public void writeToBundle(BundleOutput out) throws IOException {
            out.put("name", name);
        }

        @Override
        public void readFromBundle(BundleInput in) throws IOException {
            setName(in.get("name", null));
        }

    }

    public static void main(String[] args) {
        Lifecycle.onMain(args);

        Log log = Log.defaultInstance();

        Fruit write = new Fruit("Apple");

        log.info("Fruit", write.getName());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (BundleOutputStream stream = new BundleOutputStream(out)) {
            stream.putBundleable("fruit", write);
        } catch (IOException ignored) {
        }

        Fruit read = new Fruit();
        log.info("Fruit", read.getName());

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        try (BundleInputStream stream = new BundleInputStream(in)) {
            stream.getBundleable("fruit", read);
            log.info("Fruit", read.getName());
        } catch (IOException ignored) {
        }

    }

}

package org.example.desktop.io;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.io.BundleInput;
import unrefined.io.BundleReader;
import unrefined.io.BundleOutput;
import unrefined.io.BundleWriter;
import unrefined.io.Bundleable;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

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

        StringWriter out = new StringWriter();

        try (BundleWriter writer = new BundleWriter(out)) {
            writer.putBundleable("fruit", write);
        } catch (IOException ignored) {
        }

        Fruit read = new Fruit();
        log.info("Fruit", read.getName());

        StringReader in = new StringReader(out.toString());

        try (BundleReader reader = new BundleReader(in)) {
            reader.getBundleable("fruit", read);
            log.info("Fruit", read.getName());
        } catch (IOException ignored) {
        }

    }

}

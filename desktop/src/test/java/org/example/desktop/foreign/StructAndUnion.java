package org.example.desktop.foreign;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.nio.Pointer;
import unrefined.util.foreign.Aggregate;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Symbol;

import java.io.IOException;

public class StructAndUnion {

    private static final class Union64 extends Aggregate {
        public static final Descriptor descriptor = declareUnion(long.class, double.class);
        private Union64(Pointer memory) {
            super(memory);
        }
        public static Union64 newInstance(Pointer memory) {
            return new Union64(memory);
        }
        public static Union64 allocate() throws IOException {
            return new Union64(Pointer.allocate(sizeOfType(Union64.class)));
        }
        public static Union64 allocateDirect() throws IOException {
            return new Union64(Pointer.allocateDirect(sizeOfType(Union64.class)));
        }
        public long longValue() {
            return memory().getLong(getDescriptor().members().get(0).getOffset());
        }
        public double doubleValue() {
            return memory().getDouble(getDescriptor().members().get(1).getOffset());
        }
    }

    private static final class Int128 extends Aggregate {
        public static final Descriptor descriptor = declareStruct(long.class, long.class);
        private Int128(Pointer memory) {
            super(memory);
        }
        public static Int128 newInstance(Pointer memory) {
            return new Int128(memory);
        }
        public static Int128 allocate() throws IOException {
            return new Int128(Pointer.allocate(sizeOfType(Int128.class)));
        }
        public static Int128 allocateDirect() throws IOException {
            return new Int128(Pointer.allocateDirect(sizeOfType(Int128.class)));
        }
        public void low(long value) {
            memory().putLong(getDescriptor().members().get(0).getOffset(), value);
        }
        public long low() {
            return memory().getLong(getDescriptor().members().get(0).getOffset());
        }
        public void high(long value) {
            memory().putLong(getDescriptor().members().get(1).getOffset(), value);
        }
        public long high() {
            return memory().getLong(getDescriptor().members().get(1).getOffset());
        }
        public Union64 lowAsUnion64() {
            return new Union64(memory().slice(getDescriptor().members().get(0).getOffset(), descriptorOf(Union64.class).size()));
        }
        public Union64 highAsUnion64() {
            return new Union64(memory().slice(getDescriptor().members().get(1).getOffset(), descriptorOf(Union64.class).size()));
        }
    }

    public static void passInt128(Int128 int128) {
        Log.defaultInstance().info("Unrefined FFI", "((union64_t) HI (int128)).d = " + int128.highAsUnion64().doubleValue());
        Log.defaultInstance().info("Unrefined FFI", "((union64_t) LO (int128)).d = " + int128.lowAsUnion64().doubleValue());
    }

    public static void main(String[] args) throws NoSuchMethodException, IOException {
        Lifecycle.onMain(args);
        Foreign foreign = Foreign.getInstance();
        Symbol symbol = foreign.upcallStub(StructAndUnion.class.getDeclaredMethod("passInt128", Int128.class), void.class, Int128.class);
        Int128 int128 = Int128.allocate();
        int128.high(Double.doubleToLongBits(54321.12345));
        int128.low(Double.doubleToLongBits(12345.54321));
        symbol.invoke(int128);
    }

}

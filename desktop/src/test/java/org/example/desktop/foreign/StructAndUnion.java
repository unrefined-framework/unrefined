package org.example.desktop.foreign;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.nio.DoubleHandle;
import unrefined.nio.LongHandle;
import unrefined.nio.Pointer;
import unrefined.util.foreign.Aggregate;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Symbol;

import java.io.IOException;

public class StructAndUnion {

    public static class union64_t extends Aggregate {
        public static final Descriptor descriptor = declareUnion(
                long.class, double.class
        );
        public final LongHandle l = declareLong(0);
        public final DoubleHandle d = declareDouble(1);
        protected union64_t(Pointer memory) {
            super(memory);
        }
        public static union64_t wrap(Pointer memory) {
            return new union64_t(memory);
        }
        public static union64_t allocate() throws IOException {
            return new union64_t(Pointer.allocate(sizeOfType(union64_t.class)));
        }
        public static union64_t allocateDirect() throws IOException {
            return new union64_t(Pointer.allocateDirect(sizeOfType(union64_t.class)));
        }
    }

    public static class int128_t extends Aggregate {
        public static final Descriptor descriptor = declareStruct(
                long.class, long.class
        );
        public final LongHandle lo = declareLong(0);
        public final LongHandle hi = declareLong(1);
        public final union64_t loAsU64 = declareAggregate(union64_t.class, 0);
        public final union64_t hiAsU64 = declareAggregate(union64_t.class, 1);
        public int128_t(Pointer memory) {
            super(memory);
        }
        public static int128_t wrap(Pointer memory) {
            return new int128_t(memory);
        }
        public static int128_t allocate() throws IOException {
            return new int128_t(Pointer.allocate(sizeOfType(int128_t.class)));
        }
        public static int128_t allocateDirect() throws IOException {
            return new int128_t(Pointer.allocateDirect(sizeOfType(int128_t.class)));
        }
    }

    public static void passInt128(int128_t int128) {
        Log log = Log.defaultInstance();
        log.info("Unrefined FFI", "((union64_t) HI (int128)).d = " + int128.hiAsU64.d.get());
        log.info("Unrefined FFI", "((union64_t) LO (int128)).d = " + int128.loAsU64.d.get());
    }

    public static void main(String[] args) throws NoSuchMethodException, IOException {
        Lifecycle.onMain(args);
        Foreign foreign = Foreign.getInstance();
        Symbol symbol = foreign.upcallStub(StructAndUnion.class.getDeclaredMethod("passInt128", int128_t.class), void.class, int128_t.class);
        int128_t int128 = int128_t.allocateDirect();
        int128.hi.set(Double.doubleToLongBits(54321.12345));
        int128.lo.set(Double.doubleToLongBits(12345.54321));
        symbol.invoke(int128);
    }

}

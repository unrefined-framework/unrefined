package org.example.desktop.foreign;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.nio.Allocator;
import unrefined.nio.Pointer;
import unrefined.util.UnexpectedError;
import unrefined.util.foreign.Foreign;
import unrefined.util.foreign.Library;
import unrefined.util.foreign.Symbol;
import unrefined.util.function.BiFunctor;

import java.io.IOException;

import static unrefined.util.foreign.Symbol.Option.THROW_ERRNO;

public class QuickSort {

    public static int compareInt(long p1, long p2) {
        int i1 = Allocator.getInstance().getInt(p1);
        int i2 = Allocator.getInstance().getInt(p2);
        return Integer.compare(i1, i2);
    }

    public interface LibC extends Library {
        @Options(THROW_ERRNO)
        void qsort(@Marshal("size_t") long data,
                   @Marshal("size_t") long count,
                   @Marshal("size_t") long width,
                   @Marshal("size_t") long compare);
    }

    public static void main(String[] args) {

        Lifecycle.onMain(args);

        Log log = Log.defaultInstance();

        Foreign foreign = Foreign.getInstance();

        LibC c = foreign.downcallProxy(LibC.class);

        BiFunctor<Long, Long, Integer> compareInt = QuickSort::compareInt;
        Symbol compare = foreign.upcallStub(compareInt, int.class, foreign.addressClass(), foreign.addressClass());

        try (Pointer memory = Pointer.allocateDirect(8)) {
            memory.putInt(0, 10);
            memory.putInt(4, -1); // offset is in bytes
            log.info("Unrefined FFI", "memory[0] = " + memory.getInt(0) + ", memory[1] = " + memory.getInt(4));
            log.info("Unrefined FFI", "qsort(memory, 2, sizeof(int32_t), Integer::compare)");
            c.qsort(memory.address(), 2, 4, compare.address());
            log.info("Unrefined FFI", "memory[0] = " + memory.getInt(0) + ", memory[1] = " + memory.getInt(4));
        }
        catch (IOException e) {
            throw new UnexpectedError(e);
        }

    }

}
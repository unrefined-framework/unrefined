package unrefined.desktop;

import com.kenai.jffi.Type;
import unrefined.math.FastMath;
import unrefined.util.FastArray;
import unrefined.util.NotInstantiableError;
import unrefined.util.foreign.Aggregate;

import java.util.HashMap;
import java.util.Map;

public final class AggregateSupport {

    private AggregateSupport() {
        throw new NotInstantiableError(AggregateSupport.class);
    }

    private static final Map<Class<? extends Aggregate>, Type> TYPE_CACHE = new HashMap<>();

    private static int checkSizeValid(long size) {
        if (size < 0) throw new UnsupportedOperationException("Aggregate size too large: " + FastMath.unsign(size));
        else if (size > FastArray.ARRAY_LENGTH_MAX) throw new UnsupportedOperationException("Aggregate size too large: " + size);
        else return (int) size;
    }

    public static Type typeOf(Class<? extends Aggregate> clazz) {
        register(clazz);
        return TYPE_CACHE.get(clazz);
    }

    private static void register(Class<? extends Aggregate> clazz) {
        if (!TYPE_CACHE.containsKey(clazz)) {
            synchronized (TYPE_CACHE) {
                if (!TYPE_CACHE.containsKey(clazz)) TYPE_CACHE.put(clazz, com.kenai.jffi.Array.newArray(Type.UINT8,
                        checkSizeValid(Aggregate.descriptorOf(clazz).getSize())));
            }
        }
    }

}

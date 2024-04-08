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

    private static final Map<Aggregate.Descriptor, Type> TYPE_CACHE = new HashMap<>();

    private static int checkSizeValid(long size) {
        if (size < 0) throw new UnsupportedOperationException("Aggregate size too large: " + FastMath.unsign(size));
        else if (size > FastArray.ARRAY_LENGTH_MAX) throw new UnsupportedOperationException("Aggregate size too large: " + size);
        else return (int) size;
    }

    public static Type typeOf(Aggregate.Descriptor descriptor) {
        register(descriptor);
        return TYPE_CACHE.get(descriptor);
    }

    public static Type typeOf(Class<? extends Aggregate> clazz) {
        return typeOf(Aggregate.descriptorOf(clazz));
    }

    private static void register(Aggregate.Descriptor descriptor) {
        if (!TYPE_CACHE.containsKey(descriptor)) {
            synchronized (TYPE_CACHE) {
                if (!TYPE_CACHE.containsKey(descriptor)) TYPE_CACHE.put(
                        descriptor, com.kenai.jffi.Array.newArray(Type.UINT8,
                        checkSizeValid(descriptor.size())));
            }
        }
    }

}

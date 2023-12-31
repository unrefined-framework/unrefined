package unrefined.desktop;

import com.kenai.jffi.Type;
import unrefined.util.NotInstantiableError;

public final class ABI {

    private ABI() {
        throw new NotInstantiableError(ABI.class);
    }

    public static final int I = Type.UINT.size();
    public static final int L = Type.ULONG.size();
    public static final int P = Type.POINTER.size();

    public static final Class<?> I_TYPE = I == 8 ? long.class : int.class;
    public static final Class<?> L_TYPE = L == 8 ? long.class : int.class;
    public static final Class<?> P_TYPE = P == 8 ? long.class : int.class;

    public static final String IDENTIFIER;
    static {
        if (P == 8) {
            if (L == 8) {
                if (I == 8) IDENTIFIER = "ILP64";
                else IDENTIFIER = "LP64";
            }
            else IDENTIFIER = "LLP64";
        }
        else IDENTIFIER = "ILP32";
    }

}

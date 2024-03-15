package unrefined.util.function;

@FunctionalInterface
public interface VarFunctor<R> {

    R actuate(Object... args);

}

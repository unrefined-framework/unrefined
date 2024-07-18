package unrefined.beans;

public interface Attribute {

    Class<?> getType();
    default boolean isValid(Object value) {
        return value == null || getType().isInstance(value);
    }

}

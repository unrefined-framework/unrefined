package unrefined.util;

public interface Swappable {

    void to(Object dst);
    void from(Object src);
    void swap(Object o);

    Object clone();

}

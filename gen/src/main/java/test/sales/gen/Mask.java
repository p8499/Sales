package test.sales.gen;

public interface Mask<T extends Mask> {
    T all(boolean b);
    T keys(boolean b);
    T attributes(boolean b);
    T physicals(boolean b);
    T virtuals(boolean b);
    boolean get(String p);
    T set(String p, boolean b);
}
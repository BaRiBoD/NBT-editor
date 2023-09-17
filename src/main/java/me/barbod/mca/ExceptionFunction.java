package me.barbod.mca;

@FunctionalInterface
public interface ExceptionFunction<T, V, R extends Exception> {
    V accept(T t) throws R;
}

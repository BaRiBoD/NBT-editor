package me.barbod.io;

@FunctionalInterface
public interface ExceptionBiFunction<T, U, V, E extends Exception> {
    V accept(T t, U u) throws E;
}

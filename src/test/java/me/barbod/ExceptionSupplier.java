package me.barbod;

public interface ExceptionSupplier<T, V extends Exception> {
    T run() throws V;
}

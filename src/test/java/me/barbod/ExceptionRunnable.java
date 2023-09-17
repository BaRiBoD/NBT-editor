package me.barbod;

@FunctionalInterface
public interface ExceptionRunnable<T extends Exception> {
    void run () throws T;
}

package ru.spliterash.pcmasterclient.interfaces;

@FunctionalInterface
public interface ExceptionSupplier<T> {
    T get() throws Throwable;
}

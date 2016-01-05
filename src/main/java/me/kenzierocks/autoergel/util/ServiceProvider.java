package me.kenzierocks.autoergel.util;

public interface ServiceProvider<T> {

    T provide();

    Class<? extends T> getProvidedType();

    default int voteFor(ServiceProvider<? extends T> provider) {
        return 0;
    }

}
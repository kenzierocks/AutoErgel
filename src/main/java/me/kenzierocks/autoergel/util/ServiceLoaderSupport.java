package me.kenzierocks.autoergel.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.FluentIterable;

public final class ServiceLoaderSupport {

    public static interface ServiceProvider<T>
            extends Comparable<ServiceProvider<T>> {

        T provide();

        Class<T> getProvidedType();

        @Override
        int compareTo(ServiceProvider<T> other);

    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<ServiceProvider<T>>
            provideServiceProviders(Class<T> service) {
        checkNotNull(service, "service class cannot be null");
        Cast<Object, ServiceProvider<T>> cast =
                Cast.create(ServiceProvider.class, ServiceProvider.class);
        return Streams
                .fromSpliterator(
                        ServiceLoader.load(ServiceProvider.class).spliterator())
                .filter(sp -> service.equals(sp.getProvidedType())).map(cast);
    }

    public static <T> Optional<T> provideFirstService(Class<T> service) {
        return Optional.of(ServiceLoader.load(service).iterator())
                .filter(Iterator::hasNext).map(Iterator::next);
    }

    public static <T> Optional<T> provideNegotiatedService(Class<T> service) {
        Streams.fromIterator(provideServiceProviders(service)).
    }

    private ServiceLoaderSupport() {
    }

}

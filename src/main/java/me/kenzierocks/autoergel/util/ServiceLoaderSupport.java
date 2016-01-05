/*
 * This file is part of Autoergel, licensed under the MIT License (MIT).
 *
 * Copyright (c) kenzierocks (Kenzie Togami) <http://kenzierocks.me>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.kenzierocks.autoergel.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public final class ServiceLoaderSupport {

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
        List<ServiceProvider<T>> provided =
                provideServiceProviders(service).collect(Collectors.toList());
        return provided.stream().sorted(voteSort(provided)).findFirst()
                .map(ServiceProvider::provide);
    }

    /**
     * Vote sort uses a list of {@link ServiceProvider}s to vote on each
     * instance and then compares them by that.
     */
    private static <T> Comparator<? super ServiceProvider<T>>
            voteSort(List<ServiceProvider<T>> provided) {
        Multiset<ServiceProvider<T>> votes = HashMultiset.create();
        Consumer<ServiceProvider<T>> countVotes = (x) -> {
            if (!votes.contains(x)) {
                // add 1 extra vote to "mark" that we voted it in
                votes.add(x, 1
                        + provided.stream().mapToInt(z -> z.voteFor(x)).sum());
            }
        };
        return (a, b) -> {
            countVotes.accept(a);
            countVotes.accept(b);
            // Compare the opposite way so that biggest vote is first.
            return -Integer.compare(votes.count(a), votes.count(b));
        };
    }

    private ServiceLoaderSupport() {
    }

}

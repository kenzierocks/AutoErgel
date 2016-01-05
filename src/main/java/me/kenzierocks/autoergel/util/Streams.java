package me.kenzierocks.autoergel.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Streams {

    public static <T> Stream<? extends T>
            fromIterable(Iterable<? extends T> iter) {
        return fromSpliterator(iter.spliterator());
    }

    public static <T> Stream<? extends T>
            fromIterator(Iterator<? extends T> iter) {
        if (!iter.hasNext()) {
            return Stream.empty();
        }
        return fromSpliterator(Spliterators.spliteratorUnknownSize(iter, 0));
    }

    public static <T> Stream<? extends T>
            fromSpliterator(Spliterator<? extends T> iter) {
        return StreamSupport.stream(iter, false);
    }

    private Streams() {
    }

}

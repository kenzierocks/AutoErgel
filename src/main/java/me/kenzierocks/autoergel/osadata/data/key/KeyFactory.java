/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package me.kenzierocks.autoergel.osadata.data.key;

import java.util.Map;

import com.google.common.base.Objects;

import me.kenzierocks.autoergel.osadata.data.DataQuery;
import me.kenzierocks.autoergel.osadata.data.DataView;
import me.kenzierocks.autoergel.osadata.data.value.BaseValue;
import me.kenzierocks.autoergel.osadata.data.value.mutable.ListValue;
import me.kenzierocks.autoergel.osadata.data.value.mutable.MapValue;
import me.kenzierocks.autoergel.osadata.data.value.mutable.SetValue;
import me.kenzierocks.autoergel.osadata.data.value.mutable.Value;

/**
 * A factory of {@link Key}s, useful for both the implementation of SpongeAPI,
 * and for plugins wishing to provide their own {@link Key}s without having to
 * remain afraid of having to cast back and forth.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class KeyFactory {

    private KeyFactory() {
    }

    /**
     * Creates a new {@link Key} with the provided <code>E</code> element class
     * and <code>V</code> {@link Value} class along with the provided default
     * {@link DataQuery} to be used with the generated {@link Key}.
     *
     * <p>
     * Note that {@link Key}s are not registered, but it is recommended to avoid
     * generating {@link Key}s of potentially conflicting {@link DataQuery}(s).
     * </p>
     *
     * @param elementClass
     *            The element class
     * @param valueClass
     *            The value class
     * @param query
     *            The query
     * @param <E>
     *            The type of element
     * @param <T>
     *            The type of base value class
     * @param <V>
     *            The inferred return type
     * @return The generated key
     */
    public static <E, T extends BaseValue, V extends BaseValue<E>> Key<V>
            makeSingleKey(final Class<E> elementClass,
                    final Class<T> valueClass, final DataQuery query) {
        return new Key<V>() {

            @Override
            public Class<V> getValueClass() {
                return (Class<V>) (Class) valueClass;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(elementClass, valueClass, query);
            }

            @Override
            public String toString() {
                return "Key{Value:" + valueClass.getSimpleName() + "<"
                        + elementClass.getSimpleName() + ">, Query: "
                        + query.toString() + "}";
            }
        };
    }

    /**
     * Creates a new {@link Key} based on a {@link ListValue} of a type
     * <code>E</code> element along with the provided {@link DataQuery}.
     *
     * @param elementClass
     *            The element class
     * @param query
     *            The query to access the data
     * @param <E>
     *            The type of element
     * @return The generated key
     */
    public static <E> Key<ListValue<E>> makeListKey(final Class<E> elementClass,
            final DataQuery query) {
        return new Key<ListValue<E>>() {

            @Override
            public Class<ListValue<E>> getValueClass() {
                return (Class<ListValue<E>>) (Class) ListValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(elementClass, query);
            }

            @Override
            public String toString() {
                return "Key{Value:" + "ListValue<"
                        + elementClass.getSimpleName() + ">, Query: "
                        + query.toString() + "}";
            }
        };
    }

    /**
     * Creates a new {@link Key} based on a {@link SetValue} of a type
     * <code>E</code> element along with the provided {@link DataQuery}.
     *
     * @param elementClass
     *            The element class
     * @param query
     *            The query to access the data
     * @param <E>
     *            The type of element
     * @return The generated key
     */
    public static <E> Key<SetValue<E>> makeSetKey(final Class<E> elementClass,
            final DataQuery query) {
        return new Key<SetValue<E>>() {

            @Override
            public Class<SetValue<E>> getValueClass() {
                return (Class<SetValue<E>>) (Class) SetValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(elementClass, query);
            }

            @Override
            public String toString() {
                return "Key{Value:" + "SetValue<" + elementClass.getSimpleName()
                        + ">, Query: " + query.toString() + "}";
            }
        };
    }

    /**
     * Creates a new {@link Key} based on a {@link MapValue} of the types
     * <code>K</code> keys and <code>V</code> values with the provided
     * {@link DataQuery} for accessing the {@link Map} in {@link DataView}s.
     *
     * @param keyClass
     *            The key class of the map
     * @param valueclass
     *            The value class of the map
     * @param query
     *            The query
     * @param <K>
     *            The type of keys
     * @param <V>
     *            The type of values
     * @return The generated key
     */
    public static <K, V> Key<MapValue<K, V>> makeMapKey(final Class<K> keyClass,
            final Class<V> valueclass, final DataQuery query) {
        return new Key<MapValue<K, V>>() {

            @Override
            public Class<MapValue<K, V>> getValueClass() {
                return (Class<MapValue<K, V>>) (Class) MapValue.class;
            }

            @Override
            public DataQuery getQuery() {
                return query;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(keyClass, valueclass, query);
            }

            @Override
            public String toString() {
                return "Key{Value:" + "MapValue<" + keyClass.getSimpleName()
                        + "," + valueclass.getSimpleName() + ">, Query: "
                        + query.toString() + "}";
            }
        };
    }

}

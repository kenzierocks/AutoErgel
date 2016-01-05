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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class Cast<F, T>
        implements Function<F, T>, com.google.common.base.Function<F, T> {

    private static final LoadingCache<Class<?>, Cast<?, ?>> GENERIC_CAST =
            CacheBuilder.newBuilder()
                    .build(CacheLoader.from(c -> new Cast<>(c, c)));

    public static <FX, TX> TX cast(FX from) {
        return uncheckedCast(from);
    }

    public static <FX, TX> Cast<FX, TX> create() {
        return create(null, null);
    }

    public static <FX, TX> Cast<FX, TX> create(
            @Nullable Class<? extends FX> forcedFromClass,
            @Nullable Class<? extends TX> forcedToClass) {
        if (forcedFromClass == forcedToClass) {
            return cast(GENERIC_CAST.getUnchecked(forcedToClass));
        }
        return new Cast<>(forcedFromClass, forcedToClass);
    }

    private final Class<? extends F> fromClass;
    private final Class<? extends T> toClass;

    private Cast() {
        this(null, null);
    }

    private Cast(Class<? extends F> fC, Class<? extends T> tC) {
        this.fromClass = fC;
        this.toClass = tC;
    }

    @SuppressWarnings("unchecked")
    private static <X> X uncheckedCast(Object from) {
        return (X) from;
    }

    @Override
    public T apply(F from) {
        if (this.fromClass != null) {
            checkArgument(this.fromClass.isInstance(from),
                    "illegal call, passed F != forced F");
        }
        if (this.toClass != null) {
            checkArgument(this.toClass.isInstance(from),
                    "illegal call, passed F is not an instance of forced T");
            return this.toClass.cast(from);
        }
        return uncheckedCast(from);
    }

}

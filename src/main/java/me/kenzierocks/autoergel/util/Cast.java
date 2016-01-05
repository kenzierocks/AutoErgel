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

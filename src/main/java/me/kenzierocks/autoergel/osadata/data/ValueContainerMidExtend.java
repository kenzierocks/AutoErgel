package me.kenzierocks.autoergel.osadata.data;

import java.util.Collection;

import me.kenzierocks.autoergel.osadata.data.value.ValueContainer;
import me.kenzierocks.autoergel.osadata.data.value.immutable.ImmutableValueStore;

public interface ValueContainerMidExtend<I extends ValueContainer<I>, H> extends ValueContainer<I> {

    /**
     * Gets an copied collection of all known {@link ValueContainer}s belonging
     * to this {@link ImmutableValueStore}. An individual {@link ValueContainer}
     * can be used for data processing for various purposes.
     *
     * @return A collection of copied {@link ValueContainer}s originating from
     *         this value store
     */
    Collection<H> getContainers();

}

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
package me.kenzierocks.autoergel.recipe;

import java.util.Optional;
import java.util.Set;

import me.kenzierocks.autoergel.osadata.data.DataContainer;
import me.kenzierocks.autoergel.osadata.data.DataHolder;
import me.kenzierocks.autoergel.osadata.data.DataManager;
import me.kenzierocks.autoergel.osadata.data.ImmutableDataHolder;
import me.kenzierocks.autoergel.osadata.data.ValueContainerMidExtend;
import me.kenzierocks.autoergel.osadata.data.manipulator.DataManipulator;
import me.kenzierocks.autoergel.osadata.data.manipulator.ImmutableDataManipulator;
import me.kenzierocks.autoergel.util.ServiceLoaderSupport;

/**
 * This design of keeping all "generic" names inside this AutoErgel class allows
 * for easy use with similar systems. For example, Minecraft also has ItemStack,
 * this allows use of both without fully qualified names.
 */
public abstract class AutoErgel {

    private static final DataManager DM = ServiceLoaderSupport
            .provideNegotiatedService(DataManager.class)
            .orElseThrow(() -> new IllegalStateException("no DataManager"));

    public static DataManager getDataManager() {
        return DM;
    }

    public interface ItemType {

        interface Provider {

            Provider INSTANCE = ServiceLoaderSupport
                    .provideNegotiatedService(Provider.class)
                    .orElseThrow(() -> new IllegalStateException(
                            "no item type provider"));

            Set<ItemType> getProvidedItemTypes();

            Optional<ItemType> getById(String id);

            /**
             * The NONE type must be available on all implementations. It is
             * vital to the workings of the system.
             */
            ItemType getNoneType();

        }

        ItemType NONE = Provider.INSTANCE.getNoneType();

    }

    /**
     * Required to make a defensive copy of {@link #NONE_STACK}. TODO: Consider
     * implementing immutable stacks at some point.
     */
    private static final class ItemStackHelper {

        /**
         * This field is not guaranteed to be the only stack of {@code NONE}
         * available, it is meant as a convenience only.
         */
        private static final ItemStack NONE_STACK =
                ItemStack.of(ItemType.Provider.INSTANCE.getNoneType(), 0);

        private static boolean manipulatorsEqual(ISCommon<?, ?, ?> o1,
                ISCommon<?, ?, ?> o2) {
            return o1.getContainers().equals(o2.getContainers());
        }

    }

    private interface ISCommon<TYPE extends ISCommon<TYPE, VC, DM>, VC extends ValueContainerMidExtend<VC, DM>, DM>
            extends ValueContainerMidExtend<VC, DM> {

        Class<TYPE> $getInterface();

        ItemType getItem();

        // TODO - always assume int quantity?
        int getQuantity();

        // TODO - always assume int damage?
        int getDamage();

        int getMaxDamage();

        default boolean equalIgnoringSize(ISCommon<?, ?, ?> other) {
            return other != null && other.$getInterface() == $getInterface()
                    && other.getDamage() == getDamage()
                    && other.getItem().equals(getItem())
                    && ItemStackHelper.manipulatorsEqual(this, other);
        }

    }

    public interface ItemStackSnapshot extends
            ISCommon<ItemStackSnapshot, ItemStackSnapshot, ImmutableDataManipulator<?, ?>>,
            ImmutableDataHolder<ItemStackSnapshot> {

        default ItemStack createStack() {
            return ItemStack.Factory.INSTANCE.create(getItem(), getQuantity(),
                    getDamage(), toContainer());
        }

        @Override
        default Class<ItemStackSnapshot> $getInterface() {
            return ItemStackSnapshot.class;
        }

    }

    /**
     * Mutable item stack.
     */
    public interface ItemStack extends
            ISCommon<ItemStack, DataHolder, DataManipulator<?, ?>>, DataHolder {

        interface Factory {

            Factory INSTANCE =
                    ServiceLoaderSupport.provideNegotiatedService(Factory.class)
                            .orElseThrow(() -> new IllegalStateException(
                                    "no item stack factory"));

            ItemStack create(ItemType item, int quantity);

            ItemStack create(ItemType item, int quantity, int damage);

            ItemStack create(ItemType item, int quantity, int damage,
                    DataContainer data);

            ItemStackSnapshot createSnapshot(ItemType item, int quantity);

            ItemStackSnapshot createSnapshot(ItemType item, int quantity,
                    int damage);

            ItemStackSnapshot createSnapshot(ItemType item, int quantity,
                    int damage, DataContainer data);

        }

        static ItemStack of(ItemType item, int quantity) {
            return Factory.INSTANCE.create(item, quantity);
        }

        static ItemStack getNoneStack() {
            return ItemStackHelper.NONE_STACK.copy();
        }

        void setItem(ItemType item);

        void setQuantity(int quantity);

        void setDamage(int damage);

        default ItemStackSnapshot createSnapshot() {
            return Factory.INSTANCE.createSnapshot(getItem(), getQuantity(),
                    getDamage(), toContainer());
        }

        @Override
        ItemStack copy();

        @Override
        default Class<ItemStack> $getInterface() {
            return ItemStack.class;
        }

    }

    // package-private to allow AE subclass
    AutoErgel() {
    }

}

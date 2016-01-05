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

import java.util.function.Supplier;

import me.kenzierocks.autoergel.osadata.data.DataManager;
import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStack;
import me.kenzierocks.autoergel.recipe.AutoErgel.ItemType;

public final class ServiceProviders {

    private static abstract class SPBase<T> implements ServiceProvider<T> {

        private final Class<? extends T> providedType;
        private final Supplier<? extends T> supplier;

        SPBase(Class<? extends T> providedType, T instance) {
            this.providedType =
                    checkNotNull(providedType, "provided type cannot be null");
            checkNotNull(instance, "instance cannot be null");
            this.supplier = () -> instance;
        }

        SPBase(Class<? extends T> providedType,
                Supplier<? extends T> supplier) {
            this.providedType =
                    checkNotNull(providedType, "provided type cannot be null");
            this.supplier = checkNotNull(supplier, "supplier cannot be null");
        }

        @Override
        public Class<? extends T> getProvidedType() {
            return this.providedType;
        }

        @Override
        public T provide() {
            return this.supplier.get();
        }

        @Override
        public String toString() {
            return "ServiceProvider<" + this.providedType.getName() + ">";
        }

    }

    public static class DataManagerProvider extends SPBase<DataManager> {

        private static final Class<DataManager> CLASS = DataManager.class;

        public DataManagerProvider(DataManager instance) {
            super(CLASS, instance);
        }

        public DataManagerProvider(Supplier<DataManager> provider) {
            super(CLASS, provider);
        }

    }

    public static class ItemStackFactoryProvider
            extends SPBase<ItemStack.Factory> {

        private static final Class<ItemStack.Factory> CLASS =
                ItemStack.Factory.class;

        public ItemStackFactoryProvider(ItemStack.Factory instance) {
            super(CLASS, instance);
        }

        public ItemStackFactoryProvider(Supplier<ItemStack.Factory> provider) {
            super(CLASS, provider);
        }

    }

    public static class ItemTypeProviderProvider
            extends SPBase<ItemType.Provider> {

        private static final Class<ItemType.Provider> CLASS =
                ItemType.Provider.class;

        public ItemTypeProviderProvider(ItemType.Provider instance) {
            super(CLASS, instance);
        }

        public ItemTypeProviderProvider(Supplier<ItemType.Provider> provider) {
            super(CLASS, provider);
        }

    }

    private ServiceProviders() {
    }

}

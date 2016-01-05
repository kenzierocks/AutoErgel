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

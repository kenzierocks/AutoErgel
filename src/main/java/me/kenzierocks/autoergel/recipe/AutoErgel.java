package me.kenzierocks.autoergel.recipe;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import me.kenzierocks.autoergel.osadata.data.DataHolder;
import me.kenzierocks.autoergel.osadata.data.DataManager;
import me.kenzierocks.autoergel.osadata.data.DataSerializable;
import me.kenzierocks.autoergel.util.ServiceLoaderSupport;

/**
 * This design of keeping all "generic" names inside this AutoErgel class allows
 * for easy use with similar systems. For example, Minecraft also has ItemStack,
 * this allows use of both without fully qualified names.
 */
public abstract class AutoErgel {

    private static final DataManager DM = ServiceLoaderSupport
            .singletonService(DataManager.class).orElse(null);

    public static DataManager getDataManager() {
        return DM;
    }

    public interface ItemType {
    }

    public interface Item {
    }

    /**
     * Immutable item stack.
     */
    public interface ItemStack extends DataHolder, DataSerializable {

        interface Factory {

            Factory INSTANCE = ServiceLoader.load(Factory.class).

        }

        Item getItem();

        // TODO - always assume int quantity?
        int getQuantity();

        ItemStack setItem(Item item);

        ItemStack setQuantity(int quantity);

        @Override
        ItemStack copy();

    }

    AutoErgel() {
    }

}

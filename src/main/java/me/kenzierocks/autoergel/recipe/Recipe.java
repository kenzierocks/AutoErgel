package me.kenzierocks.autoergel.recipe;

import java.util.Optional;
import java.util.function.Function;

import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStack;


public interface Recipe {

    /**
     * Attempts to apply this recipe to the given items + layout.
     * 
     * @return The result item, if present.
     */
    Optional<ItemStack> tryToApplyRecipe(CraftingData data);

    /**
     * Attempts to apply this recipe to the given items + layout.
     * 
     * @return The result item, if present.
     */
    Optional<CraftingData> onResultTaken(CraftingData data,
            ItemStack takenResult,
            Function<ItemStack, ItemStack> getContainerItem);

    CraftingData removeItemsForOneApply(CraftingData data,
            Function<ItemStack, ItemStack> getContainerItem);

}
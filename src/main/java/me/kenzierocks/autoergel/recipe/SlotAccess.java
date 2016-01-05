package me.kenzierocks.autoergel.recipe;

import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStack;

public interface SlotAccess {

    ItemStack getSlot(int x, int y);

    void setSlot(int x, int y, ItemStack item);

    void removeStack(ItemStack stack);

}

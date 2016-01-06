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

import static com.google.common.base.Preconditions.checkState;

import java.util.Optional;
import java.util.function.Function;

import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStackSnapshot;
import me.kenzierocks.autoergel.recipe.AutoErgel.ItemType;

public interface ShapedRecipe extends Recipe {

    int getRows();

    int getCols();

    ItemStackSnapshot getStackAt(int r, int c);

    ItemStackSnapshot translateLayoutToOutput(ItemStackSnapshot[][] asLayout);

    /**
     * Check if the given ItemStack matches at the given row and column. Any
     * stacks that contain {@link ItemTypes#NONE} are equivalent to {@code null}
     * , i.e. if {@code stack} is {@code null}, and the recipe has a stack of
     * type {@code NONE} at the position, then this method returns {@code true}.
     * Item count is ignored.
     * 
     * @param r
     *            - row
     * @param c
     *            - column
     * @return {@code true} if it matches, otherwise {@code false}
     */
    default boolean matches(ItemStackSnapshot stack, int r, int c) {
        if (stack == null) {
            // special case
            return getStackAt(r, c).getItem().equals(ItemType.NONE);
        }
        return getStackAt(r, c).equalIgnoringSize(stack);
    }

    @Override
    default Optional<ItemStackSnapshot> tryToApplyRecipe(CraftingData data) {
        checkState(getRows() > 0, "cannot have 0 rows");
        checkState(getCols() > 0, "cannot have 0 columns");
        if (data.getAsLayout().length < getRows()
                || data.getAsLayout().length < 1) {
            return Optional.empty();
        }
        if (data.getAsLayout()[0].length < getCols()) {
            return Optional.empty();
        }
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                ItemStackSnapshot user = data.getAsLayout()[r][c];
                if (!matches(user, r, c)) {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(translateLayoutToOutput(data.getAsLayout()));
    }

    @Override
    default Optional<CraftingData> onResultTaken(CraftingData data,
            ItemStackSnapshot takenResult,
            Function<ItemStackSnapshot, ItemStackSnapshot> getContainerItem) {
        // First, double check that the recipe matches
        return tryToApplyRecipe(data).map(res -> {
            // Next, double check the amounts
            int possible = res.getQuantity();
            int taken = takenResult.getQuantity();
            if (possible < taken) {
                // Too many taken - take none
                return null;
            }
            // Next, apply the recipe repeatedly
            int done = 0;
            CraftingData next = data;
            while (true) {
                try {
                    next = removeItemsForOneApply(next, getContainerItem);
                    done++;
                } catch (IllegalArgumentException cannotApply) {
                    break;
                }
            }
            if (done != taken) {
                // bahasdasd?
                return null;
            }
            return next;
        });
    }

    @Override
    default CraftingData removeItemsForOneApply(CraftingData data,
            Function<ItemStackSnapshot, ItemStackSnapshot> getContainerItem) {
        ItemStackSnapshot[][] layout = data.getAsLayout();
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < getCols(); c++) {
                ItemStackSnapshot recipeStack = getStackAt(r, c).copy();
                ItemStackSnapshot layoutStack = layout[r][c];
                ItemStackSnapshot containerItem = getContainerItem.apply(layoutStack);
                if (containerItem != null) {
                    int q = layoutStack.getQuantity();
                    layoutStack = containerItem.copy().withQuantity(q);
                }
                if (!recipeStack.equalIgnoringSize(layoutStack)
                        || (layoutStack.getQuantity()
                                - recipeStack.getQuantity()) < 0) {
                    throw new IllegalArgumentException("Cannot apply once");
                }
                while (recipeStack.getQuantity() > 0
                        && layoutStack.getQuantity() > 0) {
                    layoutStack = layoutStack.withQuantityChange(-1);
                    recipeStack = recipeStack.withQuantityChange(-1);
                }
            }
        }
        return data.withLayout(layout);
    }

    interface SingleOutput extends ShapedRecipe {

        ItemStackSnapshot getOutput();

        @Override
        default ItemStackSnapshot translateLayoutToOutput(ItemStackSnapshot[][] asLayout) {
            return getOutput();
        }

    }

}
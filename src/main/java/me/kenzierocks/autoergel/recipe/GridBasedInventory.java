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

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStack;

public interface GridBasedInventory {

    int getWidth();

    int getHeight();

    /**
     * Returns the contents of this inventory, in no particular order.
     */
    default Stream<ItemStack> getContentsAsStream() {
        return IntStream.range(0, getWidth())
                .mapToObj(w -> IntStream.range(0, getHeight())
                        .mapToObj(h -> getSlot(w, h)))
                .flatMap(Function.identity());
    }

    default ItemStack[][] getContentsAsGrid() {
        ItemStack[][] grid = new ItemStack[getWidth()][getHeight()];
        for (int i = 0; i < grid.length; i++) {
            ItemStack[] row = grid[i];
            for (int j = 0; j < row.length; j++) {
                row[j] = getSlot(i, j);
            }
        }
        return grid;
    }

    ItemStack getSlot(int x, int y);

    /**
     * N.B. implementations should make defensive copy of {@code item}
     */
    void setSlot(int x, int y, ItemStack item);

    /**
     * Remove {@code {@link ItemStack#getQuantity() stack.getQuantity()} amount
     * of {@code {@link ItemStack#getItem() stack.getItem()} from this
     * inventory. The quantity may be removed across multiple individual stacks.
     * 
     * @return The leftovers of {@code stack} that were not removed. If the
     *         quantity is 0, then all items were removed.
     */
    ItemStack removeStack(ItemStack stack);

}

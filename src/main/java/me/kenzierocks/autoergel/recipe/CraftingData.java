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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import me.kenzierocks.autoergel.osadata.util.Tuple;
import me.kenzierocks.autoergel.recipe.AutoErgel.ItemStackSnapshot;

public class CraftingData {

    private static ItemStackSnapshot[][]
            deepCopyGrid(ItemStackSnapshot[][] asLayout) {
        int len = Stream.of(asLayout).filter(Objects::nonNull)
                .mapToInt(x -> x.length).findFirst().orElse(0);
        return Stream.of(asLayout)
                .map(z -> z == null ? new ItemStackSnapshot[len] : z)
                .map(x -> Stream.of(x)
                        .map(z -> Optional.ofNullable(z)
                                .orElseGet(ItemStackSnapshot::getNoneStack))
                        .map(ItemStackSnapshot::copy)
                        .toArray(ItemStackSnapshot[]::new))
                .toArray(ItemStackSnapshot[][]::new);
    }

    private final ItemStackSnapshot[][] asLayout;
    private final List<ItemStackSnapshot> asList;
    private transient ItemStackSnapshot[][] cachedCopy;

    public CraftingData(ItemStackSnapshot[][] asLayout,
            List<ItemStackSnapshot> asList) {
        this.asLayout = deepCopyGrid(asLayout);
        this.asList = ImmutableList.copyOf(
                asList.stream().map(ItemStackSnapshot::copy).iterator());
    }

    public ItemStackSnapshot[][] getAsLayout() {
        // default to caching because it's easy.
        return getAsLayout(true);
    }

    public ItemStackSnapshot[][] getAsLayout(boolean cachedCopy) {
        if (cachedCopy) {
            if (this.cachedCopy == null) {
                this.cachedCopy = getAsLayout(false);
            }
            return this.cachedCopy;
        } else {
            return deepCopyGrid(this.asLayout);
        }
    }

    public List<ItemStackSnapshot> getAsList() {
        return this.asList;
    }

    public Tuple<List<ItemStackSnapshot>, CraftingData>
            removeStacks(ItemStackSnapshot... stacks) {
        ItemStackSnapshot[][] layout = getAsLayout();
        List<ItemStackSnapshot> result =
                ImmutableList.copyOf(Stream.of(stacks).filter(Objects::nonNull)
                        .map(ItemStackSnapshot::copy).map(x -> {
                            if (x == null || x.getQuantity() <= 0) {
                                return null;
                            }
                            // For every stack2remove (S2R)
                            everything: for (int r =
                                    0; r < layout.length; r++) {
                                for (int c = 0; c < layout[0].length; c++) {
                                    ItemStackSnapshot atPos = layout[r][c];
                                    // Get the stack in inventory (SII)
                                    if (atPos != null
                                            && atPos.equalIgnoringSize(x)) {
                                        while (atPos != null) {
                                            // Reduce the quantity, slowly
                                            x = x.withQuantityChange(-1);
                                            atPos = atPos
                                                    .withQuantityChange(-1);
                                            if (atPos.getQuantity() == 0) {
                                                atPos = null;
                                            }
                                            if (x.getQuantity() == 0) {
                                                break everything;
                                            }
                                        }
                                    }
                                    // Set back stack
                                    layout[r][c] = atPos;
                                }
                            }
                            return x.getQuantity() <= 0 ? null : x;
                        }).filter(Objects::nonNull).iterator());
        return Tuple.of(result, this.withLayout(layout));
    }

    public CraftingData withLayout(ItemStackSnapshot[][] layout) {
        List<ItemStackSnapshot> list = Stream.of(layout).flatMap(Stream::of)
                .filter(Objects::nonNull).collect(Collectors.toList());
        return new CraftingData(layout, list);
    }

    public CraftingData withList(List<ItemStackSnapshot> list) {
        ItemStackSnapshot[][] layout = getAsLayout();
        Iterator<ItemStackSnapshot> iter = list.iterator();
        for (int r = 0; r < layout.length && iter.hasNext(); r++) {
            for (int c = 0; c < layout[0].length && iter.hasNext(); c++) {
                layout[r][c] = iter.next();
            }
        }
        return new CraftingData(layout, list);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(this.asLayout);
        result = prime * result
                + ((this.asList == null) ? 0 : this.asList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CraftingData)) {
            return false;
        }
        CraftingData other = (CraftingData) obj;
        if (!Arrays.deepEquals(this.asLayout, other.asLayout)) {
            return false;
        }
        if (this.asList == null) {
            if (other.asList != null) {
                return false;
            }
        } else if (!this.asList.equals(other.asList)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CraftingData[asLayout=" + Arrays.deepToString(this.asLayout)
                + ",asList=" + this.asList + "]";
    }

}

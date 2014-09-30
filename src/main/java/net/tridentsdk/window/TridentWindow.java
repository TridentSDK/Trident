/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package net.tridentsdk.window;

import net.tridentsdk.api.inventory.ItemStack;
import net.tridentsdk.api.window.Window;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TridentWindow implements Window {
    /**
     * Counter for window ids, inital value is 2 to avoid confusion with a window and a player inventory
     */
    private static final AtomicInteger counter = new AtomicInteger(2);

    private final int id;
    private final String name;
    private final int length;
    private ItemStack[] contents;

    public TridentWindow(String name, int length) {
        this.name = name;
        this.length = length;
        this.id = counter.addAndGet(1);
        this.contents = new ItemStack[length];
    }

    public TridentWindow(int length) {
        this("", length);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ItemStack[] getContents() {
        return contents;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSlot(int index, ItemStack value) {
        contents[index] = value;
        // TODO: update client
    }
}

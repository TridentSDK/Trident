/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.api.entity.block;

import net.tridentsdk.api.entity.Hanging;
import net.tridentsdk.api.inventory.ItemStack;

/**
 * Represents an ItemFrame
 *
 * @author TridentSDK Team
 */
public interface ItemFrame extends Hanging {
    /**
     * Get the current ItemStack this ItemFrame has
     *
     * @return the current ItemStack this ItemFrame has
     */
    ItemStack getCurrentItem();

    /**
     * Get the rotation of this ItemFrame's ItemStack This is the number of times this has been rotated 45 degrees
     *
     * @return the rotation of this ItemFrame's ItemStack
     */
    byte getItemStackRotation();
}

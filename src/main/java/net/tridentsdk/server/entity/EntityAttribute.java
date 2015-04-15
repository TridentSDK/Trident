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
package net.tridentsdk.server.entity;

import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.NBTSerializable;
import net.tridentsdk.meta.nbt.TagType;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

@NotThreadSafe
// TODO move to API and fix thread safety
public class EntityAttribute implements NBTSerializable {
    @NBTField(name = "Name", type = TagType.STRING)
    protected String name;
    @NBTField(name = "Base", type = TagType.DOUBLE)
    protected double value;
    @NBTField(name = "Modifiers", type = TagType.LIST)
    protected List<Modifier> modifiers;

    protected EntityAttribute() {
    }

    public String name() {
        return name;
    }

    public double value() {
        return value;
    }

    public List<Modifier> modifiers() {
        return modifiers;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public void removeModifier(int index) {
        modifiers.remove(index);
    }

    public void setModifier(int index, Modifier modifier) {
        modifiers.set(index, modifier);
    }

    @NotThreadSafe
    public static class Modifier implements NBTSerializable {
        @NBTField(name = "Name", type = TagType.STRING)
        protected String name;
        @NBTField(name = "Amount", type = TagType.DOUBLE)
        protected double amount;
        @NBTField(name = "Operation", type = TagType.INT)
        protected int operation;

        public Modifier() {}

        public String name() {
            return name;
        }

        public double amount() {
            return amount;
        }

        public int operation() {
            return operation;
        }
    }
}

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

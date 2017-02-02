package net.tridentsdk.server.inventory;

import lombok.Getter;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.inventory.Inventory;
import net.tridentsdk.inventory.InventoryType;
import net.tridentsdk.inventory.Item;

import javax.annotation.Nullable;

/**
 * Implementation of an arbitrary inventory.
 */
public class TridentInventory implements Inventory {
    /**
     * Inventory type
     */
    @Getter
    private final InventoryType type;
    /**
     * The amount of slots available in this inventory
     */
    @Getter
    private final int size;

    /**
     * Constructs a new inventory with the given type and
     * slot amount.
     *
     * @param type the inventory type
     * @param size the amount of slots that the new
     * inventory should contain
     */
    public TridentInventory(InventoryType type, int size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public boolean add(Item item, int quantity) {
        return false;
    }

    @Override
    public Item add(int slot, Item item, int quantity) {
        return null;
    }

    @Nullable
    @Override
    public Item remove(int slot, int quantity) {
        return null;
    }

    @Nullable
    @Override
    public Item get(int slot) {
        return null;
    }

    @Override
    public ChatComponent getTitle() {
        return null;
    }

    @Override
    public void setTitle(ChatComponent title) {
    }
}
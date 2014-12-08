package net.tridentsdk.server.entity.decorate;

import net.tridentsdk.docs.Volatile;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.decorate.DecorationAdapter;
import net.tridentsdk.entity.decorate.InventoryHolder;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.server.packets.play.out.PacketPlayOutOpenWindow;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.window.inventory.Inventory;
import net.tridentsdk.window.inventory.InventoryType;
import net.tridentsdk.window.inventory.ItemStack;

public class DecoratedInventoryHolder extends DecorationAdapter<Entity> implements InventoryHolder {
    @Volatile(policy = "Do not localize", reason = "IntelliJ thinks it's ok for statics", fix = "DON'T DO IT")
    private static int inventoryIds = 0;
    private final Inventory inventory;
    private final InventoryType type;

    protected DecoratedInventoryHolder(Entity entity, final String string, final int size, InventoryType type) {
        super(entity);
        inventory = new Inventory() {
            private final int inventoryId = inventoryIds++;
            private final ItemStack[] itemStacks = new ItemStack[size];
            private final String name = string;

            @Override
            public int getId() {
                return inventoryId;
            }

            @Override
            public ItemStack[] getContents() {
                return itemStacks;
            }

            @Override
            public int getLength() {
                return itemStacks.length;
            }

            @Override
            public void setSlot(int index, ItemStack value) {
                itemStacks[index] = value;
            }

            @Override
            public String getName() {
                return name;
            }
        };
        this.type = type;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack getContent(int slot) {
        return inventory.getContents()[slot];
    }

    public void applyOpenWindow(Player player) {
        PacketPlayOutOpenWindow window = new PacketPlayOutOpenWindow();
        window.set("windowId", inventory.getId())
                .set("inventoryType", type)
                .set("windowTitle", inventory.getName());
        ((TridentPlayer) player).getConnection().sendPacket(window);
    }
}

package net.tridentsdk.server.util;

import net.tridentsdk.base.Block;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.world.TridentBlock;

/**
 * Represents a block which holds data about the placer
 *
 * @author The TridentSDK Team
 */
public class OwnedTridentBlock extends TridentBlock {
    private final TridentPlayer player;

    public OwnedTridentBlock(TridentPlayer player, Block block) {
        super(block.position());
        this.player = player;
    }

    /**
     * Obtains the place
     *
     * @return
     */
    public TridentPlayer player() {
        return this.player;
    }
}

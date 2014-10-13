package net.tridentsdk.world;

import net.tridentsdk.api.Block;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.Material;

public class TridentBlock extends Block {
    public TridentBlock(Location location, Material material1, byte data) {
        super(location,true);
        super.material = material1;
        super.data = data;
    }
}

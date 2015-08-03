package net.tridentsdk.server.data.block;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.block.SignMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;

/**
 * Implements sign meta
 *
 * @author The TridentSDK Team
 */
public class SignMetaImpl implements SignMeta {
    @Override
    public String textAt(int index) {
        return null;
    }

    @Override
    public void setTextAt(int index, String text) {

    }

    @Override
    public byte encode() {
        return 0;
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue) {
        return null;
    }

    @Override
    public Meta<Block> make() {
        return null;
    }

    @Override
    public Substance[] applyTo(MetaCollection collection) {
        return new Substance[0];
    }
}

package net.tridentsdk.server.data.block;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.base.SubstanceColor;
import net.tridentsdk.meta.block.WoolMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;

/**
 * Represents data held by a wool block
 *
 * @author The TridentSDK Team
 */
public class WoolMetaImpl implements WoolMeta {
    private volatile SubstanceColor color = SubstanceColor.WHITE;

    @Override
    public void setColor(SubstanceColor color) {
        this.color = color;
    }

    @Override
    public SubstanceColor color() {
        return color;
    }

    @Override
    public Substance[] applyTo(MetaCollection collection) {
        collection.put(WoolMeta.class, this);
        return new Substance[]{Substance.WOOL};
    }

    @Override
    public Meta<Block> decode(Block instance, byte[] data) {
        WoolMeta meta = new WoolMetaImpl();
        meta.setColor(SubstanceColor.of(data[0]));
        return meta;
    }

    @Override
    public Meta<Block> make() {
        return new WoolMetaImpl();
    }
}

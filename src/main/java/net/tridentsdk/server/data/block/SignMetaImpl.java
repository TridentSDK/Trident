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
package net.tridentsdk.server.data.block;

import net.tridentsdk.base.Block;
import net.tridentsdk.base.Position;
import net.tridentsdk.base.Substance;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.entity.types.EntityType;
import net.tridentsdk.meta.block.SignMeta;
import net.tridentsdk.meta.block.Tile;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;
import net.tridentsdk.plugin.cmd.PlatformColor;
import net.tridentsdk.server.packets.play.out.PacketPlayOutSignEditorOpen;
import net.tridentsdk.server.packets.play.out.PacketPlayOutUpdateSign;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.server.util.OwnedTridentBlock;
import net.tridentsdk.server.world.TridentWorld;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Implements sign meta
 *
 * @author The TridentSDK Team
 */
public class SignMetaImpl implements SignMeta, Tile {
    private volatile byte orientation;
    private volatile Position position;
    private final AtomicReferenceArray<String> stringAtomicReferenceArray = new AtomicReferenceArray<>(4);

    @Override
    public String textAt(int index) {
        return stringAtomicReferenceArray.get(index);
    }

    @Override
    public void setTextAt(int index, String text) {
        stringAtomicReferenceArray.set(index, text);
        position.world().entities().stream().filter(e -> e.type() == EntityType.PLAYER).forEach(p -> update((Player) p));
    }

    private String[] toArray() {
        String[] strings = new String[4];
        for (int i = 0; i < 4; i++) {
            String s = stringAtomicReferenceArray.get(i);
            if (s == null) {
                setTextAt(i, s = PlatformColor.EMPTY);
            }

            strings[i] = s;
        }

        return strings;
    }

    @Override
    public byte encode() {
        // TODO handle illegal meta
        ((TridentWorld) position.world()).tilesInternal().add(this);
        return orientation;
    }

    @Override
    public Meta<Block> decode(Block instance, float yaw, byte direction, byte cx, byte cy, byte cz, short damageValue) {
        byte data = 0x00;

        yaw = (yaw + 180.0f + 45.0f) % 360.0f;     // +180 puts north at 360/0 - +45 puts north/west to 0
        int orientation = (int)(yaw / 90.0f);

        switch(orientation) {
            case 0:
                data |= 3;
                break;
            case 1:
                data |= 4;
                break;
            case 2:
                data |= 2;
                break;
            case 3:
                data |= 5;
                break;
        }

        instance.setSubstanceAndMeta(Substance.SIGN_POST, data);

        orientation = data;
        position = instance.position();
        ((OwnedTridentBlock) instance).player().connection().sendPacket(new PacketPlayOutSignEditorOpen().set("loc", position));
        return this;
    }

    @Override
    public Meta<Block> make() {
        return new SignMetaImpl();
    }

    @Override
    public Substance[] applyTo(MetaCollection collection) {
        collection.put(SignMeta.class, this);
        return new Substance[] {Substance.SIGN}; // TODO wall signs
    }

    @Override
    public void update(Player player) {
        ((TridentPlayer) player).connection().sendPacket(new PacketPlayOutUpdateSign().set("loc", position).set("messages", toArray()));
    }
}

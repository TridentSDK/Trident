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
package net.tridentsdk.server.effect.entity;

import net.tridentsdk.effect.entity.EntityStatusEffect;
import net.tridentsdk.effect.entity.EntityStatusEffectType;
import net.tridentsdk.entity.Entity;
import net.tridentsdk.entity.LivingEntity;
import net.tridentsdk.server.effect.TridentEffect;
import net.tridentsdk.server.entity.TridentEntity;
import net.tridentsdk.server.netty.packet.OutPacket;
import net.tridentsdk.server.packets.play.out.PacketPlayOutEntityStatus;

public class TridentEntityStatusEffect extends TridentEffect<EntityStatusEffectType> implements EntityStatusEffect {

    private PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();

    public TridentEntityStatusEffect(LivingEntity entity, EntityStatusEffectType type){
        packet.set("entityId", entity.entityId());
        packet.set("status", type);
    }

    @Override
    public EntityStatusEffectType type(){
        return packet.status();
    }

    @Override
    public void setType(EntityStatusEffectType type){
        packet.set("status", type);
    }

    @Override
    public void setEntity(Entity entity){
        packet.set("entityId", entity.entityId());
    }

    @Override
    public Entity entity(){
        return TridentEntity.HANDLER.entityBy(packet.entityId());
    }

    @Override
    public OutPacket getPacket(){
        return packet;
    }

}

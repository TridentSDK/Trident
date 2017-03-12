/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.ui.bossbar;

import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.ui.bossbar.BossBarColor;
import net.tridentsdk.ui.bossbar.BossBarDivision;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class DefaultBossBar extends AbstractBossBar {

    private final BossBarColor color;
    private final BossBarDivision division;

    public DefaultBossBar(/* EnderDragon dragon */) {
        this.color = BossBarColor.PINK;
        this.division = BossBarDivision.NO_DIVISION;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public ChatComponent getTitle() {
        return ChatComponent.create().setText("Just a default boss bar because #yolo");
    }

    @Override
    public void setTitle(ChatComponent title) {
        // cannot be changed
    }

    @Override
    public float getHealth() {
        // return boss's health as a percentage
        return 0;
    }

    @Override
    public void setHealth(float health) {
        // cannot be changed
    }

    @Override
    public BossBarColor getColor() {
        return this.color; // TODO - perhaps this needs to be changed
    }

    @Override
    public void setColor(BossBarColor color) {
        // cannot be changed
    }

    @Override
    public BossBarDivision getDivision() {
        return this.division;
    }

    @Override
    public void setDivision(BossBarDivision division) {
        // cannot be changed
    }

    @Override
    public boolean isDarkenSky() {
        return true; // TODO - do we darken the sky? idk
    }

    @Override
    public void setDarkenSky(boolean darkenSky) {
        // cannot be changed
    }

    @Override
    public boolean isDragonBar() {
        return true; // TODO - true if dragon, false if other boss
    }

    @Override
    public void setDragonBar(boolean dragonBar) {
        // cannot be changed
    }

    @Override
    public DefaultBossBar clone() {
        return this;
    }

}

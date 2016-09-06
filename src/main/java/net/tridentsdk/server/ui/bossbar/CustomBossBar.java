/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.ui.bossbar.BossBarColor;
import net.tridentsdk.ui.bossbar.BossBarDivision;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomBossBar extends AbstractBossBar {

    private volatile ChatComponent title = ChatComponent.text("Boss Bar");
    private volatile float health;
    private volatile BossBarColor color;
    private volatile BossBarDivision division;
    private volatile boolean darkenSky;
    private volatile boolean dragonBar;

    @Override
    public void setTitle(ChatComponent title) {
        if (title != null && !this.title.equals(title)) {
            this.changedTitle = true;
            this.title = title;
        }
    }

    @Override
    public void setHealth(float health) {
        if (this.health != health) {
            this.changedHealth = true;
            this.health = health;
        }
    }

    @Override
    public void setColor(BossBarColor color) {
        if (color != null && this.color != color) {
            this.changedStyle = true;
            this.color = color;
        }
    }

    @Override
    public void setDivision(BossBarDivision division) {
        if (division != null && this.division != division) {
            this.changedStyle = true;
            this.division = division;
        }
    }

    @Override
    public void setDarkenSky(boolean darkenSky) {
        if (this.darkenSky != darkenSky) {
            this.darkenSky = true;
            this.darkenSky = darkenSky;
        }
    }

    @Override
    public void setDragonBar(boolean dragonBar) {
        if (this.dragonBar != dragonBar) {
            this.changedFlags = true;
            this.dragonBar = dragonBar;
        }
    }

    @Override
    public CustomBossBar clone() {
        return new CustomBossBar(ChatComponent.fromJson(title.asJson().getAsJsonObject()), health, color, division, darkenSky, dragonBar);
    }

}

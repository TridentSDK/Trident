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

import lombok.NoArgsConstructor;
import net.tridentsdk.server.util.BitUtils;
import net.tridentsdk.ui.bossbar.BossBarColor;
import net.tridentsdk.ui.bossbar.BossBarDivision;
import net.tridentsdk.ui.chat.ChatComponent;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A customizable boss bar that can be used by a client.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */
@ThreadSafe
@NoArgsConstructor
public class CustomBossBar extends AbstractBossBar {
    private final AtomicReference<ChatComponent> title =
            new AtomicReference<>(ChatComponent.text("Boss Bar"));
    private final AtomicInteger health = new AtomicInteger();
    private final AtomicReference<BossBarColor> color = new AtomicReference<>(BossBarColor.PINK);
    private final AtomicReference<BossBarDivision> division = new AtomicReference<>(BossBarDivision.NO_DIVISION);
    private final AtomicBoolean darkenSky = new AtomicBoolean();
    private final AtomicBoolean dragonBar = new AtomicBoolean();

    public CustomBossBar(ChatComponent chatComponent, int health, BossBarColor color, BossBarDivision division, boolean darkenSky, boolean dragonBar) {
        this.title.set(chatComponent);
        this.health.set(health);
        this.color.set(color);
        this.division.set(division);
        this.darkenSky.set(darkenSky);
        this.dragonBar.set(dragonBar);
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public ChatComponent getTitle() {
        return this.title.get();
    }

    @Override
    public void setTitle(ChatComponent title) {
        ChatComponent old;
        while (true) {
            old = this.title.get();
            if (title != null && !title.equals(old)) {
                if (this.title.compareAndSet(old, title)) {
                    this.casState(3);
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public float getHealth() {
        return Float.intBitsToFloat(this.health.get());
    }

    @Override
    public void setHealth(float health) {
        int old;
        int n = Float.floatToRawIntBits(health);

        while (true) {
            old = this.health.get();
            if (old != n) {
                if (this.health.compareAndSet(old, n)) {
                    this.casState(2);
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public BossBarColor getColor() {
        return this.color.get();
    }

    @Override
    public void setColor(BossBarColor color) {
        BossBarColor old;
        while (true) {
            old = this.color.get();
            if (color != null && !color.equals(old)) {
                if (this.color.compareAndSet(old, color)) {
                    this.casState(1);
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public BossBarDivision getDivision() {
        return this.division.get();
    }

    @Override
    public void setDivision(BossBarDivision division) {
        BossBarDivision old;
        while (true) {
            old = this.division.get();
            if (division != null && !division.equals(old)) {
                if (this.division.compareAndSet(old, division)) {
                    this.casState(1);
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public boolean isDarkenSky() {
        return this.darkenSky.get();
    }

    @Override
    public void setDarkenSky(boolean darkenSky) {
        boolean old;

        while (true) {
            old = this.darkenSky.get();
            if (old != darkenSky) {
                if (this.darkenSky.compareAndSet(old, darkenSky)) {
                    this.casState(4);
                    break;
                }
            } else {
                break;
            }
        }
    }

    @Override
    public boolean isDragonBar() {
        return this.dragonBar.get();
    }

    @Override
    public void setDragonBar(boolean dragonBar) {
        boolean old;

        while (true) {
            old = this.dragonBar.get();
            if (old != dragonBar) {
                if (this.dragonBar.compareAndSet(old, dragonBar)) {
                    this.casState(0);
                    break;
                }
            } else {
                break;
            }
        }
    }

    /**
     * Performs a compare and swap on the getState field which
     * indicates whether or not a particular field has
     * been changed yet.
     *
     * @param idx the index to cas (described in
     * {@link AbstractBossBar#b})
     */
    private void casState(int idx) {
        int b;
        while (true) {
            b = STATE.get(this);

            // Check if bit already set
            if ((b >>> idx & 1) == 1) {
                break;
            }

            if (AbstractBossBar.STATE.compareAndSet(this, b, BitUtils.setBit(b, 0, true))) {
                break;
            }
        }
    }

    @Override
    public CustomBossBar clone() {
        return new CustomBossBar(ChatComponent.fromJson(this.title.get().asJson().asObject()),
                this.health.get(),
                this.color.get(),
                this.division.get(),
                this.darkenSky.get(),
                this.dragonBar.get());
    }
}
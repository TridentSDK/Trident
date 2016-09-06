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

import lombok.Getter;
import net.tridentsdk.ui.bossbar.BossBar;
import net.tridentsdk.ui.bossbar.BossBarManager;

/**
 * @author TridentSDK
 * @since 0.5-alpha
 */
public class TridentBossBarManager implements BossBarManager {

    @Getter
    private static final TridentBossBarManager instance = new TridentBossBarManager();

    private TridentBossBarManager() {}

    @Override
    public boolean isDefault(BossBar bar) {
        return bar instanceof DefaultBossBar;
    }

    @Override
    public BossBar newBossBar() {
        return new CustomBossBar();
    }

}

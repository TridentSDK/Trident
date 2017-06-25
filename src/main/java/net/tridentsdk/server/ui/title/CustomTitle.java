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
package net.tridentsdk.server.ui.title;

import lombok.Getter;
import net.tridentsdk.ui.chat.ChatComponent;
import net.tridentsdk.ui.title.Title;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * The representation of a title that is sent to the player.
 */
@NotThreadSafe
@Getter
public class CustomTitle implements Title {
    private ChatComponent header;
    private ChatComponent subtitle;
    private int fadeIn = Title.DEFAULT_FADE_IN;
    private int stay = Title.DEFAULT_STAY;
    private int fadeOut = Title.DEFAULT_FADE_OUT;

    @Override
    public Title setHeader(ChatComponent title) {
        this.header = title;
        return this;
    }

    @Override
    public Title setSubtitle(ChatComponent subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    @Override
    public Title setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    @Override
    public Title setStay(int stay) {
        this.stay = stay;
        return this;
    }

    @Override
    public Title setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    @Override
    public boolean isDefaultFadeTimes() {
        return this.fadeIn == Title.DEFAULT_FADE_IN &&
                this.stay == Title.DEFAULT_STAY &&
                this.fadeOut == Title.DEFAULT_FADE_OUT;
    }
}
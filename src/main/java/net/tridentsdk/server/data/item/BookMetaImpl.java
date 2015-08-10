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
package net.tridentsdk.server.data.item;

import net.tridentsdk.meta.item.BookMeta;
import net.tridentsdk.meta.nbt.NBTField;
import net.tridentsdk.meta.nbt.TagType;

import java.util.List;

public class BookMetaImpl extends ItemMetaImpl implements BookMeta {
    @NBTField(name = "generation", type = TagType.INT)
    protected int copyTier;
    @NBTField(name = "author", type = TagType.STRING)
    protected String author;
    @NBTField(name = "title", type = TagType.STRING)
    protected String title;
    @NBTField(name = "pages", type = TagType.LIST)
    protected List<String> pages;

    @Override
    public int copyTier() {
        return copyTier;
    }

    @Override
    public void setCopyTier(int copyTier) {
        this.copyTier = copyTier;
    }

    @Override
    public String author() {
        return author;
    }

    @Override
    public void setAuthor(String name) {
        this.author = name;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public List<String> pages() {
        return pages;
    }

    @Override
    public void setPages(List<String> pages) {
        this.pages = pages;
    }
}

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
package net.tridentsdk.server.data;

import com.google.common.collect.Lists;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.block.BlockMeta;
import net.tridentsdk.meta.component.*;
import net.tridentsdk.server.data.block.*;
import net.tridentsdk.util.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Provides metadata implementations to the API
 *
 * @author The TridentSDK Team
 */
public class MetaProviderFactory implements MetaProvider {
    private final MetaCollection collection = MetaFactory.newCollection();
    private final Map<Substance, MetaCompiler> metaMap = new ConcurrentHashMap<>();

    public MetaProviderFactory() {
        register(new ColorMetaImpl());
        register(new SignMetaImpl());
        register(new FurnaceMetaImpl());
        register(new ChestMetaImpl());
        register(new DirectionMetaImpl());
    }

    public boolean hasData(Substance substance) {
        return metaMap.containsKey(substance);
    }

    @Override
    public boolean decode(Block block, Value<Substance> substance, byte[] data, Value<Byte> result) {
        MetaCompiler compiler = metaMap.get(substance.get());
        if (compiler != null) {
            byte metaByte = 0;
            for (Meta<?> meta : compiler.compileBlock(block, data)) {
                if (meta instanceof IllegalMeta) {
                    block.clearMeta();
                    return false;
                }

                block.applyMeta((BlockMeta) meta);
                metaByte |= meta.encodeMeta()[0];
            }

            result.set(metaByte);
            substance.set(block.substance());
        } else {
            // If no data was found for an item, the item is not placeable
            if (!substance.get().isBlock()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public <S, T extends Meta<S>> T provide(Class<T> cls) {
        return (T) collection.get(cls).make();
    }

    @Override
    public void register(Meta meta) {
        Substance[] substances = meta.applyTo(collection);
        for (Substance substance : substances) {
            MetaCompiler metaCompiler = metaMap.get(substance);
            if (metaCompiler == null) {
                metaCompiler = new MetaCompiler();
            }

            metaCompiler.add(meta);
            metaMap.put(substance, metaCompiler);
        }
    }

    private class MetaCompiler {
        private final List<Meta> metas = new CopyOnWriteArrayList<>();

        public void add(Meta meta) {
            metas.add(meta);
            Collections.sort(metas, (o1, o2) -> {
                if (o1 instanceof BlockMeta && o2 instanceof BlockMeta) {
                    BlockMeta<?> b1 = ((BlockMeta) o1);
                    BlockMeta<?> b2 = ((BlockMeta) o2);

                    for (Class c : b1.dependencies()) {
                        if (c.isInstance(b2)) {
                            return 1;
                        }
                    }

                    for (Class c : b2.dependencies()) {
                        if (c.isInstance(b1)) {
                            return -1;
                        }
                    }
                }

                return 0;
            });
        }

        public Collection<Meta> compileBlock(Block block, byte[] data) {
            List<Meta> compiled = Lists.newArrayList();
            for (Meta meta : metas) {
                compiled.add(meta.decodeMeta(block, data));
            }

            return compiled;
        }
    }
}

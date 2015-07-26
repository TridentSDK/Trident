package net.tridentsdk.server.data;

import com.google.common.collect.Lists;
import net.tridentsdk.base.Block;
import net.tridentsdk.base.Substance;
import net.tridentsdk.meta.block.BlockMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.meta.component.MetaCollection;
import net.tridentsdk.meta.component.MetaFactory;
import net.tridentsdk.meta.component.MetaProvider;
import net.tridentsdk.server.data.block.WoolMetaImpl;

import java.util.Collection;
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
        register(new WoolMetaImpl());
    }

    public boolean hasData(Substance substance) {
        return metaMap.containsKey(substance);
    }

    public void populate(Block block) {
        Substance substance = block.substance();
        MetaCompiler compiler = metaMap.get(substance);
        if (compiler != null) {
            for (BlockMeta<Block> meta : compiler.compileBlock(block)) {
                block.commit(meta, false);
            }
        }
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
        }

        public Collection<BlockMeta> compileBlock(Block block) {
            List<BlockMeta> compiled = Lists.newArrayList();
            for (Meta meta : metas) {
                compiled.add(((BlockMeta) meta.decode(block, new byte[]{block.meta()})));
            }

            return compiled;
        }
    }
}

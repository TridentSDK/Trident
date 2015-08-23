package net.tridentsdk.server.bench;

import com.google.common.collect.Sets;
import net.tridentsdk.base.SubstanceColor;
import net.tridentsdk.meta.block.ColorMeta;
import net.tridentsdk.meta.component.Meta;
import net.tridentsdk.server.data.block.ColorMetaImpl;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static net.tridentsdk.server.bench.Benchmarks.*;

/*
http://bit.ly/1Pl7Mwi
 */
@State(Scope.Benchmark)
public class MetaTest {
    @Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
    private int cpuTokens;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(".*" + MetaTest.class.getSimpleName() + ".*")
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(1))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(1))
                .forks(1)
                .threads(4)
                .verbosity(VerboseMode.SILENT)
                .build();

        chart(parse(new Runner(opt).run()), "Meta storage Map vs Set - 4 threads");
    }

    public static class MapMeta {
        private final Map<Class, Meta<?>> metaMap = new ConcurrentHashMap<>();
        public <T extends Meta<?>> T getMeta(Class cls) {
            Meta meta = metaMap.get(cls);
            if (meta == null) throw new RuntimeException();
            return (T) meta;
        }

        public void applyMeta(Meta... metas) {
            for (Meta meta : metas) {
                Class cls = meta.getClass();
                if (cls.getSimpleName().contains("Impl")) {
                    cls = cls.getInterfaces()[0];
                }

                metaMap.put(cls, meta);
            }
        }
    }

    public static class SetMeta {
        private final Set<Meta> metas = Sets.newConcurrentHashSet();
        public <T extends Meta<?>> T getMeta(Class cls) {
            for (Meta meta : metas) {
                if (cls.isInstance(meta)) {
                    return (T) meta;
                }
            }

            throw new RuntimeException();
        }

        public void applyMeta(Meta... metas) {
            Collections.addAll(this.metas, metas);
        }
    }

    private final MapMeta map = new MapMeta();
    private final SetMeta array = new SetMeta();
    private final Random random = new Random();
    private volatile SubstanceColor value;

    @Setup
    public void setup() {
        map.applyMeta(new ColorMetaImpl());
        array.applyMeta(new ColorMetaImpl());
    }

    @Setup(Level.Iteration)
    public void setupIt() {
        value = SubstanceColor.values()[random.nextInt(SubstanceColor.values().length)];
    }

    @Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public SubstanceColor map() {
        Blackhole.consumeCPU(cpuTokens);

        ColorMeta meta = map.getMeta(ColorMeta.class);
        meta.setColor(value);
        map.applyMeta(meta);
        return meta.color();
    }

    @Benchmark
    public SubstanceColor set() {
        Blackhole.consumeCPU(cpuTokens);

        ColorMeta meta = array.getMeta(ColorMeta.class);
        meta.setColor(value);
        array.applyMeta(meta);
        return meta.color();
    }
}

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

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
http://bit.ly/1Pl7Mwi
 */
@State(Scope.Benchmark)
public class MetaTest {
    //@Param({ "1", "2", "4", "8", "16", "32", "64", "128", "256", "512", "1024" })
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
                        //.threads(4)
                        //.verbosity(VerboseMode.SILENT)
                .build();

        new Runner(opt).run();
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

    public static class InheritMeta {
        private final ColorMeta meta = new ColorMetaImpl();
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

    //@Benchmark
    public void control() {
        Blackhole.consumeCPU(cpuTokens);
    }

    @Benchmark
    public SubstanceColor map() {
        //Blackhole.consumeCPU(cpuTokens);

        ColorMeta meta = map.getMeta(ColorMeta.class);
        meta.setColor(value);
        map.applyMeta(meta);
        return meta.color();
    }

    @Benchmark
    public SubstanceColor set() {
        //Blackhole.consumeCPU(cpuTokens);

        ColorMeta meta = array.getMeta(ColorMeta.class);
        meta.setColor(value);
        array.applyMeta(meta);
        return meta.color();
    }
}

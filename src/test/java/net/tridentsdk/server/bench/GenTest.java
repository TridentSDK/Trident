package net.tridentsdk.server.bench;

import net.tridentsdk.config.Config;
import net.tridentsdk.registry.Registered;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.service.TridentImpl;
import net.tridentsdk.server.world.TridentWorld;
import net.tridentsdk.server.world.TridentWorldLoader;
import net.tridentsdk.util.FastRandom;
import net.tridentsdk.util.TridentLogger;
import net.tridentsdk.world.Chunk;
import net.tridentsdk.world.ChunkLocation;
import net.tridentsdk.world.WorldLoader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class GenTest {
    static {
        TridentLogger.init(org.apache.log4j.Level.OFF);
        TridentImpl trident = new TridentImpl();
        Registered.setProvider(trident);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + GenTest.class.getSimpleName() + ".*")
                .mode(Mode.AverageTime)
                .measurementIterations(20)
                .measurementTime(TimeValue.milliseconds(200))
                .warmupIterations(20)
                .warmupTime(TimeValue.milliseconds(200))
                        //.threads(4)
                .forks(1)
                .timeUnit(TimeUnit.NANOSECONDS)
                .build();

        new Runner(opt).run();
    }

    public static void main0(String[] args) {
        GenTest test = new GenTest();
        test.setup0();
        while (true) {
            test.setup();
            try {
                test.gen();
            } catch (NullPointerException e) {
                test.teardown();
                continue;
            }
            test.teardown();
        }
    }

    private volatile TridentWorld world;
    private volatile ChunkLocation location;

    @Setup
    public void setup0() {
        if (world != null) return;
        TridentWorldLoader.loadAll();
        world = (TridentWorld) WorldLoader.newLoader().createWorld("world");
        TridentServer.createServer(new Config("server.json"));
    }

    @Setup(Level.Invocation)
    public void setup() {
        location = ChunkLocation.create((int) FastRandom.random(50), (int) FastRandom.random(50));
    }

    @TearDown(Level.Invocation)
    public void teardown() {
        while (!world.loadedChunks.tryRemove(location)) ;
    }

    @Param({"1", "2", "4", "8", "16", "32", "64"})
    private int cpuTokens;

    @Benchmark
    public Chunk gen() {
        Blackhole.consumeCPU(cpuTokens);
        return world.chunkAt(location, true);
    }
}

package net.tridentsdk.server.unit;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;
import net.tridentsdk.config.JsonConfig;
import net.tridentsdk.factory.CollectFactory;
import net.tridentsdk.factory.ConfigFactory;
import net.tridentsdk.factory.Factories;
import net.tridentsdk.server.TridentScheduler;
import net.tridentsdk.server.threads.ThreadsManager;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

public class LatchInitTest {
    @Test
    public void doTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotNull(Factories.collect().createMap());
            }
        }).start();

        Factories.init(new ConfigFactory() {
            @Override
            public JsonConfig serverConfig() {
                return new JsonConfig(Paths.get("/topkek"));
            }
        });
        Factories.init(new CollectFactory() {
            @Override
            public <K, V> ConcurrentMap<K, V> createMap() {
                return new ConcurrentHashMapV8<>();
            }
        });
        Factories.init(new TridentScheduler());
        Factories.init(new ThreadsManager());
    }
}

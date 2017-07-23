package net.tridentsdk.server.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import net.tridentsdk.plugin.Plugin;
import net.tridentsdk.plugin.PluginDesc;

/**
 * @author Nick Robson
 */
public class TridentDummyCommandPlugin extends Plugin {

    private static final AtomicInteger used0 = new AtomicInteger(), used1 = new AtomicInteger();
    public static final Plugin TRIDENT_INST = new TridentDummyCommandPlugin("trident", "Trident");
    public static final Plugin MINECRAFT_INST = new TridentDummyCommandPlugin("minecraft", "Minecraft");
    private TridentDummyCommandPlugin(String id, String display) {
        if (!Arrays.asList("trident", "minecraft").contains(id))
            throw new IllegalArgumentException("only trident and minecraft are allowed ids");
        if (!used0.compareAndSet(0, 1) && !used1.compareAndSet(0, 1)) {
            throw new IllegalArgumentException("invalid registration");
        }
        PluginDesc pluginDesc = new PluginDesc() {
            @Override public boolean equals(Object obj) { return obj == this; }
            @Override public int hashCode() { return id().hashCode(); }
            @Override public String toString() { return id(); }
            @Override public Class<? extends Annotation> annotationType() { return PluginDesc.class; }
            @Override public String id() { return id; }
            @Override public String name() { return display; }
            @Override public String version() { return "1.0.0"; }
            @Override public String author() { return "TridentSDK Team"; }
            @Override public String[] depends() { return new String[0]; }
        };
        try {
            Field f = Plugin.class.getDeclaredField("description");
            f.setAccessible(true);
            f.set(this, pluginDesc);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

}

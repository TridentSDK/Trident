package net.tridentsdk.server.event;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import net.tridentsdk.Trident;
import net.tridentsdk.docs.InternalUseOnly;
import net.tridentsdk.event.*;
import net.tridentsdk.plugin.Plugin;
import net.tridentsdk.registry.Registered;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

/**
 * The server's event handler, should only be created once, and only once by the server only
 * <p>
 * <p>To access this handler, use this code:
 * <pre><code>
 *     Events handler = Registered.events();
 * </code></pre></p>
 *
 * @author The TridentSDK Team
 * @since 0.3-alpha-DP
 */
public class EventHandler extends ForwardingCollection<EventNotifier> implements Events {
    private static final Comparator<ReflectNotifier> COMPARATOR = new ReflectNotifier(null, null, 0, null, null, null);
    private static final Function<Class<?>, Set<ReflectNotifier>> CREATE_QUEUE = (k) ->
            new ConcurrentSkipListSet<>(COMPARATOR);

    private final ConcurrentMap<Class<? extends Event>, Set<ReflectNotifier>> callers = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, MethodAccess> accessors = new ConcurrentHashMap<>();

    private EventHandler() {
        if (!Trident.isTrident()) {
            throw new RuntimeException(new IllegalAccessException("EventManager must be initiated by TridentSDK!"));
        }
    }

    /**
     * Creates a new event handler, should only be used internally
     * <p>
     * <p>To access this handler, use this code:
     * <pre><code>
     *     Events handler = Registered.events();
     * </code></pre></p>
     *
     * @return the new event handler
     */
    @InternalUseOnly
    public static Events create() {
        return new EventHandler();
    }

    private HashMultimap<Class<? extends Event>, ReflectNotifier> reflectorsFrom(Plugin plugin, Listener listener,
                                                                                 final Class<?> c) {
        MethodAccess access = accessors.computeIfAbsent(c, (k) -> MethodAccess.get(c));

        Method[] methods = c.getDeclaredMethods();

        HashMultimap<Class<? extends Event>, ReflectNotifier> map = HashMultimap.create(11, 11);
        for (int i = 0, n = methods.length; i < n; i++) {
            Method method = methods[i];
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1)
                continue;

            Class<?> type = parameterTypes[0];

            if (!Event.class.isAssignableFrom(type))
                continue;

            Class<? extends Event> eventClass = type.asSubclass(Event.class);
            ListenerOpts handler = method.getAnnotation(ListenerOpts.class);
            Importance importance = handler == null ? Importance.MEDIUM : handler.importance();

            ReflectNotifier registeredListener = new ReflectNotifier(access, plugin, access.getIndex(method.getName()),
                    listener, eventClass, importance);
            map.get(eventClass).add(registeredListener);
        }

        return map;
    }

    @Override
    public void fire(final Event event) {
        final Set<ReflectNotifier> listeners = callers.get(event.getClass());
        if (listeners == null) return;

        final CountDownLatch latch = new CountDownLatch(1);

        Registered.plugins().executor().execute(() -> {
            for (ReflectNotifier listener : listeners) {
                listener.handle(event);
            }

            latch.countDown();
        });

        // Setting of event state happens-before counting down
        // therefore event state need not be volatile
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    @InternalUseOnly
    public void registerListener(Plugin plugin, Listener listener) {
        final Class<?> c = listener.getClass();
        HashMultimap<Class<? extends Event>, ReflectNotifier> reflectors = reflectorsFrom(plugin, listener, c);

        for (Class<? extends Event> eventClass : reflectors.keySet()) {
            Set<ReflectNotifier> eventCallers = callers.computeIfAbsent(eventClass, CREATE_QUEUE);
            eventCallers.addAll(reflectors.get(eventClass));
        }
    }

    @Override
    public void unregister(Class<? extends Listener> cls) {
        for (Map.Entry<Class<? extends Event>, Set<ReflectNotifier>> entry : this.callers.entrySet()) {
            for (Iterator<ReflectNotifier> iterator = entry.getValue().iterator(); iterator.hasNext(); ) {
                ReflectNotifier it = iterator.next();
                if (it.listener().getClass().equals(cls)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    protected Collection<EventNotifier> delegate() {
        List<EventNotifier> reflectors = Lists.newArrayList();
        callers.values().forEach(q -> reflectors.addAll(Collections2.transform(q, e -> (EventNotifier) e)));

        return reflectors;
    }
}

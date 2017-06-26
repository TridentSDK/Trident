package net.tridentsdk.server.config;

import lombok.Getter;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.player.TridentPlayer;
import net.tridentsdk.util.Misc;

import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * List of server operators
 */
@ThreadSafe
public class OpsList extends TridentConfig {
    /**
     * Path to the ops file
     */
    public static final Path PATH = Misc.HOME_PATH.resolve("ops.json");
    /**
     * The key to the ops collection in the config file
     */
    private static final String OPS_KEY = "ops";

    /**
     * The list of server ops
     */
    @Getter
    private final Set<UUID> ops = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Ops list initializer method
     */
    private OpsList() {
        super(PATH);
    }

    /**
     * Initializes the ops list to the config file contents.
     *
     * @return the new ops list
     */
    public static OpsList init() throws IOException {
        OpsList list = new OpsList();
        if (list.hasKey(OPS_KEY)) {
            list.getCollection("ops", list.getOps());
        } else {
            list.set("ops", list.getOps());
            list.save();
        }

        return list;
    }

    /**
     * Adds the given UUID as a server operator.
     *
     * @param uuid the UUID to give operator status
     */
    public void addOp(UUID uuid) {
        this.ops.add(uuid);
        this.set("ops", this.ops);
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TridentServer.getInstance().getLogger().log(TridentPlayer.getPlayers().get(uuid).getName() +
                " [" + uuid + "] has been opped");
    }

    /**
     * Removes the given UUID from the operators list.
     *
     * @param uuid the UUID to deop
     */
    public void removeOp(UUID uuid) {
        this.ops.remove(uuid);
        this.set("ops", this.ops);
        try {
            this.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TridentServer.getInstance().getLogger().log(TridentPlayer.getPlayers().get(uuid).getName() +
                " [" + uuid + "] has been deopped");
    }
}
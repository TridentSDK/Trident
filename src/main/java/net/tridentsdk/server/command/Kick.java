package net.tridentsdk.server.command;

import net.tridentsdk.chat.ChatColor;
import net.tridentsdk.chat.ChatComponent;
import net.tridentsdk.command.*;
import net.tridentsdk.server.TridentServer;
import net.tridentsdk.server.player.TridentPlayer;

import java.util.Optional;

/**
 * Kick command for players.
 */
public class Kick implements CmdListener {
    @Cmd(name = "kick", help = "/kick <player> [reason]", desc = "Kicks a player from the server")
    @Constrain(value = MinArgsConstraint.class, type = ConstraintType.INT, integer = 1)
    public void kick(String label, CmdSource source, String[] args) {
        String player = args[0];
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(' ');
        }
        String reason = builder.toString();
        reason = reason.isEmpty() ? "Kicked by administrator" : reason;

        Optional<TridentPlayer> p = TridentPlayer.getPlayers().values().
                stream().
                filter(pl -> pl.getName().equals(player)).
                findFirst();

        if (!p.isPresent()) {
            source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).
                    setText("No player by the name \"" + player + "\" is online"));
        } else {
            p.get().kick(ChatComponent.text(reason));
            TridentServer.getInstance().getLogger().log("Kicked player " + player + " for: " + reason);
        }
    }
}
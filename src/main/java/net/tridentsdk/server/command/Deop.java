package net.tridentsdk.server.command;

import net.tridentsdk.command.*;
import net.tridentsdk.entity.living.Player;
import net.tridentsdk.ui.chat.ChatColor;
import net.tridentsdk.ui.chat.ChatComponent;

import java.util.Map;

public class Deop implements CmdListener {
    @Cmd(name = "deop", help = "/deo <player>", desc = "Sets the player to a non-operator")
    @Constrain(value = MinArgsConstraint.class, type = ConstraintType.INT, integer = 1)
    @Constrain(value = MaxArgsConstraint.class, type = ConstraintType.INT, integer = 1)
    @Constrain(value = PermsConstraint.class, type = ConstraintType.STRING, str = "minecraft.deop")
    @Constrain(value = SourceConstraint.class, type = ConstraintType.SOURCE,
            src = {CmdSourceType.PLAYER, CmdSourceType.CONSOLE})
    public void op(String label, CmdSource source, String[] args) {
        String player = args[0];
        Player p = Player.byName(player);

        if (p == null) {
            Map<String, Player> map = Player.search(player);
            if (map.size() == 1) {
                p = map.values().stream().findFirst().orElseThrow(RuntimeException::new);
            } else {
                source.sendMessage(ChatComponent.create().setColor(ChatColor.RED).
                        setText("No player by the name \"" + player + "\" is online"));
                return;
            }
        }

        p.setOp(false);
    }
}

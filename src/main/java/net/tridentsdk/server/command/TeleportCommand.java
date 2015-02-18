package net.tridentsdk.server.command;


import net.tridentsdk.entity.living.Player;
import net.tridentsdk.meta.ChatColor;
import net.tridentsdk.plugin.annotation.CommandDescription;
import net.tridentsdk.plugin.cmd.Command;

@CommandDescription(name = "teleport", permission = "trident.teleport", aliases = "tp")
public class TeleportCommand extends Command {
    @Override
    public void handlePlayer(Player player, String arguments, String alias) {
        String [] args = arguments.split(" ");
        if (args.length < 3) {
           /* player.sendMessage(ServerCommandRegistrar.SERVER_PREFIX + ServerCommandRegistrar.ERROR_PREFIX
                    + "Not enough arguments, check command. Usage: /tp <name> (x) (y) (z)");*/
            return;
        }
        
        try {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            player.teleport(x, y, z);
            //player.sendMessage(ServerCommandRegistrar.SERVER_PREFIX + "Teleporting...");
        } catch (NumberFormatException ex) {
            /*player.sendMessage(ServerCommandRegistrar.SERVER_PREFIX + ServerCommandRegistrar.ERROR_PREFIX
                    + "Feature not implemented yet.");*/
        }
    }
}

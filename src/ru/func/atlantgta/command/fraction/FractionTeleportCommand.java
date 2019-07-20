package ru.func.atlantgta.command.fraction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.fraction.Fraction;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.util.MessageUtil;

public class FractionTeleportCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public FractionTeleportCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (strings.length == 0) {
            if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;

            AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);

            if (atlantPlayer.getPost().getName().equals("NONE")) {
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("FractionNotFoundException"));
                return true;
            }

            int time = PLUGIN.getConfig().getInt("settings.baseCommand.time");
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("belowTeleportation")
                    .replace("%TIME%", time + "")
            );
            Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
                        ((Player) commandSender).teleport(atlantPlayer.getFraction().getBaseLocation());
                        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("afterTeleportation"));
                    },
                    20L * time
            );
        } else if (strings.length == 1) {
            if (!commandSender.isOp()) {
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("OplessException"));
                return true;
            }
            Fraction fraction = FractionUtil.getFractionByName(strings[0]);
            if (fraction == null) {
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("FractionNotFoundException"));
                return true;
            }
            Location newbaseLocation = ((Player) commandSender).getLocation();
            fraction.setBaseLocation(newbaseLocation);
            PLUGIN.getConfig().set("fractions." + fraction.getName() + ".base",
                    newbaseLocation.getX() + " " +
                    newbaseLocation.getY() + " " +
                    newbaseLocation.getZ()
            );
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("correctBaseChange"));
            PLUGIN.saveConfig();
        }
        return true;
    }
}

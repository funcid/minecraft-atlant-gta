package ru.func.atlantgta.command.fraction;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.util.MessageUtil;

import java.util.concurrent.atomic.AtomicReference;

public class FractionTeleportCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public FractionTeleportCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (strings.length == 0) {
            if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

            IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());

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
            AtomicReference<Boolean> exist = new AtomicReference<>(false);
            FractionUtil.getFractionByName(strings[0]).ifPresent(fraction -> {
                exist.set(true);
                Location newbaseLocation = ((Player) commandSender).getLocation();
                fraction.setBaseLocation(newbaseLocation);
                PLUGIN.getConfig().set("fractions." + fraction.getName() + ".base",
                        newbaseLocation.getX() + " " +
                                newbaseLocation.getY() + " " +
                                newbaseLocation.getZ()
                );
                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("correctBaseChange"));
                PLUGIN.saveConfig();
            });
            if (!exist.get())
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("FractionNotFoundException"));
        }
        return false;
    }
}

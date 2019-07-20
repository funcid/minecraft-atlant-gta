package ru.func.atlantgta.command.prison;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

public class PrisonCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public PrisonCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;

        if (!commandSender.isOp()) {
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
            return true;
        }
        if (strings.length == 1) {
            Location location = ((Player) commandSender).getLocation();

            if ("jail".equals(strings[0])) {
                PLUGIN.getConfig().set("settings.arrestCommand.prison",
                        location.getX() + " " +
                        location.getY() + " " +
                        location.getZ()
                );
                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("correctLocationChange"));
            } else if ("freedom".equals(strings[0])) {
                PLUGIN.getConfig().set("settings.arrestCommand.freedom",
                        location.getX() + " " +
                        location.getY() + " " +
                        location.getZ()
                );
                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("correctLocationChange"));
            } else return true;
            PLUGIN.saveConfig();
        }
        return true;
    }
}

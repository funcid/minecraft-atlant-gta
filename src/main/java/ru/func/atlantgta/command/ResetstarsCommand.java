package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;


public class ResetstarsCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public ResetstarsCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.isOp()) {
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
                return true;
            }
        }
        if (strings.length == 1) {
            if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]).getUniqueId())) {
                PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[0]).getUniqueId()).setStars(0);
                if (commandSender instanceof Player)
                    commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("resetStars"));
            }
        }
        return false;
    }
}

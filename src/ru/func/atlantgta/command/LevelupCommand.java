package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

public class LevelupCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public LevelupCommand(AtlantGTA plugin) {
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
            if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]))) {
                PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[0])).setLevel(PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[0])).getLevel() + 1);
                Bukkit.getPlayer(strings[0]).sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("levelUp"));
            }
        }
        return true;
    }
}

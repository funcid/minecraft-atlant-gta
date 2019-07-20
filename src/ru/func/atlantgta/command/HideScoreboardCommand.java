package ru.func.atlantgta.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.listener.ConnectionListener;

public class HideScoreboardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        ConnectionListener.getHideScoreboard().add(((Player) commandSender).getUniqueId());
        return true;
    }
}

package ru.func.atlantgta.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.listener.ConnectionListener;

public class HideScoreboardCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public HideScoreboardCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        ConnectionListener connectionListener = PLUGIN.getConnectionListener();
        if (connectionListener.getHideScoreboard().contains(((Player) commandSender).getUniqueId())) {
            connectionListener.getHideScoreboard().remove(((Player) commandSender).getUniqueId());
            connectionListener.enableScoreboard((Player) commandSender);
        } else
            connectionListener.getHideScoreboard().add(((Player) commandSender).getUniqueId());
        return true;
    }
}

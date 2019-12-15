package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;

public class GiveArmyTicketCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public GiveArmyTicketCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
        if (atlantPlayer.getPost().getRoots().contains("givearmyticketCommand")) {
            if (strings.length > 0)
                if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]).getUniqueId()))
                    PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[0]).getUniqueId()).setTicket(true);
            else return false;
        }
        return true;
    }
}

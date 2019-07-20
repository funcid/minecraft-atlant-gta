package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;

public class GiveArmyTicketCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public GiveArmyTicketCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;

        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);
        if (atlantPlayer.getPost().getRoots().contains("givearmyticketCommand"))
            if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0])))
                PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[0])).setTicket(true);
        return true;
    }
}

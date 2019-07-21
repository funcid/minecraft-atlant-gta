package ru.func.atlantgta.command.help;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HelpMeCommand implements CommandExecutor {

    private List<UUID> invites = new ArrayList<>();

    private final AtlantGTA PLUGIN;
    private final int PRICE;

    public HelpMeCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        PRICE = PLUGIN.getConfig().getInt("settings.helpMeCommand.price");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;

        if (strings.length >= 2) {
            Player player = Bukkit.getPlayer(strings[0]);
            if (PLUGIN.getOnlinePlayers().containsKey(player)) {
                if (invites.contains(player.getUniqueId())) {
                    if (PLUGIN.getOnlinePlayers().get(commandSender).getPost().getRoots().contains("helpmeCommand")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 1; i < strings.length; i++)
                            stringBuilder.append(strings[i]).append(" ");

                        player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("answer")
                                .replace("%NAME%", commandSender.getName())
                                .replace("%ANSWER%", stringBuilder.toString())
                        );

                        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("answered")
                                .replace("%MONEY%", PRICE + "")
                        );
                        PLUGIN.getEconomy().depositPlayer((Player) commandSender, PRICE);
                        invites.remove(player.getUniqueId());
                    }
                }
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < strings.length; i++)
                    stringBuilder.append(strings[i]).append(" ");
                for (Player p : Bukkit.getOnlinePlayers())
                    if (PLUGIN.getOnlinePlayers().get(p).getPost().getRoots().contains("helpmeCommand"))
                        p.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("helpMe")
                                .replace("%NAME%", commandSender.getName())
                                .replace("%QUESTION%", stringBuilder.toString())
                        );
                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("request"));
                invites.add(((Player) commandSender).getUniqueId());
            }
        }
        return true;
    }
}

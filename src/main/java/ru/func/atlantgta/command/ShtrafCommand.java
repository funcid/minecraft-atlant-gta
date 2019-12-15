package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class ShtrafCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final int MIN;
    private final int MAX;

    public ShtrafCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        MIN = PLUGIN.getConfig().getInt("settings.shtrafCommand.min");
        MAX = PLUGIN.getConfig().getInt("settings.shtrafCommand.max");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());

        if (strings.length == 2) {
            if (atlantPlayer.getPost().getRoots().contains("shtrafCommand")) {
                Player player = Bukkit.getPlayer(strings[0]);
                if (PLUGIN.getOnlinePlayers().containsKey(player.getUniqueId())) {
                    int shtraf;
                    try {
                        shtraf = Integer.parseInt(strings[1]);
                    } catch (Exception e) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                        return true;
                    }
                    if (shtraf > MIN && shtraf < MAX) {
                        if (PLUGIN.getEconomy().getBalance(player) > shtraf) {
                            PLUGIN.getEconomy().withdrawPlayer(player, shtraf);
                            PLUGIN.getEconomy().depositPlayer((Player) commandSender, shtraf);
                            player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("shtraf")
                                    .replace("%MONEY%", shtraf + "")
                            );
                            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("shtrafed")
                                    .replace("%NAME%", player.getName())
                                    .replace("%MONEY%", shtraf + "")
                            );
                        } else
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("PlayerHaventMoneyException"));
                    } else
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("IllegalNumberException"));
                } else
                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UnrealPlayerException"));
            } else
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
        }
        return true;
    }
}

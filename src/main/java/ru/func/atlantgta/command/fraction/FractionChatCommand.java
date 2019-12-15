package ru.func.atlantgta.command.fraction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class FractionChatCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public FractionChatCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return true;

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());

        if (atlantPlayer.getPost().getName().equals("NONE")) {
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("AlreadyHaveFractionException"));
            return true;
        }

        String targetMessage = String.format(ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("fractionchat")), atlantPlayer.getPost().getSubName(), commandSender.getName(), String.join(" ", strings));

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> PLUGIN.getOnlinePlayers().get(p.getUniqueId()).getFraction().equals(atlantPlayer.getFraction()))
                .forEach(p -> p.sendMessage(targetMessage));
        commandSender.sendMessage("Сообщение отправлено");
        return true;
    }
}

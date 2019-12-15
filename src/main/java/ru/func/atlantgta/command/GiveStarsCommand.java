package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

public class GiveStarsCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public GiveStarsCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (!commandSender.isOp() || !PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId()) || !PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId()).getPost().getRoots().contains("givestarsCommand")) {
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
                return true;
            }
        }
        if (strings.length == 2) {
            if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]).getUniqueId())) {
                try {
                    Player player = Bukkit.getPlayer(strings[0]);
                    int stars = Integer.parseInt(strings[1]);
                    stars = stars < 0 ? 1 : stars > 5 ? 5 : stars;
                    PLUGIN.getOnlinePlayers().get(player.getUniqueId()).setStars(stars);
                    Bukkit.broadcastMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("globalSearch")
                        .replace("%NAME%", player.getName())
                        .replace("%STARS%", stars + "")
                    );
                } catch (Exception ignored) { }
            }
        }
        return true;
    }
}

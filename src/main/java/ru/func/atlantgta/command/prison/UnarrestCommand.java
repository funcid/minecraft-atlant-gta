package ru.func.atlantgta.command.prison;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Map;
import java.util.UUID;

public class UnarrestCommand implements CommandExecutor {

    private Map<UUID, UUID> invites = Maps.newHashMap();

    private final AtlantGTA PLUGIN;
    private int pricePerStar;

    public UnarrestCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        pricePerStar = PLUGIN.getConfig().getInt("settings.arrestCommand.pricePerStar");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 2) {
            if ("accept".equals(strings[0])) {
                Player player = Bukkit.getPlayer(strings[1]);
                if (invites.containsKey(player.getUniqueId())) {
                    int price = pricePerStar * PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId()).getStars();
                    PrisonUtil.getPrisoners().replace(((Player) commandSender).getUniqueId(), 1);

                    commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("unarrest"));
                    player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("unarrested")
                            .replace("%NAME%", commandSender.getName())
                            .replace("%MONEY%", price + "")
                    );

                    PLUGIN.getEconomy().withdrawPlayer((Player) commandSender, price);
                    PLUGIN.getEconomy().depositPlayer(player, price);
                }
            }
        } else if (strings.length == 1) {
            Player player = Bukkit.getPlayer(strings[0]);
            if (!invites.containsKey(((Player) commandSender).getUniqueId())) {
                if (PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId()).getPost().getRoots().contains("unarrestCommand")) {
                    if (PrisonUtil.getPrisoners().containsKey(player.getUniqueId())) {
                        player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("freedomInvite")
                                .replace("%NAME%", commandSender.getName())
                                .replace("%MONEY%", PLUGIN.getOnlinePlayers().get(player.getUniqueId()).getStars() * pricePerStar + "")
                        );
                        invites.put(((Player) commandSender).getUniqueId(), player.getUniqueId());
                        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("request"));
                        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> invites.remove(((Player) commandSender).getUniqueId()), 45 * 20);
                    }
                }
            }
        }
        return true;
    }
}

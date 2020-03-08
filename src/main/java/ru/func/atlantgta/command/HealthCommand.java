package ru.func.atlantgta.command;

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

public class HealthCommand implements CommandExecutor {

    private Map<UUID, String> invites = Maps.newHashMap();

    private final AtlantGTA PLUGIN;
    private final int HP;

    public HealthCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        HP = PLUGIN.getConfig().getInt("settings.healthCommand.hp");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 2) {
            if ("accept".equals(strings[0])) {
                Player player = Bukkit.getPlayer(strings[1]);
                if (invites.containsKey(player.getUniqueId())) {
                    int price = Integer.parseInt(invites.get(player.getUniqueId()).split(":")[1]);
                    if (invites.get(player.getUniqueId()).split(":")[0].equals(((Player) commandSender).getUniqueId().toString())) {
                        if (PLUGIN.getEconomy().getBalance((Player) commandSender) > price) {

                            ((Player) commandSender).setHealth(((Player) commandSender).getHealth() + HP > ((Player) commandSender).getMaxHealth() ? ((Player) commandSender).getMaxHealth() : ((Player) commandSender).getHealth() + HP);

                            player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("healed"));
                            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("heal")
                                    .replace("%VALUE%", HP + "")
                                    .replace("%MONEY%", price + "")
                            );

                            PLUGIN.getEconomy().withdrawPlayer((Player) commandSender, price);
                            PLUGIN.getEconomy().depositPlayer(player, price);

                            invites.remove(player.getUniqueId());
                        } else
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMoneyException"));
                    }
                }
            } else if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]).getUniqueId())) {
                Player player = Bukkit.getPlayer(strings[0]);
                if (!invites.containsKey(((Player) commandSender).getUniqueId())) {
                    if (PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId()).getPost().getRoots().contains("healthCommand")) {
                        int price;
                        try {
                            price = Integer.parseInt(strings[1]);
                        } catch (Exception e) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                            return true;
                        }
                        if (price < 0) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("IllegalMoneyException"));
                            return true;
                        }
                        invites.put(((Player) commandSender).getUniqueId(), player.getUniqueId() + ":" + price);
                        player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("hp")
                            .replace("%NAME%", commandSender.getName())
                            .replace("%MONEY%", price + "")
                        );
                        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("request"));
                        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> invites.remove(((Player) commandSender).getUniqueId()), 40 * 20);
                    }
                }
            }
        }
        return false;
    }
}

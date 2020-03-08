package ru.func.atlantgta.command;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Map;
import java.util.UUID;

public class TradeCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    private Map<UUID, UUID> invites = Maps.newHashMap();
    private Map<UUID, ItemStack> container = Maps.newHashMap();
    private Map<UUID, Integer> value = Maps.newHashMap();

    public TradeCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 2) {
            if ("accept".equals(strings[0])) {
                Player player = Bukkit.getPlayer(strings[1]);
                if (invites.containsKey(((Player) commandSender).getUniqueId())) {
                    int price = value.get(((Player) commandSender).getUniqueId());
                    if (PLUGIN.getEconomy().getBalance((Player) commandSender) > price) {
                        player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("tradeAccepted"));

                        PLUGIN.getEconomy().depositPlayer(player, price);
                        PLUGIN.getEconomy().withdrawPlayer((Player) commandSender, price);

                        ((Player) commandSender).getInventory().addItem(container.get(player.getUniqueId()));

                        invites.remove(player.getUniqueId());
                        container.remove(player.getUniqueId());
                        value.remove(((Player) commandSender).getUniqueId());
                    }
                }
            } else if (PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]).getUniqueId())) {
                Player player = Bukkit.getPlayer(strings[0]);
                UUID uuid = ((Player) commandSender).getUniqueId();
                try {
                    if (!invites.containsKey(((Player) commandSender).getUniqueId())) {
                        ItemStack itemStack = ((Player) commandSender).getInventory().getItemInMainHand();
                        if (itemStack != null) {
                            if (!itemStack.getType().equals(Material.AIR)) {
                                if (strings[1].contains("-")) {
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("IllegalMoneyException"));
                                    return true;
                                }
                                invites.put(uuid, player.getUniqueId());
                                container.put(uuid, ((Player) commandSender).getInventory().getItemInMainHand());
                                value.put(player.getUniqueId(), Integer.parseInt(strings[1]));

                                ((Player) commandSender).getInventory().setItemInMainHand(null);

                                player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("tradeInvite")
                                        .replace("%NAME%", commandSender.getName())
                                        .replace("%ITEM%", itemStack.getType().toString())
                                        .replace("%MONEY%", strings[1])
                                );

                                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("request"));

                                Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
                                    if (invites.containsKey(uuid)) {
                                        ((Player) commandSender).getInventory().addItem(itemStack);

                                        invites.remove(uuid);
                                        container.remove(uuid);
                                        value.remove(player.getUniqueId());
                                    }
                                }, 25 * 20L);
                            }
                        }
                    } else
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("MakeSecondInviteException"));
                } catch (Exception e) {
                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                }
            }
        }
        return false;
    }
}

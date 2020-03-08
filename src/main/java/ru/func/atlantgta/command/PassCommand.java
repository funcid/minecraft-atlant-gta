package ru.func.atlantgta.command;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class PassCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    private Map<UUID, UUID> invites = Maps.newHashMap();

    private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
    private ItemStack paper = new ItemStack(Material.PAPER);

    public PassCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 0) {
            showPassword((Player) commandSender, (Player) commandSender);
        } else if (strings.length == 1) {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player == null || !PLUGIN.getOnlinePlayers().containsKey(player.getUniqueId())) {
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UnrealPlayerException"));
                return true;
            }
            invites.put(Bukkit.getPlayer(strings[0]).getUniqueId(), ((Player) commandSender).getUniqueId());
            Bukkit.getPlayer(strings[0]).sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("viewPassword")
                    .replace("%NAME%", commandSender.getName())
            );
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("request"));
            Bukkit.getScheduler().runTaskLater(PLUGIN, () -> invites.remove(Bukkit.getPlayer(strings[0]).getUniqueId()), 45 * 20);

        } else if (strings.length == 2) {
            if ("accept".equals(strings[0])) {
                Player sender = Bukkit.getPlayer(strings[1]);
                if (PLUGIN.getOnlinePlayers().containsKey(sender.getUniqueId())) {
                    if (invites.containsKey(((Player) commandSender).getUniqueId()) && invites.get(((Player) commandSender).getUniqueId()).equals(sender.getUniqueId())) {
                        if (((Player) commandSender).getLocation().distance(sender.getLocation()) < PLUGIN.getConfig().getInt("settings.passCommand.distance"))
                            showPassword((Player) commandSender, sender);
                        else
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("DistanceCommandException"));
                    }
                }
            }
        }
        return false;
    }

    private void showPassword(Player sender, Player toOpen) {

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(sender.getUniqueId());

        ItemMeta itemMeta = paper.getItemMeta();
        itemMeta.setDisplayName("§e§lПАСПОРТ");
        itemMeta.setLore(Arrays.asList(
                "",
                "§fИмя: §b§l" + sender.getName(),
                "§fУровень: §b§l" + atlantPlayer.getLevel(),
                "§fВозраст: §b§l" + atlantPlayer.getAge(),
                "",
                "§eБоеприпасов: " + atlantPlayer.getAmmunition(),
                "",
                "§fФракция: §b§l" + (atlantPlayer.getFraction() == null ? "Ошибка 404" : atlantPlayer.getFraction().getSubName()),
                "§fДолжность: §b§l" + (atlantPlayer.getPost() == null ? "Ошибка 404" : atlantPlayer.getPost().getSubName())

        ));
        paper.setItemMeta(itemMeta);

        Inventory inventory = Bukkit.createInventory(null, 27, "§e§lПАСПОРТ");

        for (int i = 0; i < 9; i++)
            inventory.setItem(i, glass);

        inventory.setItem(12, atlantPlayer.getStars() > 0 ? new ItemStack(Material.NETHER_STAR, atlantPlayer.getStars()) : new ItemStack(Material.BRICK));
        inventory.setItem(14, paper);

        for (int i = 18; i < 27; i++)
            inventory.setItem(i, glass);

        toOpen.openInventory(inventory);
    }
}

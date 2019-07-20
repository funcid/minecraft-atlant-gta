package ru.func.atlantgta.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Arrays;

public class GetEquipmentCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final int PRICE;
    private final ItemStack BATON = new ItemStack(Material.STICK);

    public GetEquipmentCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        PRICE = PLUGIN.getConfig().getInt("settings.getequipCommand.price");

        ItemMeta itemMeta = BATON.getItemMeta();
        itemMeta.setDisplayName(PLUGIN.getConfig().getString("settings.policeStick.name"));
        itemMeta.setLore(Arrays.asList(PLUGIN.getConfig().getString("settings.policeStick.description")));
        BATON.setItemMeta(itemMeta);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;
        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);
        if (atlantPlayer.getPost().getRoots().contains("getequipCommand")) {
            atlantPlayer.setAmmunition(atlantPlayer.getAmmunition() + PRICE);
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("getEquip")
                    .replace("%VALUE%", PRICE + "")
            );
            ((Player) commandSender).getInventory().addItem(BATON);
            ((Player) commandSender).performCommand("skin policeman");
        } else
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
        return true;
    }
}

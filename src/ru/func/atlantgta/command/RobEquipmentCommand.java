package ru.func.atlantgta.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class RobEquipmentCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final int PRICE;
    private final int STARS;

    public RobEquipmentCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        PRICE = PLUGIN.getConfig().getInt("settings.robequipCommand.price");
        STARS = PLUGIN.getConfig().getInt("settings.robequipCommand.stars");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;

        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);

        if (atlantPlayer.getPost().getRoots().contains("robequipCommand")) {

            atlantPlayer.setAmmunition(atlantPlayer.getAmmunition() + PRICE);
            atlantPlayer.setStars(atlantPlayer.getStars() > (5 - STARS) ? 5 : atlantPlayer.getStars() + STARS);
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("getEquip")
                    .replace("%VALUE%", PRICE + "")
            );
        } else
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
        return true;
    }
}

package ru.func.atlantgta.command;

import com.shampaggon.crackshot.CSUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class MakeGunCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final ConfigurationSection configurationSection;
    private final CSUtility csUtil = new CSUtility();

    public MakeGunCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        configurationSection = PLUGIN.getConfig().getConfigurationSection("guns");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 1) {
            if (configurationSection.contains(strings[0])) {
                IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
                int price = configurationSection.getInt(strings[0] + ".price");
                if (atlantPlayer.getAmmunition() >= price) {
                    atlantPlayer.setAmmunition(atlantPlayer.getAmmunition() - price);
                    csUtil.giveWeapon((Player) commandSender, strings[0], 1);
                    commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("boughtWeapon")
                            .replace("%WEAPON%", strings[0])
                            .replace("%MONEY%", price + "")
                    );
                } else
                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMoneyException"));
            } else
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
        }
        return false;
    }
}

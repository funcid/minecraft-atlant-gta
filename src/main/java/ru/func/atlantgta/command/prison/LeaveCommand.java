package ru.func.atlantgta.command.prison;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.util.MessageUtil;

public class LeaveCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private Location freedom;
    private PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);

    public LeaveCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        String[] coordinates = PLUGIN.getConfig().getString("settings.arrestCommand.freedom").split("\\s+");
        freedom = new Location(
                Bukkit.getWorld("world"),
                Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]),
                Double.parseDouble(coordinates[2])
        );
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        Player player = (Player) commandSender;
        if (PrisonUtil.getPrisoners().containsKey(player.getUniqueId())) {
            int freedomTime = PrisonUtil.getPrisoners().get(player.getUniqueId());
            int timeNow = player.getStatistic(Statistic.PLAY_ONE_TICK);
            if (freedomTime <= timeNow) {
                PLUGIN.getOnlinePlayers().get(player.getUniqueId()).setStars(0);
                player.addPotionEffect(blindness);
                player.teleport(freedom);
            } else
                player.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("TimeNotOutException")
                        .replace("%TIME%", (freedomTime - timeNow) / 1200 + "")
                );
        }
        return true;
    }
}

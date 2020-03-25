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
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class ArrestCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private Location prison;
    private int distance;
    private int timePerStar;
    private PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 60, 1);

    public ArrestCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        distance = PLUGIN.getConfig().getInt("settings.arrestCommand.distance");
        String[] coordinates = PLUGIN.getConfig().getString("settings.arrestCommand.prison").split("\\s+");
        prison = new Location(
                Bukkit.getWorld("world"),
                Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]),
                Double.parseDouble(coordinates[2])
        );
        timePerStar = PLUGIN.getConfig().getInt("settings.arrestCommand.timePerStar");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 1) {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player != null && PLUGIN.getOnlinePlayers().containsKey(player.getUniqueId())) {
                IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(player.getUniqueId());
                IPlayer user = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
                if (!user.getPost().getName().equals("NONE")) {
                    if (user.getPost().getRoots().contains("arrestCommand")) {
                        if (atlantPlayer.getStars() > 0) {
                            if (((Player) commandSender).getNearbyEntities(distance, distance, distance).contains(player)) {
                                PrisonUtil.getPrisoners().put(player.getUniqueId(),
                                        player.getStatistic(Statistic.PLAY_ONE_TICK) +
                                                atlantPlayer.getStars() *
                                                        timePerStar * 60 * 20
                                );
                                player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("arrested"));
                                player.teleport(prison);
                                player.addPotionEffect(blindness);

                                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("arrest"));
                            } else
                                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("PlayerNotNearException"));
                        } else
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("PlayerHaventStarsException"));
                    } else
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UseCommandException"));
                } else
                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("MayBePolicemanException"));
            } else
                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("UnrealPlayerException"));
        }
        return true;
    }
}

package ru.func.atlantgta.command;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class LevelCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final int DIVIDER;
    private final int HOURS;

    public LevelCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        DIVIDER = PLUGIN.getConfig().getInt("settings.levelCommand.divider");
        HOURS = PLUGIN.getConfig().getInt("settings.levelCommand.hours");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
        long levelTime = atlantPlayer.getLevel() * HOURS * 60;
        long timePlayed = ((CraftPlayer) commandSender).getStatistic(Statistic.PLAY_ONE_TICK)/1200;

        if (levelTime < timePlayed) {
            atlantPlayer.setLevel(atlantPlayer.getLevel() + 1);
            atlantPlayer.setAge(atlantPlayer.getAge() + 1);
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("levelUp"));
            return true;
        }
        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("HaventTimeException")
                .replace("%TIME%", (levelTime - timePlayed)*60/DIVIDER + "")
        );
        return true;
    }
}

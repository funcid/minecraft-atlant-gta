package ru.func.atlantgta.command;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SalaryCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final int DELAY;
    private Map<UUID, Integer> salaryDelays = Maps.newHashMap();

    public SalaryCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        DELAY = PLUGIN.getConfig().getInt("settings.salary.time");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return false;


        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);

        if (atlantPlayer.getPost().getSalary() != 0) {
            if (salaryDelays.containsKey(((Player) commandSender).getUniqueId())) {
                int gamePlayed = ((Player) commandSender).getStatistic(Statistic.PLAY_ONE_TICK) / 1200;
                if (gamePlayed > salaryDelays.get(((Player) commandSender).getUniqueId()))
                    giveSalary((Player) commandSender);
                else
                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("WaitDelayException")
                        .replace("%TIME%", (salaryDelays.get(((Player) commandSender).getUniqueId()) - gamePlayed) + "")
                    );
            } else {
                giveSalary((Player) commandSender);
            }
        }

        return true;
    }

    private void giveSalary(Player player) {
        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(player);

        int salary = atlantPlayer.getPost().getSalary();
        PLUGIN.getEconomy().depositPlayer(player, salary);
        salaryDelays.put(player.getUniqueId(),
                player.getStatistic(Statistic.PLAY_ONE_TICK) / 1200 + DELAY
        );
        player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("getSalary")
                .replace("%MONEY%", salary + "")
        );
    }
}

package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Random;

public class RobCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final Random random = new Random();

    public RobCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());

        if (!atlantPlayer.getPost().getRoots().contains("robCommand")) {
            System.out.println(atlantPlayer.getPost().getName() + " " + atlantPlayer.getPost().getRoots());
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("RobException"));
            return true;
        }
        if (strings.length != 1 || !PLUGIN.getOnlinePlayers().containsKey(Bukkit.getPlayer(strings[0]).getUniqueId())) {
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
            return true;
        }
        Player player = Bukkit.getPlayer(strings[0]);
        if (player.getLocation().distance(((Player) commandSender).getLocation()) > PLUGIN.getConfig().getInt("settings.robCommand.distance")) {
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NotNearPlayerException"));
            return true;
        }
        int min = PLUGIN.getConfig().getInt("settings.robCommand.min");
        int max = PLUGIN.getConfig().getInt("settings.robCommand.max");
        int rob = min + random.nextInt(max - min + 1);

        if (rob > PLUGIN.getEconomy().getBalance(player)) {
            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("playerHaventMoney"));
            return true;
        }

        PLUGIN.getEconomy().withdrawPlayer(player, rob);
        PLUGIN.getEconomy().depositPlayer((Player) commandSender, rob);

        player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("wereRobbed")
                .replace("%MONEY%", rob + "")
        );
        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("robbed")
                .replace("%MONEY%", rob + "")
        );
        IPlayer rober = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
        if (rober.getStars() < 5) rober.setStars(rober.getStars() + 1);
        return false;
    }
}

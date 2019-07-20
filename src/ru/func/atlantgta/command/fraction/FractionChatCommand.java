package ru.func.atlantgta.command.fraction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class FractionChatCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public FractionChatCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return true;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return true;

        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);

        if (atlantPlayer.getPost().getName().equals("NONE")) {
            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("AlreadyHaveFractionException"));
            return true;
        }

        StringBuffer stringBuffer = new StringBuffer(atlantPlayer.getFraction().getSubName() + " " + commandSender.getName() + " " + atlantPlayer.getPost().getName() + " >_ ");

        for (String string : strings) stringBuffer.append(string).append(" ");

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> PLUGIN.getOnlinePlayers().get(p).getFraction().equals(atlantPlayer.getFraction()))
                .forEach(p -> p.sendMessage(stringBuffer.toString()));
        return true;
    }
}

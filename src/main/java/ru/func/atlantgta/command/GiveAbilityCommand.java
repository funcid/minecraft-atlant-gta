package ru.func.atlantgta.command;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.util.MessageUtil;

@AllArgsConstructor
public class GiveAbilityCommand implements CommandExecutor {
    private final AtlantGTA PLUGIN;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 2) {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player != null) {
                IPlayer sender = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
                IPlayer adressat = PLUGIN.getOnlinePlayers().get(player.getUniqueId());
                switch (strings[1]) {
                    case "военный_билет":
                        if(sender.getPost().getRoots().contains("givearmyticketCommand") || commandSender.isOp()) {
                            adressat.setTicket(true);
                            player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("isTicket"));
                            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("givenTicket").replace("%NAME%", player.getName()));
                        }
                        break;
                    case "медицинская_карта":
                        if(sender.getPost().getRoots().contains("givemedcardCommand") || commandSender.isOp()) {
                            adressat.setCard(true);
                            player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("isCard"));
                            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("givenCard").replace("%NAME%", player.getName()));
                        }
                        break;
                }
                return true;
            }
        }
        return false;
    }
}

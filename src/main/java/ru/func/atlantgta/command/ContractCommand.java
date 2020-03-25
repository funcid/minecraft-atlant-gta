package ru.func.atlantgta.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.listener.ContractUtil;
import ru.func.atlantgta.util.MessageUtil;

public class ContractCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;
    private final int MIN_REWARD;

    public ContractCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
        MIN_REWARD = PLUGIN.getConfig().getInt("settings.contractCommand.minimalReward");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        if (strings.length == 2) {
            IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());
            if (atlantPlayer.getPost().getRoots().contains("contractCommand")) {
                Player entity = Bukkit.getPlayer(strings[0]);
                if (entity != null && PLUGIN.getOnlinePlayers().containsKey(entity.getUniqueId())) {
                    int price;
                    try {
                        price = Integer.parseInt(strings[1]);
                    } catch (NumberFormatException e) {
                        return true;
                    }
                    if (price < MIN_REWARD) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("MoreMoneyException"));
                        return true;
                    }
                    if (ContractUtil.getContracts().containsKey(entity.getUniqueId())) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("FractionNotFoundException"));
                        return true;
                    }
                    if (PLUGIN.getEconomy().getBalance((Player) commandSender) < price) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMoneyException"));
                        return true;
                    }
                    ContractUtil.getContracts().put(entity.getUniqueId(), price);
                    PLUGIN.getEconomy().withdrawPlayer((Player) commandSender, price);
                    commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("request"));
                    for (Player player : Bukkit.getOnlinePlayers())
                        if (PLUGIN.getOnlinePlayers().get(player.getUniqueId()).getPost().getRoots().contains("completeContract"))
                            player.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("contract")
                                    .replace("%NAME%", entity.getName())
                                    .replace("%MONEY%", price + "")
                            );
                    return true;
                }
            }
        }
        return false;
    }
}

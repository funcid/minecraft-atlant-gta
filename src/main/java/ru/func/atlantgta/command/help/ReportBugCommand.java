package ru.func.atlantgta.command.help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportBugCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) return false;

        commandSender.sendMessage("");
        commandSender.sendMessage("§e§lСообщить об ошибке: ");
        commandSender.sendMessage(" §l* Сайт разработчика - §d§lhttp://§5§lfuncid.ru");
        commandSender.sendMessage(" §l* Вк разработчика - §d§lhttps://§5§lvk.com/funcid");
        commandSender.sendMessage(">_ §oИсправим как можно быстро...");
        return true;
    }
}

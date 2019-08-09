package ru.func.atlantgta.command.fraction;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.fraction.Fraction;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.fraction.PostUtil;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Map;
import java.util.UUID;

public class FractionCommand implements CommandExecutor {

    private final AtlantGTA PLUGIN;

    public FractionCommand(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    private Map<UUID, UUID> invites = Maps.newHashMap();;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        if (!PLUGIN.getOnlinePlayers().containsKey(commandSender)) return true;

        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(commandSender);

        switch (strings.length) {
            case 1:
                switch (strings[0]) {
                    case "leave":
                        atlantPlayer.setFraction(FractionUtil.getNoneFraction());
                        atlantPlayer.setPost(PostUtil.getNonePost());
                        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("leaveFraction"));
                        break;
                    //Другие аргументы при одном аргументе
                }
                break;
            case 2:
                switch (strings[0]) {
                    case "invite":
                        if (!atlantPlayer.getPost().getName().equalsIgnoreCase("OWNER")) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoRootsException"));
                            return true;
                        }
                        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoPlayerFoundException"));
                            return true;
                        }
                        if (PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[1])).getLevel() < atlantPlayer.getFraction().getMinLevel()) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("LowLevelException"));
                            return true;
                        }
                        if (invites.containsKey(((Player) commandSender).getUniqueId())) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("MakeSecondInviteException"));
                            return true;
                        } else {
                            Player hasInvited = Bukkit.getPlayer(strings[1]);
                            invites.put(((Player) commandSender).getUniqueId(), hasInvited.getUniqueId());
                            hasInvited.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("fractionInvite")
                                    .replace("%NAME%", commandSender.getName())
                                    .replace("%FRACTION%", atlantPlayer.getFraction().getName())
                            );
                            Bukkit.getScheduler().runTaskLater(PLUGIN, () -> invites.remove(((Player) commandSender).getUniqueId()), 45 * 20);
                        }
                        break;
                    case "accept":
                        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) return false;
                        Player sender = Bukkit.getPlayer(strings[1]);
                        if (invites.containsKey(sender.getUniqueId()) && invites.get(sender.getUniqueId()).equals(((Player) commandSender).getUniqueId())) {
                            if (atlantPlayer.getFraction().getName().equalsIgnoreCase("NONE")) {
                                AtlantPlayer atlantSender = PLUGIN.getOnlinePlayers().get(sender);
                                if (atlantSender.getFraction().getName().equals("ARMY") && !atlantPlayer.hasCard()) {
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMedicineCardFound"));
                                    return true;
                                }
                                if (atlantSender.getFraction().getName().equals("POLICE") && !atlantPlayer.hasTicket()) {
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoArmyTicketFound"));
                                    return true;
                                }
                                atlantPlayer.setFraction(atlantSender.getFraction());
                                atlantPlayer.setPost(PostUtil.getPostByName("F" + atlantPlayer.getFraction().getName()));
                                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("joinFraction"));
                            } else {
                                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("JustInFractionException"));
                                return true;
                            }
                        } else {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("JustInFractionException"));
                            return true;
                        }
                        break;
                    case "kick":
                        if (!atlantPlayer.getPost().getName().equalsIgnoreCase("OWNER")) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoRootsException"));
                            return true;
                        }
                        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoPlayerFoundException"));
                            return true;
                        }
                        AtlantPlayer playerToKick = PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[1]));
                        if (!atlantPlayer.getFraction().equals(playerToKick.getFraction())) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("PlayerInAnotherFractionException"));
                            return true;
                        }
                        playerToKick.setFraction(FractionUtil.getNoneFraction());
                        playerToKick.setPost(PostUtil.getNonePost());
                        Bukkit.getPlayer(strings[1]).sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("wasKicked"));
                        break;
                    case "join":
                        Fraction fraction = FractionUtil.getFractionByName(strings[1]);
                        if (fraction != null) {
                            if (atlantPlayer.getFraction().getName().equalsIgnoreCase("NONE")) {
                                if (strings[1].equals("ARMY") && !atlantPlayer.hasCard()) {
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMedicineCardFound"));
                                    return true;
                                }
                                if (strings[1].equals("POLICE") && !atlantPlayer.hasTicket()) {
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoArmyTicketFound"));
                                    return true;
                                }
                                atlantPlayer.setFraction(fraction);
                                atlantPlayer.setPost(PostUtil.getPostByName("F" + fraction.getName()));
                                commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("joinFraction"));
                            } else {
                                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("JustInFractionException"));
                                return true;
                            }
                        }
                        break;
                }
                break;
            case 3:
                switch (strings[0]) {
                    case "setpost":
                        if (!atlantPlayer.getPost().getName().equalsIgnoreCase("OWNER")) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoRootsException"));
                            return true;
                        }
                        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoPlayerFoundException"));
                            return true;
                        }
                        AtlantPlayer player = PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[1]));
                        try {
                            if (!PostUtil.getPostByName(strings[2]).getParrent().equals(atlantPlayer.getFraction())) {
                                commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                                return true;
                            }
                            player.setPost(PostUtil.getPostByName(strings[2]));
                        } catch (Exception e) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                            return true;
                        }
                        break;
                }
                break;
        }
        return true;
    }
}

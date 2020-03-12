package ru.func.atlantgta.command.fraction;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.IPlayer;
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

    private Map<UUID, UUID> invites = Maps.newHashMap();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!PLUGIN.getOnlinePlayers().containsKey(((Player) commandSender).getUniqueId())) return false;

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId());

        switch (strings.length) {
            case 1:
                switch (strings[0]) {
                    case "leave":
                        atlantPlayer.setFraction(FractionUtil.getNoneFraction());
                        atlantPlayer.setPost(PostUtil.getNonePost());
                        commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("leaveFraction"));
                        return true;
                    //Другие аргументы при одном аргументе
                }
                break;
            case 2:
                switch (strings[0]) {
                    /*case "invite":

                        if (!atlantPlayer.getPost().getRoots().contains("fractionInviteCommand")) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoRootsException"));
                            return true;
                        }
                        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoPlayerFoundException"));
                            return true;
                        }
                        if (PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[1]).getUniqueId()).getLevel() < atlantPlayer.getFraction().getMinLevel()) {
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
                                IPlayer atlantSender = PLUGIN.getOnlinePlayers().get(sender.getUniqueId());
                                if (atlantSender.getFraction().getName().equals("ARMY") && !atlantPlayer.isCard()) {
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMedicineCardFound"));
                                    return true;
                                }
                                if (atlantSender.getFraction().getName().equals("POLICE") && !atlantPlayer.isTicket()) {
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
                        break;*/
                    case "kick":
                        if (!atlantPlayer.getPost().getRoots().contains("fractionKickCommand")) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoRootsException"));
                            return true;
                        }
                        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoPlayerFoundException"));
                            return true;
                        }
                        IPlayer playerToKick = PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[1]).getUniqueId());
                        if (!atlantPlayer.getFraction().equals(playerToKick.getFraction())) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("PlayerInAnotherFractionException"));
                            return true;
                        }
                        playerToKick.setFraction(FractionUtil.getNoneFraction());
                        playerToKick.setPost(PostUtil.getNonePost());
                        Bukkit.getPlayer(strings[1]).sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("wasKicked"));
                        return true;
                    case "join":
                        if(PLUGIN.getOnlinePlayers().get(((Player) commandSender).getUniqueId()).getLevel() >= 2) {
                            FractionUtil.getFractionBySubName(strings[1]).ifPresent(fraction -> {
                                boolean cancelled = false;
                                if (atlantPlayer.getFraction().getName().equalsIgnoreCase("NONE")) {
                                    if (fraction.getName().equalsIgnoreCase("ARMY") && !atlantPlayer.isCard()) {
                                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoMedicineCardFound"));
                                        cancelled = true;
                                    }
                                    if (!cancelled) {
                                        if (fraction.getName().equalsIgnoreCase("POLICE") && !atlantPlayer.isTicket()) {
                                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoArmyTicketFound"));
                                            cancelled = true;
                                        }
                                        if (!cancelled) {
                                            atlantPlayer.setFraction(fraction);
                                            atlantPlayer.setPost(PostUtil.getPostByName("F" + fraction.getName()));
                                            commandSender.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("joinFraction"));
                                        }
                                    }
                                } else
                                    commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("JustInFractionException"));
                            });
                        } else
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("LowLevelJoiningFraction"));
                        return true;
                }
                return true;
            case 3:
                if (strings[0].equals("setpost")) {
                    if (!atlantPlayer.getPost().getRoots().contains("fractionSetpostCommand")) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoRootsException"));
                        return true;
                    }
                    if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(strings[1]))) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NoPlayerFoundException"));
                        return true;
                    }
                    IPlayer player = PLUGIN.getOnlinePlayers().get(Bukkit.getPlayer(strings[1]).getUniqueId());
                    try {
                        if (!PostUtil.getPostByName(strings[2]).getParent().equals(atlantPlayer.getFraction())) {
                            commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                            return true;
                        }
                        player.setPost(PostUtil.getPostByName(strings[2]));
                    } catch (Exception e) {
                        commandSender.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("NumberFormatException"));
                        return true;
                    }
                }
                break;
        }
        return false;
    }
}

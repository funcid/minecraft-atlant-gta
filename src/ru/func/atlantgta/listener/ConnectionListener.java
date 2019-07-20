package ru.func.atlantgta.listener;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.fraction.PostUtil;
import ru.func.atlantgta.util.MessageUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final AtlantGTA PLUGIN;
    private static List<UUID> hideScoreboard = new ArrayList<>();
    final int UPDATE_TIME;

    private ConfigurationSection scoreboardSelection;
    private final String NAME;
    private final String DONATE;
    private final String ADDRESS;
    private final String CITY;

    public ConnectionListener(AtlantGTA plugin) {
        PLUGIN = plugin;
        UPDATE_TIME = PLUGIN.getConfig().getInt("settings.scoreboard.update");

        scoreboardSelection = PLUGIN.getConfig().getConfigurationSection("scoreboard");
        NAME = scoreboardSelection.getString("top");
        DONATE = scoreboardSelection.getString("donate");
        ADDRESS = scoreboardSelection.getString("address");
        CITY = scoreboardSelection.getString("city");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        if (loadStats(e.getPlayer()).getPost().getRoots().contains("doubleHealth"))
            e.getPlayer().setMaxHealth(40);
        enableScoreboard(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        saveStats(e.getPlayer(), 0);
    }

    public AtlantPlayer loadStats(Player p) {

        ConfigurationSection standardStatsConfigurationSection = PLUGIN.getConfig().getConfigurationSection("standardStats");
        try {
            ResultSet rs = PLUGIN.getStatement().executeQuery("SELECT * FROM `AtlantPlayers` WHERE uuid = '" + p.getUniqueId() + "';");
            if (rs.next()) {
                AtlantPlayer atlantPlayer = new AtlantPlayer(
                        rs.getInt("level"),
                        rs.getInt("age"),
                        FractionUtil.getFractionByName(rs.getString("fraction").split(":")[0]),
                        PostUtil.getPostByName(rs.getString("fraction").split(":")[1]),
                        0,
                        rs.getInt("ammo"),
                        rs.getBoolean("ticket"),
                        rs.getBoolean("card")
                );
                //Создает объект с уровнем, возрастом, фракцией, должностью
                PLUGIN.getOnlinePlayers().put(p, atlantPlayer);
                return atlantPlayer;
            } else {
                //Создает новый профиль в базе данных
                PLUGIN.getStatement().executeUpdate("INSERT INTO `AtlantPlayers` (uuid, name, level, age, ammo, fraction, ticket, card) VALUES(" +
                        "'" + p.getUniqueId() + "', " +
                        "'" + p.getName() + "', " +
                        "'" + standardStatsConfigurationSection.getString("level") + "'," +
                        "'" + standardStatsConfigurationSection.getString("age") + "', " +
                        "'" + standardStatsConfigurationSection.getString("ammo") + "', " +
                        "'NONE:NONE'" +
                        "'false', " +
                        "'false');");
                p.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("newAccount"));
                return loadStats(p);
            }
        } catch (Exception e) {
            p.sendMessage(MessageUtil.getERROR() + MessageUtil.getErrors().getString("LoadProfileException"));
        }
        return null;
    }

    public void saveStats(Player p, int i) {
        if (PLUGIN.getOnlinePlayers().containsKey(p)) {
            AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(p);
            try {
                ResultSet rs = PLUGIN.getStatement().executeQuery("SELECT * FROM `AtlantPlayers` WHERE uuid = '" + p.getUniqueId() + "';");
                if (rs.next())
                    PLUGIN.getStatement().executeUpdate("UPDATE `AtlantPlayers` SET " +
                            "level = '" + atlantPlayer.getLevel() + "', " +
                            "age = '" + atlantPlayer.getAge() + "', " +
                            "ammo = '" + atlantPlayer.getAmmunition() + "', " +
                            "fraction = '" + atlantPlayer.getFraction().getName() + ":" + atlantPlayer.getPost().getName() + "', " +
                            "ticket = '" + atlantPlayer.hasTicket() + "', " +
                            "card = '" + atlantPlayer.hasCard() + "' " +
                            "WHERE uuid = '" + p.getUniqueId() + "';");

                Bukkit.getLogger().info(p.getName() + " сохранен.");
            } catch (SQLException ex) {
                if (i < 3) saveStats(p, ++i);
            }
            PLUGIN.getOnlinePlayers().remove(p);
        }
    }

    private void enableScoreboard(Player player) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        Scoreboard scoreboard = new Scoreboard();

        ScoreboardObjective objective = scoreboard.registerObjective("§a§lAtlantWorld", IScoreboardCriteria.b);
        PacketPlayOutScoreboardObjective createObj = new PacketPlayOutScoreboardObjective(objective, 0);
        PacketPlayOutScoreboardObjective removeObj = new PacketPlayOutScoreboardObjective(objective, 1);
        PacketPlayOutScoreboardDisplayObjective display = new PacketPlayOutScoreboardDisplayObjective(1, objective);

        objective.setDisplayName(NAME);

        ScoreboardScore playerScore = new ScoreboardScore(scoreboard, objective, "§c§lИгрок:");
        playerScore.setScore(13);
        ScoreboardScore nameScore = new ScoreboardScore(scoreboard, objective, "§lИмя: §7>> §6" + player.getName());
        nameScore.setScore(12);
        ScoreboardScore null1Score = new ScoreboardScore(scoreboard, objective, " ");
        null1Score.setScore(14);
        ScoreboardScore null2Score = new ScoreboardScore(scoreboard, objective, "  ");
        null2Score.setScore(9);
        ScoreboardScore null3Score = new ScoreboardScore(scoreboard, objective, "   ");
        null3Score.setScore(4);
        ScoreboardScore informationScore = new ScoreboardScore(scoreboard, objective, "§c§lИнформация:");
        informationScore.setScore(8);
        ScoreboardScore donateScore = new ScoreboardScore(scoreboard, objective, DONATE);
        donateScore.setScore(3);
        ScoreboardScore ipScore = new ScoreboardScore(scoreboard, objective, ADDRESS);
        ipScore.setScore(2);
        ScoreboardScore cityScore = new ScoreboardScore(scoreboard, objective, CITY);
        cityScore.setScore(1);

        AtlantPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (hideScoreboard.contains(player.getUniqueId()) || !player.isOnline()) {
                    playerConnection.sendPacket(removeObj);
                    hideScoreboard.remove(player.getUniqueId());
                    this.cancel();
                } else {
                    playerConnection.sendPacket(removeObj);
                    playerConnection.sendPacket(createObj);
                    playerConnection.sendPacket(display);

                    String stars = "§6§l";
                    for (int i = 0; i < atlantPlayer.getStars(); i++)
                        stars += "✮";
                    String none = "§7§l";
                    for (int i = 0; i < 5 - atlantPlayer.getStars(); i++)
                        none += "✮";

                    ScoreboardScore fractionScore = new ScoreboardScore(scoreboard, objective, "§lФракция: §7>> §6" + atlantPlayer.getFraction().getSubName());
                    fractionScore.setScore(11);
                    ScoreboardScore postScore = new ScoreboardScore(scoreboard, objective, "§lДолжность: §7>> §6" + atlantPlayer.getPost().getSubName());
                    postScore.setScore(10);
                    ScoreboardScore starsScore = new ScoreboardScore(scoreboard, objective, "§a§lПреступность: §7>> " + stars + none);
                    starsScore.setScore(7);
                    ScoreboardScore onlineScore = new ScoreboardScore(scoreboard, objective, "§lИгроков: §7>> §6" + Bukkit.getOnlinePlayers().size());
                    onlineScore.setScore(6);
                    ScoreboardScore balanceScore = new ScoreboardScore(scoreboard, objective, "§lБаланс: §7>> §6" + (long) PLUGIN.getEconomy().getBalance(player));
                    balanceScore.setScore(5);

                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(null1Score));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(playerScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(nameScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(fractionScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(postScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(null2Score));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(informationScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(starsScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(onlineScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(balanceScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(null3Score));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(donateScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(ipScore));
                    playerConnection.sendPacket(new PacketPlayOutScoreboardScore(cityScore));
                }
            }
        }.runTaskTimerAsynchronously(PLUGIN, 0, 20 * UPDATE_TIME);
    }

    public static List<UUID> getHideScoreboard() {
        return hideScoreboard;
    }
}

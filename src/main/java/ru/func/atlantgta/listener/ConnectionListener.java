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
import ru.func.atlantgta.IPlayer;
import ru.func.atlantgta.database.QueryResult;
import ru.func.atlantgta.fraction.FractionUtil;
import ru.func.atlantgta.fraction.PostUtil;
import ru.func.atlantgta.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConnectionListener implements Listener {

    private final AtlantGTA PLUGIN;
    private List<UUID> hideScoreboard = new ArrayList<>();
    private final int UPDATE_TIME;

    private final String NAME;
    private final String DONATE;
    private final String ADDRESS;
    private final String CITY;

    public ConnectionListener(AtlantGTA plugin) {
        PLUGIN = plugin;
        UPDATE_TIME = PLUGIN.getConfig().getInt("settings.scoreboard.update");

        ConfigurationSection scoreboardSelection = PLUGIN.getConfig().getConfigurationSection("scoreboard");
        NAME = scoreboardSelection.getString("top");
        DONATE = scoreboardSelection.getString("donate");
        ADDRESS = scoreboardSelection.getString("address");
        CITY = scoreboardSelection.getString("city");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        loadStats(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        saveStats(e.getPlayer());
    }

    public void loadStats(Player p) {
        ConfigurationSection standardStatsConfigurationSection = PLUGIN.getConfig().getConfigurationSection("standardStats");
        PLUGIN.getDatabase().executeQuery("SELECT * FROM `AtlantPlayers` WHERE uuid = '" + p.getUniqueId().toString() + "';", result -> {
            IPlayer player;
            if (result.isEmpty()) {
                player = new AtlantPlayer(
                        standardStatsConfigurationSection.getInt("level"),
                        standardStatsConfigurationSection.getInt("age"),
                        FractionUtil.getNoneFraction(),
                        PostUtil.getNonePost(),
                        0,
                        standardStatsConfigurationSection.getInt("ammo"),
                        false,
                        false
                );
                p.sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("newAccount"));
            } else {
                QueryResult.SQLSection rs = result.all().stream().findFirst().get();
                player = new AtlantPlayer(
                        rs.lookupValue("level"),
                        rs.lookupValue("age"),
                        FractionUtil.getFractionByName(((String) rs.lookupValue("fraction")).split(":")[0]).get(),
                        PostUtil.getPostByName(((String) rs.lookupValue("fraction")).split(":")[1]),
                        rs.lookupValue("star"),
                        rs.lookupValue("ammo"),
                        ((int) rs.lookupValue("ticket")) == 1,
                        ((int) rs.lookupValue("card")) == 1
                );
            }
            PLUGIN.getOnlinePlayers().put(p.getUniqueId(), player);
            p.setMaxHealth(player.getPost().getRoots().contains("doubleHealth") ? 40 : 20);
            enableScoreboard(p);
            PLUGIN.getLogger().info(p.getDisplayName() + " авторизация. УСПЕШНО");
        });
    }

    public void saveAll() {
        if (Bukkit.getOnlinePlayers().size() == 0)
            return;
        String values = Bukkit.getOnlinePlayers().stream()
                .map(this::getValues)
                .collect(Collectors.joining(","));
        PLUGIN.getDatabase().executeUpdate(String.format("REPLACE INTO `AtlantPlayers` VALUES %s;", values));
    }

    private void saveStats(Player p) {
        if (PLUGIN.getOnlinePlayers().containsKey(p.getUniqueId())) {
            PLUGIN.getDatabase().executeUpdate(String.format("REPLACE INTO `AtlantPlayers` VALUES %s;", getValues(p)));
            PLUGIN.getLogger().info(p.getDisplayName() + " сохранение. УСПЕШНО");
            PLUGIN.getOnlinePlayers().remove(p.getUniqueId());
        }
    }

    private String getValues(Player p) {
        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(p.getUniqueId());
        return String.format("('%s', '%s', %s, %s, %s, '%s', %s, %s, %s)",
                p.getUniqueId().toString(),
                p.getName(),
                atlantPlayer.getLevel(),
                atlantPlayer.getAge(),
                atlantPlayer.getAmmunition(),
                atlantPlayer.getFraction().getName() + ":" + atlantPlayer.getPost().getName(),
                (atlantPlayer.isTicket() ? 1 : 0),
                (atlantPlayer.isCard() ? 1 : 0),
                atlantPlayer.getStars()
        );
    }

    public void enableScoreboard(Player player) {
        /* С самного начала этот код понимали я и Бог,
        прошло 10 дней, отсался только Бог
        (лан шучу, это просто)
        С кем я вообще болтаю? Ахаха
        - Со мной
        - Но.. Кто ты?
        - Ты это я, только я не пишу говнокода
        - Потому что ты пишешь комментарии, а не код, дибил
        */
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        Scoreboard scoreboard = new Scoreboard();

        ScoreboardObjective objective = scoreboard.registerObjective(NAME, IScoreboardCriteria.b);
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

        IPlayer atlantPlayer = PLUGIN.getOnlinePlayers().get(player.getUniqueId());

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

                    StringBuilder stars = new StringBuilder("§6§l");
                    for (int i = 0; i < atlantPlayer.getStars(); i++)
                        stars.append("✮");
                    StringBuilder none = new StringBuilder("§7§l");
                    for (int i = 0; i < 5 - atlantPlayer.getStars(); i++)
                        none.append("✮");

                    ScoreboardScore fractionScore = new ScoreboardScore(scoreboard, objective, "§lФракция: §7>> §6" + atlantPlayer.getFraction().getSubName());
                    fractionScore.setScore(11);
                    ScoreboardScore postScore = new ScoreboardScore(scoreboard, objective, "§lДолжность: §7>> §6" + atlantPlayer.getPost().getSubName());
                    postScore.setScore(10);
                    ScoreboardScore starsScore = new ScoreboardScore(scoreboard, objective, "§a§lПреступность: §7>> " + stars.append(none).toString());
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

    public List<UUID> getHideScoreboard() {
        return hideScoreboard;
    }
}

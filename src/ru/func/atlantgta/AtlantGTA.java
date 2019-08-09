package ru.func.atlantgta;

import com.google.common.collect.Maps;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ru.func.atlantgta.command.*;
import ru.func.atlantgta.command.fraction.FractionChatCommand;
import ru.func.atlantgta.command.fraction.FractionCommand;
import ru.func.atlantgta.command.fraction.FractionTeleportCommand;
import ru.func.atlantgta.command.help.HelpMeCommand;
import ru.func.atlantgta.command.help.ReportBugCommand;
import ru.func.atlantgta.command.prison.ArrestCommand;
import ru.func.atlantgta.command.prison.LeaveCommand;
import ru.func.atlantgta.command.prison.PrisonCommand;
import ru.func.atlantgta.command.prison.UnarrestCommand;
import ru.func.atlantgta.command.TradeCommand;
import ru.func.atlantgta.database.MySQL;
import ru.func.atlantgta.fraction.*;
import ru.func.atlantgta.listener.ConnectionListener;
import ru.func.atlantgta.listener.MenuHandler;
import ru.func.atlantgta.listener.PlayerDamageListener;
import ru.func.atlantgta.listener.PlayerDeathListener;
import ru.func.atlantgta.util.MessageUtil;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class AtlantGTA extends JavaPlugin {

    private Economy ECONOMY = null;

    private MessageUtil messageUtil = new MessageUtil(this);
    private ConnectionListener connectionListener = new ConnectionListener(this);
    /* Мапа со всей статистикой игрока */
    private Map<Player, AtlantPlayer> onlinePlayers = Maps.newHashMap();

    /* SQL переменные */
    private Statement STATEMENT;
    private final ConfigurationSection sqLSettingsConfigurationSection = getConfig().getConfigurationSection("sqlSettings");
    private final MySQL BASE = new MySQL(
            sqLSettingsConfigurationSection.getString("user"),
            sqLSettingsConfigurationSection.getString("password"),
            sqLSettingsConfigurationSection.getString("host"),
            sqLSettingsConfigurationSection.getString("database"),
            sqLSettingsConfigurationSection.getInt("port")
    );
    /* Fraction переменные */
    private final ConfigurationSection fractionsConfigurationSection = getConfig().getConfigurationSection("fractions");

    /* Я писал этот код под таким мощным анабиозом что обмен веществ в моем мозге
    настолько замедлился что впал в кому и на самых низких мощностях продолжил
    функцию написания этого кода
     */
    @Override
    public void onEnable() {
        registerConfig();
        if (!setupEconomy())
            Bukkit.getPluginManager().disablePlugin(this);
        // Подключение к базе данных
        try {
            getLogger().info("[!] Connecting to DataBase.");
            STATEMENT = BASE.openConnection().createStatement();
            STATEMENT.executeUpdate("CREATE TABLE IF NOT EXISTS `AtlantPlayers` (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid TEXT, name TEXT, level INT, age INT, ammo INT, fraction TEXT, ticket INT, card INT);");
            getLogger().info("[!] Connected to DataBase.");
        } catch (ClassNotFoundException | SQLException e) {
            getLogger().info("[!] Connection exception.");
            Bukkit.broadcastMessage(MessageUtil.getFATAL_ERROR() + MessageUtil.getErrors().getString("LoadDatabaseException"));
        }
        // Создание фракций
        for (String fraction : fractionsConfigurationSection.getKeys(false)) {
            ConfigurationSection arg = fractionsConfigurationSection.getConfigurationSection(fraction);
            String[] coords = arg.getString("base").split("\\s+");
            Location location = new Location(Bukkit.getWorld("world"),
                    Double.parseDouble(coords[0]),
                    Double.parseDouble(coords[1]),
                    Double.parseDouble(coords[2])
            );
            Fraction currentFraction = new FractionBuilder()
                    .minLevel(arg.getInt("minLevel"))
                    .name(arg.getName())
                    .subName(arg.getString("name"))
                    .baseLocation(location)
                    .build();
            FractionUtil.getFractions().add(currentFraction);
            for (String postName : arg.getConfigurationSection("ranks").getKeys(false)) {
                ConfigurationSection post = arg.getConfigurationSection("ranks").getConfigurationSection(postName);
                PostUtil.getPosts().add(new PostBuilder()
                        .parrent(currentFraction)
                        .name(post.getName())
                        .subName(post.getString("name"))
                        .roots(post.getString("roots"))
                        .salary(post.getInt("salary"))
                        .build()
                );
            }
        }

        Bukkit.getPluginManager().registerEvents(connectionListener, this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MenuHandler(), this);

        Bukkit.getOnlinePlayers().forEach(connectionListener::loadStats);

        // Регистрация команд
        Bukkit.getPluginCommand("f").setExecutor(new FractionCommand(this));
        Bukkit.getPluginCommand("fc").setExecutor(new FractionChatCommand(this));
        Bukkit.getPluginCommand("base").setExecutor(new FractionTeleportCommand(this));
        Bukkit.getPluginCommand("rob").setExecutor(new RobCommand(this));
        Bukkit.getPluginCommand("pass").setExecutor(new PasswordCommand(this));
        Bukkit.getPluginCommand("resetstars").setExecutor(new ResetstarsCommand(this));
        Bukkit.getPluginCommand("level").setExecutor(new LevelCommand(this));
        Bukkit.getPluginCommand("levelup").setExecutor(new LevelupCommand(this));
        Bukkit.getPluginCommand("givestars").setExecutor(new GiveStarsCommand(this));
        Bukkit.getPluginCommand("arrest").setExecutor(new ArrestCommand(this));
        Bukkit.getPluginCommand("unarrest").setExecutor(new UnarrestCommand(this));
        Bukkit.getPluginCommand("prison").setExecutor(new PrisonCommand(this));
        Bukkit.getPluginCommand("leave").setExecutor(new LeaveCommand(this));
        Bukkit.getPluginCommand("getequip").setExecutor(new GetEquipmentCommand(this));
        Bukkit.getPluginCommand("robequip").setExecutor(new RobEquipmentCommand(this));
        Bukkit.getPluginCommand("makegun").setExecutor(new MakeGunCommand(this));
        Bukkit.getPluginCommand("trade").setExecutor(new TradeCommand(this));
        Bukkit.getPluginCommand("shtraf").setExecutor(new ShtrafCommand(this));
        Bukkit.getPluginCommand("hp").setExecutor(new HealthCommand(this));
        Bukkit.getPluginCommand("givemedcard").setExecutor(new GiveMedicineCardCommand(this));
        Bukkit.getPluginCommand("givearmyticket").setExecutor(new GiveArmyTicketCommand(this));
        Bukkit.getPluginCommand("salary").setExecutor(new SalaryCommand(this));
        Bukkit.getPluginCommand("contract").setExecutor(new ContractCommand(this));
        Bukkit.getPluginCommand("helpme").setExecutor(new HelpMeCommand(this));
        Bukkit.getPluginCommand("hidescore").setExecutor(new HideScoreboardCommand());
        Bukkit.getPluginCommand("reportbug").setExecutor(new ReportBugCommand());
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> connectionListener.saveStats(player, 1));
    }

    private void registerConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
            ECONOMY = economyProvider.getProvider();

        return (ECONOMY != null);
    }

    /* Возвращает мапу со статистикой онлайн игроков */
    public Map<Player, AtlantPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    /* Возвращает стэйтемент */
    public Statement getStatement() {
        return STATEMENT;
    }

    /* Возвращает экономику */
    public Economy getEconomy() {
        return ECONOMY;
    }
}


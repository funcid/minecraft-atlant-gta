package ru.func.atlantgta;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
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
import ru.func.atlantgta.database.MySQL;
import ru.func.atlantgta.fraction.*;
import ru.func.atlantgta.listener.ConnectionListener;
import ru.func.atlantgta.listener.MenuHandler;
import ru.func.atlantgta.listener.PlayerDamageListener;
import ru.func.atlantgta.listener.PlayerDeathListener;
import ru.func.atlantgta.util.MessageUtil;

import java.util.Map;
import java.util.UUID;

public class AtlantGTA extends JavaPlugin {

    @Getter
    private static AtlantGTA instance;

    private Economy ECONOMY = null;

    private MessageUtil messageUtil = new MessageUtil(this);
    @Getter
    private ConnectionListener connectionListener = new ConnectionListener(this);
    /* Мапа со всей статистикой игрока */
    @Getter
    private Map<UUID, IPlayer> onlinePlayers = Maps.newHashMap();

    /* SQL переменные */
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
        instance = this;
        registerConfig();
        if (!setupEconomy())
            Bukkit.getPluginManager().disablePlugin(this);
        // Подключение к базе данных
        getLogger().info("[!] Connecting to DataBase.");
        BASE.executeUpdate("CREATE TABLE IF NOT EXISTS `AtlantPlayers` (uuid varchar(50), name varchar(50), level INT, age INT, ammo INT, fraction varchar(150), ticket INT, card INT, star INT, PRIMARY KEY(uuid));");
        getLogger().info("[!] Connected to DataBase.");
        // Создание фракций
        getLogger().info("[Загрузка фракций и постов начата]");
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
                getLogger().info(" ---- " + postName + " пост успешно установлен.");
            }
            getLogger().info(fraction + " фракция успешно установлена.");
        }
        getLogger().info("[Загрузка фракций завершена]");

        Bukkit.getPluginManager().registerEvents(connectionListener, this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MenuHandler(), this);

        Bukkit.getOnlinePlayers().forEach(connectionListener::loadStats);

        // Регистрация команд
        Bukkit.getPluginCommand("giveabil").setExecutor(new GiveAbilityCommand(this));
        Bukkit.getPluginCommand("f").setExecutor(new FractionCommand(this));
        Bukkit.getPluginCommand("fc").setExecutor(new FractionChatCommand(this));
        Bukkit.getPluginCommand("base").setExecutor(new FractionTeleportCommand(this));
        Bukkit.getPluginCommand("rob").setExecutor(new RobCommand(this));
        Bukkit.getPluginCommand("pass").setExecutor(new PassCommand(this));
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
        Bukkit.getPluginCommand("salary").setExecutor(new SalaryCommand(this));
        Bukkit.getPluginCommand("contract").setExecutor(new ContractCommand(this));
        Bukkit.getPluginCommand("helpme").setExecutor(new HelpMeCommand(this));
        Bukkit.getPluginCommand("hidescore").setExecutor(new HideScoreboardCommand(this));
        Bukkit.getPluginCommand("reportbug").setExecutor(new ReportBugCommand());
    }

    @Override
    public void onDisable() {
        connectionListener.saveAll();
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
    public MySQL getDatabase() {
        return BASE;
    }

    public Economy getEconomy() {
        return ECONOMY;
    }
}


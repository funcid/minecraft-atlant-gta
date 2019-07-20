package ru.func.atlantgta.util;

import org.bukkit.configuration.ConfigurationSection;
import ru.func.atlantgta.AtlantGTA;

public class MessageUtil {

    private static String INFO;
    private static String ERROR;
    private static String FATAL_ERROR;

    private static ConfigurationSection errors;
    private static ConfigurationSection messages;

    public MessageUtil(AtlantGTA plugin) {
        ConfigurationSection messageConfigurationSection = plugin.getConfig()
                .getConfigurationSection("message.messageUtil");

        errors = plugin.getConfig()
                .getConfigurationSection("message.errors");
        messages = plugin.getConfig()
                .getConfigurationSection("message.messages");

        INFO = messageConfigurationSection.getString("INFO");
        ERROR = messageConfigurationSection.getString("ERROR");
        FATAL_ERROR = messageConfigurationSection.getString("FATAL_ERROR");
    }

    public static String getINFO() {
        return INFO;
    }

    public static String getERROR() {
        return ERROR;
    }

    public static String getFATAL_ERROR() {
        return FATAL_ERROR;
    }

    public static ConfigurationSection getErrors() {
        return errors;
    }

    public static ConfigurationSection getMessages() {
        return messages;
    }
}

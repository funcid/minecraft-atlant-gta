package ru.func.atlantgta.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuHandler implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if ("ПАСПОРТ".equals(ChatColor.stripColor(e.getInventory().getName())))
            e.setCancelled(true);
    }
}

package ru.func.atlantgta.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ru.func.atlantgta.AtlantGTA;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerDamageListener implements Listener {

    private AtlantGTA PLUGIN;
    private final int TIME;

    public PlayerDamageListener(AtlantGTA plugin) {
        PLUGIN = plugin;
        TIME = PLUGIN.getConfig().getInt("settings.policeStick.time") * 20;
    }

    private List<UUID> blockedPlayers = new ArrayList<>();

    @EventHandler
    public void onPlayerDamagePlayerEvent(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player entity = (Player) e.getEntity();
            if (((Player) e.getDamager()).getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
                if (PLUGIN.getOnlinePlayers().get(entity).getPost().getRoots().contains("policeStick")) {
                    if (PLUGIN.getOnlinePlayers().get(entity).getStars() > 0) {
                        entity.performCommand("lay");
                        blockedPlayers.add(entity.getUniqueId());
                        Bukkit.getScheduler().runTaskLater(PLUGIN, () -> {
                            entity.performCommand("lay");
                            blockedPlayers.remove(entity.getUniqueId());
                        }, TIME);
                    }
                }
            }
        }
    }
    @EventHandler
    public void playerUseCommandEvent(PlayerCommandPreprocessEvent e) {
        if (blockedPlayers.contains(e.getPlayer().getUniqueId()))
            e.setCancelled(true);
    }
}

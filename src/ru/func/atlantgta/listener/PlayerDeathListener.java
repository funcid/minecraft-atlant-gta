package ru.func.atlantgta.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ru.func.atlantgta.AtlantGTA;
import ru.func.atlantgta.AtlantPlayer;
import ru.func.atlantgta.util.MessageUtil;

public class PlayerDeathListener implements Listener {

    private AtlantGTA PLUGIN;

    public PlayerDeathListener(AtlantGTA plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    public void onPlayerDamagePlayerEvent(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            AtlantPlayer killer = PLUGIN.getOnlinePlayers().get(e.getEntity().getKiller());
            killer.setStars(killer.getStars() > 5 ? 5 : killer.getStars() + 1);

            if (ContractUtil.getContracts().containsKey(e.getEntity().getUniqueId())) {
                if (killer.getPost().getRoots().contains("completeContract")) {
                    PLUGIN.getEconomy().depositPlayer(
                            e.getEntity().getKiller(),
                            ContractUtil.getContracts().get(e.getEntity().getUniqueId())
                    );
                    e.getEntity().getKiller().sendMessage(MessageUtil.getINFO() + MessageUtil.getMessages().getString("completeContract")
                            .replace("%NAME%", e.getEntity().getPlayer().getName())
                            .replace("%MONEY%", ContractUtil.getContracts().get(e.getEntity().getUniqueId()) + "")
                    );
                    ContractUtil.getContracts().remove(e.getEntity().getUniqueId());
                }
            }
        }
    }
}

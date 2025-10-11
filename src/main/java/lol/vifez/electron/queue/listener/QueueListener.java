package lol.vifez.electron.queue.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.queue.Queue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class QueueListener implements Listener {

    public QueueListener() {
        Practice instance = Practice.getInstance();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Queue queue = Practice.getInstance().getQueueManager().getQueue(player.getUniqueId());

        if (queue != null) {
            queue.remove(player);
        }
    }
}
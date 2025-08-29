package lol.vifez.electron.util.nametag.listener;

import lol.vifez.electron.util.nametag.NameTagAPI;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class NameTagListener implements Listener {

    private final NameTagAPI instance;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        instance.getHandler().removeNameTag(event.getPlayer());
    }
}

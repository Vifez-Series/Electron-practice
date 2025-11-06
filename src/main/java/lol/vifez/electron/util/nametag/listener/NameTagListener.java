package lol.vifez.electron.util.nametag.listener;

import lol.vifez.electron.util.nametag.NameTagAPI;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@RequiredArgsConstructor
public class NameTagListener implements Listener {

    private final NameTagAPI instance;

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        instance.getHandler().removeNameTag(event.getPlayer());
    }
}

package lol.vifez.electron.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class PearlListener implements Listener {

    private static final long COOLDOWN = 16_000;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public PearlListener() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onPearlThrow(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.ENDER_PEARL) return;

        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (now - last < COOLDOWN) {
            double seconds = (COOLDOWN - (now - last)) / 1000.0;
            player.sendMessage(CC.translate("&cYou are on cooldown for " + String.format("%.1f", seconds) + "s"));
            event.setCancelled(true);
            return;
        }

        cooldowns.put(player.getUniqueId(), now);
    }
}
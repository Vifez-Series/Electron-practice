package lol.vifez.electron.profile.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.profile.menu.ProfileMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class ProfileListener implements Listener {

    private final Practice instance;

    public ProfileListener(Practice instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (instance.getProfileManager() == null) {
            return;
        }

        Profile profile = instance.getProfileManager().getProfile(event.getUniqueId());

        if (profile == null) {
            profile = new Profile(event.getUniqueId());
            instance.getProfileManager().save(profile);
        }

        if (profile.getName() == null || !profile.getName().equals(event.getName())) {
            profile.setName(event.getName());
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Player)) return;
        Player target = (Player) event.getRightClicked();

        Profile playerProfile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (playerProfile == null) return;

        Match match = playerProfile.getMatch();

        boolean inQueue = match != null && playerProfile.getQueue() != null;

        Match activeMatch = instance.getMatchManager().getMatch(player.getUniqueId());

        if (inQueue || activeMatch != null) {
            return;
        }

        Profile targetProfile = instance.getProfileManager().getProfile(target.getUniqueId());
        if (targetProfile == null) return;

        new ProfileMenu(targetProfile).openMenu(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (instance.getSpawnLocation() != null) {
            player.teleport(instance.getSpawnLocation());
        }

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(Hotbar.getSpawnItems());

        player.sendTitle(CC.translate("&b&lPRACTICE"), CC.translate("&7Welcome to Practice (EU)"));

        if (instance.getConfig().getBoolean("settings.join-message.enabled", true)) {
            List<String> messages = instance.getConfig().getStringList("settings.join-message.messages");
            for (String message : messages) {
                player.sendMessage(CC.translate(message));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile != null) {
            instance.getProfileManager().save(profile);
        }

        String quitMessage = instance.getConfig().getString("settings.quit-message", "%player% has left the game.");
        quitMessage = quitMessage.replace("%player%", player.getName());

        event.setQuitMessage(CC.translate(quitMessage));
    }
}
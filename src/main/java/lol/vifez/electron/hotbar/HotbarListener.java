package lol.vifez.electron.hotbar;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.menu.editor.KitSelectMenu;
import lol.vifez.electron.leaderboard.menu.LeaderboardMenu;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.navigator.menu.NavigatorMenu;
import lol.vifez.electron.settings.menu.OptionsMenu;
import lol.vifez.electron.queue.Queue;
import lol.vifez.electron.queue.menu.QueuesMenu;
import lol.vifez.electron.queue.menu.RankedMenu;
import lol.vifez.electron.queue.menu.UnrankedMenu;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class HotbarListener implements Listener {

    public HotbarListener() {
        Practice instance = Practice.getInstance();
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInHand();
        Practice instance = Practice.getInstance();

        if (itemInHand == null) return;

        if (itemInHand.isSimilar(Hotbar.UNRANKED.getItem())) {
            event.setCancelled(true);
            new UnrankedMenu(instance).openMenu(player);

        } else if (itemInHand.isSimilar(Hotbar.LEADERBOARDS.getItem())) {
            event.setCancelled(true);
            new LeaderboardMenu(instance).openMenu(player);

        } else if (itemInHand.isSimilar(Hotbar.SETTINGS.getItem())) {
            event.setCancelled(true);
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            if (profile != null) new OptionsMenu().openMenu(player);
            else player.sendMessage(CC.translate("&cProfile not found!"));

        } else if (itemInHand.isSimilar(Hotbar.RANKED.getItem())) {
            event.setCancelled(true);
            new RankedMenu(instance).openMenu(player);

        } else if (itemInHand.isSimilar(Hotbar.LEAVE_QUEUE.getItem())) {
            event.setCancelled(true);
            Queue queue = instance.getQueueManager().getQueue(player.getUniqueId());
            if (queue != null) queue.remove(player);

            player.getInventory().setContents(Hotbar.getSpawnItems());
            player.getInventory().setArmorContents(null);
            CC.sendMessage(player, "&cYou left the queue!");

        } else if (itemInHand.isSimilar(Hotbar.KIT_EDITOR.getItem())) {
            event.setCancelled(true);
            new KitSelectMenu(instance).openMenu(player);

        } else if (itemInHand.isSimilar(Hotbar.NAVIGATOR.getItem())) {
            event.setCancelled(true);
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            if (profile != null) {
                new NavigatorMenu(instance).openMenu(player);
            } else {
                player.sendMessage(CC.translate("&cProfile not found!"));
            }

        } else if (itemInHand.isSimilar(Hotbar.QUEUES.getItem())) {
            event.setCancelled(true);
            new QueuesMenu(instance).openMenu(player);
        }
    }
}
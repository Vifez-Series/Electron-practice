package lol.vifez.electron.queue.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class RankedQueueLore extends EasyButton {

    public RankedQueueLore(Practice instance, Player player, Kit kit) {
        super(new ItemBuilder(kit.getDisplayItem())
                .name("&b&l" + kit.getName())
                .lore(buildLore(instance, player, kit))
                .flag(ItemFlag.HIDE_ATTRIBUTES)
                .build(), true, false, () -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
            instance.getQueueManager().getQueue(kit, true).add(profile.getPlayer());

            CC.sendMessage(player, " ");
            CC.sendMessage(player, "&b&l" + kit.getName() + " &7(Ranked)");
            CC.sendMessage(player, "&7» &ePlease wait for the match to start...");
            CC.sendMessage(player, " ");

            player.closeInventory();
        }, null);
    }

    public static List<String> buildLore(Practice instance, Player player, Kit kit) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        int playing = instance.getMatchManager().getPlayersInKitMatches(kit);
        int inQueue = instance.getQueueManager().getPlayersInQueue(kit, true).size();

        List<String> lore = new java.util.ArrayList<>();

        if (kit.getDescription() != null && !kit.getDescription().isEmpty()) {
            lore.addAll(kit.getDescription());
            lore.add("");
        }

        lore.add("&fFighting: &c" + playing);
        lore.add("&fQueueing: &c" + inQueue);
        lore.add("");
        lore.add("&fYour Elo&7: &c" + profile.getElo(kit));
        lore.add("");
        lore.add("&c&lTop 3");

        List<Profile> topPlayers = instance.getProfileManager().getProfiles().values().stream()
                .filter(p -> p.getEloMap().containsKey(kit.getName().toLowerCase()))
                .sorted(Comparator.comparingInt(p -> -p.getElo(kit)))
                .limit(3)
                .collect(Collectors.toList());

        for (int i = 0; i < 3; i++) {
            if (i < topPlayers.size()) {
                Profile p = topPlayers.get(i);
                lore.add(String.format("&c%d. &f%s &7(&c%d&7)", i + 1, p.getName(), p.getElo(kit)));
            } else {
                lore.add(String.format("&c%d. N/A", i + 1));
            }
        }

        lore.add("");
        lore.add("&aClick to queue!");
        return lore;
    }
}
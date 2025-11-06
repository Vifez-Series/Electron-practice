package lol.vifez.electron.queue.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
 */

@RequiredArgsConstructor
public class RankedMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player player) {
        return "&7Select a ranked kit...";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Kit> rankedKits = instance.getKitManager().getKits().values().stream()
                .filter(Kit::isRanked)
                .collect(Collectors.toList());

        int[] kitSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30};

        for (int i = 0; i < rankedKits.size() && i < kitSlots.length; i++) {
            buttons.put(kitSlots[i], new RankedKitButton(instance, player, rankedKits.get(i)));
        }

        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 14)
                            .name("&7")
                            .build(),
                    true, false, () -> {}));
        }

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.FIREWORK)
                        .name("&c&lRandom Queue")
                        .lore("&7Select a random ranked kit")
                        .build(),
                true,
                false,
                () -> {
                    if (rankedKits.isEmpty()) {
                        CC.sendMessage(player, "&cNo ranked kits are available to queue.");
                        return;
                    }

                    Kit randomKit = rankedKits.get((int) (Math.random() * rankedKits.size()));
                    Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

                    instance.getQueueManager().getQueue(randomKit, true).add(profile.getPlayer());

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&c&lRandom Queue &7[R]");
                    CC.sendMessage(player, "&c• &7Kit: &c" + randomKit.getName());
                    CC.sendMessage(player, "&c• &7Searching for a &cplayer...");
                    CC.sendMessage(player, " ");
                    player.closeInventory();
                }
        ));

        return buttons;
    }
}

class RankedKitButton extends EasyButton {

    public RankedKitButton(Practice instance, Player player, Kit kit) {
        super(new ItemBuilder(kit.getDisplayItem())
                        .name("&c&l" + kit.getName())
                        .lore(buildLore(instance, player, kit))
                        .flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)
                        .build(),
                true,
                false,
                () -> {
                    Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
                    instance.getQueueManager().getQueue(kit, true).add(profile.getPlayer());

                    CC.sendMessage(player, " ");
                    CC.sendMessage(player, "&c&lRanked Queue");
                    CC.sendMessage(player, "&c• &7Kit: &c" + kit.getName());
                    CC.sendMessage(player, "&c• &7Searching for a &cplayer...");
                    CC.sendMessage(player, " ");

                    player.closeInventory();
                });
    }

    private static List<String> buildLore(Practice instance, Player player, Kit kit) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

        int playing = instance.getMatchManager().getPlayersInKitMatches(kit);
        int inQueue = instance.getQueueManager().getPlayersInQueue(kit, true).size();

        List<String> lore = new ArrayList<>();

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
                .filter(p -> p.getEloMap().containsKey(kit.getName()))
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
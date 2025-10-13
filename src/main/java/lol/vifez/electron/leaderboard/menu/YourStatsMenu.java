package lol.vifez.electron.leaderboard.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.elo.EloUtil;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */
public class YourStatsMenu extends Menu {

    private final Practice instance;

    public YourStatsMenu(Practice instance) {
        this.instance = instance;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Your Stats";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            return buttons;
        }

        int globalElo = EloUtil.getGlobalElo(profile);

        List<String> globalLore = new ArrayList<>();
        globalLore.add("&7Your overall average");
        globalLore.add("&7across all ranked kits.");
        globalLore.add(" ");
        globalLore.add("&fGlobal Elo: &b" + globalElo);

        buttons.put(4, new EasyButton(
                new ItemBuilder(Material.NETHER_STAR)
                        .name("&b&lGlobal Elo")
                        .lore(globalLore)
                        .build(),
                true, true, () -> {}
        ));

        int[] kitSlots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };

        Kit[] kits = instance.getKitManager().getKits().values().toArray(new Kit[0]);
        int index = 0;

        for (Kit kit : kits) {
            if (!kit.isRanked()) continue;
            if (index >= kitSlots.length) break;

            int elo = profile.getElo(kit);
            List<String> lore = new ArrayList<>();
            lore.add("&7Your current ranked elo");
            lore.add(" ");
            lore.add("&fElo: &b" + elo);

            buttons.put(kitSlots[index], new EasyButton(
                    new ItemBuilder(kit.getDisplayItem())
                            .name(kit.getColor() + kit.getName())
                            .lore(lore)
                            .build(),
                    true, true, () -> {}
            ));

            index++;
        }

        buttons.put(40, new EasyButton(
                new ItemBuilder(Material.BOOK)
                        .name("&a&lLeaderboards")
                        .lore(Arrays.asList(
                                "&7View the global",
                                "&7Leaderboards.",
                                " ",
                                "&bClick to view!"
                        ))
                        .build(),
                true, true, () -> new LeaderboardMenu(instance).openMenu(player)
        ));

        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true, false, () -> {}
            ));
        }

        return buttons;
    }
}
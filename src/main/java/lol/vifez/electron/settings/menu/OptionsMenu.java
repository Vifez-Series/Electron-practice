package lol.vifez.electron.settings.menu;

import lol.vifez.electron.Practice;
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
public class OptionsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "&7Options";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        Practice instance = Practice.getInstance();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) return buttons;

        buttons.put(12, new EasyButton(
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("&b&lBank")
                        .lore(Arrays.asList(
                                "&7View and manage your bank.",
                                "&7balance, shop, and more.",
                                " ",
                                "&cCurrently unavailable."
                        ))
                        .build(),
                true, true, () -> {
        }
        ));

        buttons.put(14, new EasyButton(
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name("&b&lConfigurations")
                        .lore(Arrays.asList(
                                "&7Adjust your personal settings,",
                                "&7toggle scoreboard, messages, and more.",
                                " ",
                                "&bClick to open!"
                        ))
                        .build(),
                true, true, () -> new SettingsMenu(instance, profile).openMenu(player)
        ));

        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
        };

        for (int slot : borderSlots) {
            if (buttons.containsKey(slot)) continue;
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
package lol.vifez.electron.settings.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.buttons.ToggleMessagesButton;
import lol.vifez.electron.settings.menu.buttons.ToggleScoreboardButton;
import lol.vifez.electron.settings.menu.buttons.WorldTimeButton;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.DisplayButton;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */
public class SettingsMenu extends Menu {

    private final Practice instance;
    private final Profile profile;

    public SettingsMenu(Practice instance, Profile profile) {
        this.instance = instance;
        this.profile = profile;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Settings";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, ToggleScoreboardButton.createToggleScoreboardButton(profile, instance));
        buttons.put(11, ToggleMessagesButton.createToggleMessagesButton(profile, instance));
        buttons.put(12, WorldTimeButton.createWorldTimeButton(profile, instance));

        buttons.put(22, new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name("&c&lGo Back")
                        .lore("&7Return to the main options menu.")
                        .build(),
                true, true, () -> new OptionsMenu().openMenu(player)
        ));

        for (int i = 0; i < getSize(); i++) {
            buttons.putIfAbsent(i, new DisplayButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build()
            ));
        }

        return buttons;
    }
}
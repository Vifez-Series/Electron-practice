package lol.vifez.electron.settings.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.buttons.ToggleMessagesButton;
import lol.vifez.electron.settings.menu.buttons.ToggleScoreboardButton;
import lol.vifez.electron.settings.menu.buttons.WorldTimeButton;
import lol.vifez.electron.settings.menu.buttons.NicknameButton;
import lol.vifez.electron.settings.menu.buttons.ChatColorButton;
import lol.vifez.electron.settings.menu.buttons.ToggleChatButton;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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
        buttons.put(13, NicknameButton.createNicknameButton(profile, instance));
        buttons.put(14, ChatColorButton.createChatColorButton(profile, instance));
        buttons.put(15, ToggleChatButton.createToggleChatButton(profile, instance));

        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 26,
                19, 20, 21, 22, 23, 24, 25
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 8)
                            .build(),
                    true, false, () -> {}, null
            ));
        }

        return buttons;
    }
}
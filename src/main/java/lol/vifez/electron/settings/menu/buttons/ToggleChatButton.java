package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ToggleChatButton {

    public static Button createToggleChatButton(Profile profile, Practice instance) {
        boolean chatEnabled = profile.isChatEnabled();
        ItemStack chatItem = new ItemBuilder(Material.PAPER)
                .name(CC.translate("&eToggle Chat"))
                .lore(Arrays.asList(
                        CC.translate("&7Show or hide chat messages."),
                        CC.translate("&7Current value: " + (chatEnabled ? "&aShowing" : "&cHidden")),
                        CC.translate("&r"),
                        CC.translate("&eClick to toggle!")
                ))
                .build();

        return new EasyButton(chatItem, true, false, () -> {
            profile.setChatEnabled(!chatEnabled);
            Practice.getInstance().getProfileManager().save(profile);
            new SettingsMenu(instance, profile).openMenu(profile.getPlayer());
        }, null);
    }
}
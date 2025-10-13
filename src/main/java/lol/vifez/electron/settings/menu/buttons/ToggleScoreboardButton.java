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

public class ToggleScoreboardButton {
    public static Button createToggleScoreboardButton(Profile profile, Practice instance) {
        boolean scoreboardEnabled = profile.isScoreboardEnabled();
        ItemStack scoreboardItem = new ItemBuilder(Material.PAINTING)
                .name(CC.translate("&eScoreboard Visibility"))
                .lore(Arrays.asList(
                        CC.translate("&7Toggle the scoreboard visibility."),
                        CC.translate("&7Current value: " + (scoreboardEnabled ? "&aEnabled" : "&cDisabled")),
                        CC.translate("&r"),
                        CC.translate("&bClick to change!")
                ))
                .build();

        return new EasyButton(scoreboardItem, true, false, () -> {
            profile.setScoreboardEnabled(!scoreboardEnabled);
            Practice.getInstance().getProfileManager().save(profile);
            new SettingsMenu(instance, profile).openMenu(profile.getPlayer());
        });
    }
}
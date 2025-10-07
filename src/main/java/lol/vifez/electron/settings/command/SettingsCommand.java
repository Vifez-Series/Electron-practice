package lol.vifez.electron.settings.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@CommandAlias("settings|options")
public class SettingsCommand extends BaseCommand {

    private final Practice instance = Practice.getInstance();

    @Default
    public void openSettings(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            player.sendMessage(CC.translate("&cCould not load your profile."));
            return;
        }

        new SettingsMenu(instance, profile).openMenu(player);
    }
}
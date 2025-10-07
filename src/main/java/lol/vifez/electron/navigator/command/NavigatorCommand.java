package lol.vifez.electron.navigator.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.Practice;
import lol.vifez.electron.navigator.menu.NavigatorMenu;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@CommandAlias("navigator|nav|navbar")
public class NavigatorCommand extends BaseCommand {

    private final Practice instance = Practice.getInstance();

    @Default
    public void openNavigator(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (profile == null) {
            CC.sendMessage(player, "&cYour profile is not loaded.");
            return;
        }

        new NavigatorMenu(instance).openMenu(player);
    }
}
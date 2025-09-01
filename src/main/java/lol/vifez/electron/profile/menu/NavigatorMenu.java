package lol.vifez.electron.profile.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.duel.menu.DuelPlayerMenu;
import lol.vifez.electron.kit.menu.kiteditor.KitSelectionMenu;
import lol.vifez.electron.leaderboard.menu.LeaderboardMenu;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.profile.menu.settings.SettingsMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class NavigatorMenu extends Menu {

    private final Practice instance;

    public NavigatorMenu(Practice instance) {
        this.instance = instance;
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&7Navigation");
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new EasyButton(new ItemBuilder(Material.NETHER_STAR)
                .name("&bProfile")
                .lore("&7View your profile.",
                        "&r",
                        "&aLeft-click to view your &eprofile.")
                .build(), true, false, () -> {
            Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (targetProfile != null) {
                new ProfileMenu(targetProfile).openMenu(player);
            } else {
                CC.sendMessage(player, "&cProfile not found.");
            }
        }));

        buttons.put(13, new EasyButton(new ItemBuilder(Material.EMERALD)
                .name("&bLeaderboards")
                .lore("&7View global leaderboards!",
                        "&r",
                        "&aLeft-click to view &eLeaderboards.")
                .build(), true, false, () -> {
            new LeaderboardMenu(instance).openMenu(player);
        }));

        buttons.put(15, new EasyButton(new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("&bSettings")
                .lore("&7Toggle scoreboard, world time, private messages",
                        "&7and more!",
                        "&r",
                        "&aLeft-click to view &esettings.")
                .build(), true, false, () -> {
            Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
            if (profile != null) {
                new SettingsMenu(Practice.getInstance(), profile).openMenu(player);
            } else {
                CC.sendMessage(player, "&cYour profile is not loaded.");
            }
        }));

        buttons.put(30, new EasyButton(new ItemBuilder(Material.DIAMOND_SWORD)
                .name("&bDuel")
                .lore("&7Opens a menu of players you can duel.",
                        "&r",
                        "&aLeft-click to &eduel.")
                .build(), true, false, () -> {
            new DuelPlayerMenu().openMenu(player);
        }));

        buttons.put(32, new EasyButton(new ItemBuilder(Material.BOOK_AND_QUILL)
                .name("&bLoadout Editor")
                .lore("&7Customize your kit loadout!",
                        "&r",
                        "&aLeft-click to &eedit layouts.")
                .build(), true, false, () -> {
            new KitSelectionMenu(instance).openMenu(player);
        }));

        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) {
                buttons.put(i, new EasyButton(new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability((short) 7)
                        .name(" ")
                        .build(), true, false, () -> {}));
            }
        }

        return buttons;
    }
}
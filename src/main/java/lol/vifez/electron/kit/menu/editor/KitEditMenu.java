package lol.vifez.electron.kit.menu.editor;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.hotbar.Hotbar;
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

public class KitEditMenu extends Menu {

    private final Kit kit;

    public KitEditMenu(Kit kit) {
        this.kit = kit;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Editing... [" + kit.getName() + "]";
    }

    @Override
    public void onOpen(Player player) {
        Practice plugin = Practice.getInstance();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        profile.setEditMode(true);

        CC.sendMessage(player, "&aYou are now editing " + kit.getColor() + kit.getName() + " &alayout");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Practice plugin = Practice.getInstance();
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        buttons.put(11, new EasyButton(new ItemBuilder(Material.INK_SACK)
                .name("&2&lConfirm")
                .durability((short) 10)
                .lore("&r", "", "&fKit: &b" + kit.getName(), "&fClick to save your kit changes")
                .build(), true, false, () -> {
            profile.getKitLoadout().put(kit.getName().toLowerCase(), player.getInventory().getContents());
            player.closeInventory();
            profile.setEditMode(false);
            player.getInventory().setContents(Hotbar.getSpawnItems());

            CC.sendMessage(player, "&aSuccessfully saved " + kit.getColor() + kit.getName() + " &alayout");
        }));

        buttons.put(13, new EasyButton(new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .name("&e&lRestore default")
                .lore("&r", "", "&fKit: &b" + kit.getName(), "&fClick to restore kit to default")
                .build(), true, false, () -> {
            CC.sendMessage(player, "&eRestored kit to default");
            player.getInventory().setContents(kit.getContents());
        }));

        buttons.put(15, new EasyButton(new ItemBuilder(Material.INK_SACK)
                .name("&c&lCancel")
                .durability((short) 1)
                .lore("&r", "", "&fKit: &b" + kit.getName(), "&fClick to cancel your changes")
                .build(), true, false, () -> {
            profile.setEditMode(false);
            CC.sendMessage(player, "&cCancelled layout changes");
            player.closeInventory();
            player.getInventory().setContents(Hotbar.getSpawnItems());
        }));

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
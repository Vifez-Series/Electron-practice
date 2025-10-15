package lol.vifez.electron.kit.menu.editor;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.hotbar.Hotbar;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KitEditMenu extends Menu {

    private final Kit kit;

    public KitEditMenu(Kit kit) {
        this.kit = kit;
    }

    @Override
    public String getTitle(Player player) {
        return "&7Editing...";
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

        buttons.put(11, new EasyButton(new ItemBuilder(Material.WOOL)
                .name("&2&lSave & Exit")
                .durability((short) 13)
                .lore("&r", "&7Click here to save this layout and exit the menu")
                .build(), true, false, () -> {
            profile.getKitLoadout().put(kit.getName().toLowerCase(), player.getInventory().getContents());
            player.closeInventory();
            profile.setEditMode(false);
            player.getInventory().setContents(Hotbar.getSpawnItems());

            CC.sendMessage(player, "&aSuccessfully saved " + kit.getColor() + kit.getName() + " &alayout");
        }));

        buttons.put(13, new EasyButton(new ItemBuilder(Material.WOOL)
                .name("&e&lReset")
                .durability((short) 4)
                .lore("&r", "&7Click here to reset this layout to the default")
                .build(), true, false, () -> {
            player.getInventory().setContents(kit.getContents());
        }));

        buttons.put(15, new EasyButton(new ItemBuilder(Material.WOOL)
                .name("&c&lCancel & Exit")
                .durability((short) 14)
                .lore("&r", "&7Click here to exit the menu and not save the changes")
                .build(), true, false, () -> {
            profile.setEditMode(false);
            CC.sendMessage(player, "&cCancelled layout changes");
            player.closeInventory();
            player.getInventory().setContents(Hotbar.getSpawnItems());
        }));

        return buttons;
    }
}
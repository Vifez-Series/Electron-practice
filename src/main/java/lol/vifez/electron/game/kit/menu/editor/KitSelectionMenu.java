package lol.vifez.electron.game.kit.menu.editor;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class KitSelectionMenu extends Menu {

    private final Practice instance;

    @Override
    public String getTitle(Player p0) {
        return "&7Kit Editor";
    }

    @Override
    public Map<Integer, Button> getButtons(Player p0) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (Kit kit : instance.getKitManager().getKits().values()) {
            buttons.put(i, new KitButton(this, instance, p0, kit));
            i++;
        }

        return buttons;
    }
}

class KitButton extends EasyButton {

    public KitButton(KitSelectionMenu menu, Practice instance, Player player, Kit kit) {
        super(new ItemBuilder(kit.getDisplayItem())
                .name(kit.getColor() + kit.getName())
                .lore("&r", "&aClick here to edit " + kit.getColor() + kit.getName() + "'s &alayout!")
                .build(), true, false, () -> {
            Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());

            if (profile.getKitLoadout().get(kit.getName().toLowerCase()) == null) {
                player.getInventory().setContents(kit.getContents());
            } else {
                player.getInventory().setContents(profile.getKitLoadout().get(kit.getName().toLowerCase()));
            }

            menu.setClosedByMenu(true);

            player.closeInventory();
            new KitEditMenu(kit).openMenu(player);
        });
    }
}
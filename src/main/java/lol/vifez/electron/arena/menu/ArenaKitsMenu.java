package lol.vifez.electron.arena.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.arena.Arena;
import lol.vifez.electron.arena.ArenaManager;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class ArenaKitsMenu extends Menu {

    private final Arena arena;
    private final ArenaManager manager;

    public ArenaKitsMenu(Arena arena, ArenaManager manager) {
        this.arena = arena;
        this.manager = manager;
    }

    @Override
    public String getTitle(Player player) {
        return "&8Manage Kits &7(" + arena.getName() + ")";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();

        List<Kit> kits = new ArrayList<>(Practice.getInstance().getKitManager().getKits().values());

        int slot = 10;
        for (Kit kit : kits) {
            boolean has = arena.getKits().contains(kit.getName().toLowerCase());
            map.put(slot, new EasyButton(
                    new ItemBuilder(has ? Material.EMERALD : Material.REDSTONE)
                            .name("&b" + kit.getName())
                            .lore(Arrays.asList(
                                    "&7Status: " + (has ? "&aAdded" : "&cNot Added"),
                                    " ",
                                    "&fClick to toggle."
                            ))
                            .build(),
                    true,false,() -> {
                if (has) {
                    arena.getKits().remove(kit.getName().toLowerCase());
                    player.sendMessage(CC.translate("&cRemoved &b" + kit.getName() + " &cfrom " + arena.getName()));
                } else {
                    arena.getKits().add(kit.getName().toLowerCase());
                    player.sendMessage(CC.translate("&aAdded &b" + kit.getName() + " &ato " + arena.getName()));
                }
                new ArenaKitsMenu(arena, manager).openMenu(player);
            }
            ));

            slot++;
            if ((slot + 1) % 9 == 0) slot += 2;
        }

        map.put(49, new EasyButton(
                new ItemBuilder(Material.ARROW)
                        .name("&cGo back")
                        .build(),
                true,false,() -> new ArenaEditorMenu(arena, manager).openMenu(player)
        ));

        int[] border = {0,1,2,3,4,5,6,7,8,45,46,47,48,50,51,52,53};
        for (int i : border) {
            map.put(i, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short)15).name("&7").build(),
                    true,false,()->{}
            ));
        }

        return map;
    }
}
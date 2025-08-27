package lol.vifez.electron.arena.menu;

import lol.vifez.electron.arena.Arena;
import lol.vifez.electron.arena.manager.ArenaManager;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

@RequiredArgsConstructor
public class ArenasMenu extends Menu {

    private final ArenaManager arenaManager;

    @Override
    public String getTitle(Player player) {
        return "&7Arenas";
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        List<Arena> arenas = new ArrayList<>(arenaManager.getArenas());

        for (int i = 0; i < arenas.size(); i++) {
            Arena arena = arenas.get(i);
            buttons.put(10 + i, new EasyButton(
                    new ItemBuilder(Material.PAPER)
                            .name("&b&l⚔ " + arena.getName())
                            .lore(getArenaLore(arena))
                            .build(),
                    true, false, () -> {
            }
            ));
        }

        int[] borderSlots = {
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 26, 27, 35,
                36, 37, 38, 39, 40, 41, 42, 43, 44
        };

        for (int slot : borderSlots) {
            buttons.put(slot, new EasyButton(
                    new ItemBuilder(Material.STAINED_GLASS_PANE)
                            .durability((short) 15)
                            .name("&7")
                            .build(),
                    true, false, () -> {}
            ));
        }

        return buttons;
    }

    private List<String> getArenaLore(Arena arena) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add("&7Type: &b" + arena.getType());
        lore.add("&7Spawn A: " + formatStatus(arena.getSpawnA() != null));
        lore.add("&7Spawn B: " + formatStatus(arena.getSpawnB() != null));
        lore.add("&7Kits: " + (arena.getKits().isEmpty() ? "&cNone" : "&a" + String.join(", ", arena.getKits())));
        lore.add("&7Status: " + (arena.isBusy() ? "&cBusy" : "&aAvailable"));
        return lore;
    }

    private String formatStatus(boolean status) {
        return status ? "&a✔ Set" : "&cNot Set";
    }
}
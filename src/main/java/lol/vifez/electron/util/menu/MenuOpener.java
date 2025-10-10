package lol.vifez.electron.util.menu;

import org.bukkit.entity.Player;

public interface MenuOpener {
    void openMenu(Player player);
    
    default void open(Player player) {
        openMenu(player);
    }
}
package lol.vifez.electron.util.menu.button.impl;


import lol.vifez.electron.util.ItemBuilder;
import org.bukkit.Material;

public class GlassButton extends EasyButton {

    public GlassButton() {
        super(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .durability((short) 15)
                .name(" ")
                .build(), true, false, () -> {}, null);
    }
}
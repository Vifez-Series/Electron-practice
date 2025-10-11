package lol.vifez.electron.util.menu.button.impl;

import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.kit.enums.KitType;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class EnumButton extends Button {

    private final ItemStack item;
    private final Kit kit;
    private final KitType target;
    private final BiConsumer<KitType, Boolean> writeFunction;
    private final Function<KitType, Boolean> readFunction;

    public EnumButton(Kit kit, KitType target, BiConsumer<KitType, Boolean> writeFunction, Function<KitType, Boolean> readFunction) {
        this.item = new ItemBuilder(Material.PAPER).name("&bKit Type Selection").build();
        this.kit = kit;
        this.target = target;
        this.writeFunction = writeFunction;
        this.readFunction = readFunction;
    }

    @Override
    public ItemStack getItem(Player player) {
        List<String> list = new ArrayList<>();

        list.add("&r");

        for (KitType type : target.getAll()) {
            list.add((kit.getKitType() == type ? "&a" : "&7") + type.name());
        }

        list.add("&r");
        list.add("&aClick to change the kit type!");

        return new ItemBuilder(this.item)
                .lore(list)
                .build();
    }

    @Override
    public void onClick(Player player, int slot, ClickType type) {
        boolean current = this.readFunction.apply(this.target);
        this.writeFunction.accept(this.target, !current);
    }
}
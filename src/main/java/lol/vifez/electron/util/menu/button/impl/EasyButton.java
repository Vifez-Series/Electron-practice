package lol.vifez.electron.util.menu.button.impl;

import lol.vifez.electron.util.menu.button.Button;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/*
 * Copyright (c) 2025 MTR. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

@RequiredArgsConstructor
public class EasyButton extends Button {

    private final ItemStack item;
    private boolean cancelEvent = false, updateOnClick = false;
    protected final Runnable onClick;
    private Runnable onRightClick;

    public EasyButton(ItemStack item, boolean cancelEvent, boolean updateOnClick, Runnable onClick, Runnable onRightClick) {
        this.item = item;
        this.cancelEvent = cancelEvent;
        this.updateOnClick = updateOnClick;
        this.onClick = onClick;
        this.onRightClick = onRightClick;
    }

    @Override
    public ItemStack getItem(Player player) {
        return item;
    }

    @Override
    public void onClick(Player player, int slot, ClickType type) {
        if (type == ClickType.RIGHT && onRightClick != null) {
            onRightClick.run();
        } else {
            onClick.run();
        }
    }

    @Override
    public boolean cancelEvent(Player player, int slot, ClickType type) {
        return cancelEvent;
    }

    @Override
    public boolean updateOnClick(Player player, int slot, ClickType type) {
        return updateOnClick;
    }
}

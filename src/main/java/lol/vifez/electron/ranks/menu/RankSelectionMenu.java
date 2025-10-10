package lol.vifez.electron.ranks.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.ranks.Rank;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankSelectionMenu extends Menu {
    
    private final Practice practice;

    public RankSelectionMenu(Practice practice) {
        this.practice = practice;
    }

    @Override
    public String getTitle(Player player) {
        return "Ranks";
    }

    @Override
    public Map<Integer, lol.vifez.electron.util.menu.button.Button> getButtons(Player player) {
        Map<Integer, lol.vifez.electron.util.menu.button.Button> buttons = new HashMap<>();
        
        int slot = 10;
        for (Rank rank : practice.getRankManager().getRanks()) {
            if (slot > 16) break;
            
            final Rank buttonRank = rank;
            buttons.put(slot++, new lol.vifez.electron.util.menu.button.Button() {
                @Override
                public ItemStack getItem(Player player) {
                    ItemStack item = new ItemStack(Material.NAME_TAG);
                    List<String> lore = new ArrayList<>();
                    lore.add(CC.translate("&7Weight: &f" + buttonRank.getWeight()));
                    lore.add(CC.translate("&7Permission: &f" + buttonRank.getPermission()));
                    lore.add(CC.translate("&7Default: &f" + buttonRank.isDefaultRank()));
                    lore.add("");
                    lore.add(CC.translate("&eClick to edit this rank"));
                    
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(CC.translate(buttonRank.getDisplayName()));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    return item;
                }

                @Override
                public void onClick(Player player, int slot, ClickType type) {
                    new RankEditMenu(practice, buttonRank).openMenu(player);
                }
            });
        }
        
        return buttons;
    }
}
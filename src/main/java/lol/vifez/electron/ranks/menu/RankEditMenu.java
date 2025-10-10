package lol.vifez.electron.ranks.menu;

import lol.vifez.electron.Practice;
import lol.vifez.electron.ranks.Rank;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.menu.Menu;
import lol.vifez.electron.util.menu.button.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankEditMenu extends Menu {
    
    private final Practice practice;
    private final Rank rank;

    public RankEditMenu(Practice practice, Rank rank) {
        this.practice = practice;
        this.rank = rank;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing: " + rank.getDisplayName();
    }

    @Override
    public Map<Integer, lol.vifez.electron.util.menu.button.Button> getButtons(Player player) {
        Map<Integer, lol.vifez.electron.util.menu.button.Button> buttons = new HashMap<>();

        buttons.put(11, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemStack item = new ItemStack(Material.NAME_TAG);
                List<String> lore = new ArrayList<>();
                lore.add(CC.translate("&7Current prefix: " + rank.getFormattedPrefix()));
                lore.add("");
                lore.add(CC.translate("&eClick to edit prefix"));
                
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(CC.translate("&bPrefix"));
                meta.setLore(lore);
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public void onClick(Player player, int slot, ClickType type) {
                player.closeInventory();
                player.sendMessage(CC.translate("&ePlease type the new prefix for " + rank.getDisplayName() + " &ein chat."));
                player.sendMessage(CC.translate("&7(Use '&' for color codes, type 'cancel' to cancel)"));
                
                Practice.getInstance().getConversationManager().startConversation(player, (msg) -> {
                    if (msg.equalsIgnoreCase("cancel")) {
                        player.sendMessage(CC.translate("&cPrefix edit cancelled."));
                        openMenu(player);
                        return;
                    }
                    
                    rank.setPrefix(msg);
                    practice.getRankManager().saveRanks();
                    player.sendMessage(CC.translate("&aPrefix updated successfully!"));
                    openMenu(player);
                });
            }
        });

        buttons.put(13, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemStack item = new ItemStack(Material.WOOL, 1, (short) 3);
                List<String> lore = new ArrayList<>();
                lore.add(CC.translate("&7Current color: " + rank.getColor()));
                lore.add("");
                lore.add(CC.translate("&eClick to change color"));
                
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(CC.translate("&bColor"));
                meta.setLore(lore);
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public void onClick(Player player, int slot, ClickType type) {
                player.closeInventory();
                player.sendMessage(CC.translate("&ePlease type the new color code for " + rank.getDisplayName() + " &ein chat."));
                player.sendMessage(CC.translate("&7(Use '&' color codes, type 'cancel' to cancel)"));
                player.sendMessage(CC.translate("&7Available colors: &0&l■ &1&l■ &2&l■ &3&l■ &4&l■ &5&l■ &6&l■ &7&l■ &8&l■ &9&l■ &a&l■ &b&l■ &c&l■ &d&l■ &e&l■ &f&l■"));
                
                Practice.getInstance().getConversationManager().startConversation(player, (msg) -> {
                    if (msg.equalsIgnoreCase("cancel")) {
                        player.sendMessage(CC.translate("&cColor edit cancelled."));
                        openMenu(player);
                        return;
                    }
                    
                    rank.setColor(msg);
                    practice.getRankManager().saveRanks();
                    player.sendMessage(CC.translate("&aColor updated successfully!"));
                    openMenu(player);
                });
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getItem(Player player) {
                ItemStack item = new ItemStack(Material.PAPER);
                List<String> lore = new ArrayList<>();
                lore.add(CC.translate("&7Weight: &f" + rank.getWeight()));
                lore.add("");
                lore.add(CC.translate("&eLeft-Click to increase"));
                lore.add(CC.translate("&eRight-Click to decrease"));
                
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(CC.translate("&bWeight"));
                meta.setLore(lore);
                item.setItemMeta(meta);
                return item;
            }

            @Override
            public void onClick(Player player, int slot, ClickType type) {
                if (type == ClickType.LEFT) {
                    rank.setWeight(rank.getWeight() + 1);
                } else if (type == ClickType.RIGHT) {
                    rank.setWeight(Math.max(0, rank.getWeight() - 1));
                }
                practice.getRankManager().loadRanks();
                openMenu(player);
            }
        });

        return buttons;
    }

}
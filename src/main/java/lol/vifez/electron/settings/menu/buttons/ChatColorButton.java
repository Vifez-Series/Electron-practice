package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ChatColorButton {

    public static Button createChatColorButton(Profile profile, Practice instance) {
        ChatColor currentColor = profile.getChatColor() != null ? profile.getChatColor() : ChatColor.WHITE;
        ItemStack colorItem = new ItemBuilder(Material.INK_SACK)
                .durability(getDyeColor(currentColor))
                .name(CC.translate("&eChat Color"))
                .lore(Arrays.asList(
                        CC.translate("&7Change your chat message color."),
                        CC.translate("&7Current color: " + currentColor + "Example"),
                        CC.translate("&r"),
                        CC.translate("&eClick to cycle through colors!")
                ))
                .build();

        return new EasyButton(colorItem, true, false, () -> {
            Player player = profile.getPlayer();
            if (!player.hasPermission("electron.rank.vip")) {
                CC.sendMessage(player, "&cYou need VIP rank to use chat colors!");
                player.closeInventory();
                return;
            }
            ChatColor nextColor = getNextColor(currentColor);
            profile.setChatColor(nextColor);
            Practice.getInstance().getProfileManager().save(profile);
            CC.sendMessage(player, "&aChat color set to: " + nextColor + "Example");
            new SettingsMenu(instance, profile).openMenu(player);
        }, null);
    }

    private static ChatColor getNextColor(ChatColor current) {
        ChatColor[] colors = {
            ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GREEN, 
            ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.RED, 
            ChatColor.GRAY
        };
        
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == current) {
                return colors[(i + 1) % colors.length];
            }
        }
        return ChatColor.WHITE;
    }

    private static short getDyeColor(ChatColor color) {
        switch (color) {
            case WHITE: return 15;
            case YELLOW: return 11;
            case GREEN: return 10;
            case AQUA: return 12;
            case LIGHT_PURPLE: return 13;
            case RED: return 1;
            case GRAY: return 8;
            default: return 15;
        }
    }
}
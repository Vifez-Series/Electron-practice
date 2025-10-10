package lol.vifez.electron.chat;

import lol.vifez.electron.Practice;
import lol.vifez.electron.ranks.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author aysha
 * @project Electron
 * @website https://vifez.lol
 */

public class ChatFormat {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private final Practice plugin;
    
    public ChatFormat(Practice plugin) {
        this.plugin = plugin;
    }

    public String format(String message, Player player) {
        // Get player's rank information
        Rank rank = plugin.getRankManager().getPlayerRank(player);
        String prefix = "";
        String playerName = player.getName();
        
        // Apply rank formatting if available
        if (rank != null) {
            prefix = rank.getFormattedPrefix();
            playerName = rank.getColor() + playerName;
        } else {
            // Get default settings from config
            String defaultColor = plugin.getConfig().getString("chat.default.name-color", "&7");
            String defaultPrefix = plugin.getConfig().getString("chat.default.prefix", "");
            prefix = defaultPrefix;
            playerName = defaultColor + playerName;
        }

        // Get chat format from config, default to simple format if not found
        String format = plugin.getConfig().getString("chat.format", "{prefix}{name}&7: {message}");
        
        // Replace placeholders
        format = format
            .replace("{prefix}", prefix)
            .replace("{name}", playerName)
            .replace("{displayname}", player.getDisplayName())
            .replace("{world}", player.getWorld().getName());
            
        // Handle message colors based on permissions
        if (player.hasPermission("practice.chat.color")) {
            if (player.hasPermission("practice.chat.hex")) {
                message = translateHexColorCodes(colorize(message));
            } else {
                message = colorize(message);
            }
        }
        
        // Add message to format
        format = format.replace("{message}", message);
        
        // Process final colors
        return translateHexColorCodes(colorize(format));
    }
    
    /**
     * Translates color codes using &
     */
    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Translates hex color codes using &#RRGGBB format
     */
    private String translateHexColorCodes(String message) {
        final char COLOR_CHAR = '§';
        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 32);
        
        while (matcher.find()) {
            final String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x" 
                + COLOR_CHAR + group.charAt(0) 
                + COLOR_CHAR + group.charAt(1)
                + COLOR_CHAR + group.charAt(2)
                + COLOR_CHAR + group.charAt(3)
                + COLOR_CHAR + group.charAt(4)
                + COLOR_CHAR + group.charAt(5));
        }
        
        return matcher.appendTail(buffer).toString();
    }
}

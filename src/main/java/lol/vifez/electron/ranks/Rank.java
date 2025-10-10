package lol.vifez.electron.ranks;

import lombok.Data;
import org.bukkit.ChatColor;

@Data
public class Rank {
    private final String name;
    private String prefix;
    private String color;
    private String permission;
    private int weight;
    private boolean defaultRank;

    public String getFormattedPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getColoredName() {
        return ChatColor.translateAlternateColorCodes('&', color + name);
    }

    public String getDisplayName() {
        return getFormattedPrefix() + getColoredName();
    }

    public Rank(String name, String prefix, String color, String permission, int weight, boolean defaultRank) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.permission = permission;
        this.weight = weight;
        this.defaultRank = defaultRank;
    }
}

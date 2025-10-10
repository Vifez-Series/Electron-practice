package lol.vifez.electron.ranks;

import lol.vifez.electron.Practice;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankManager {
    
    @Getter
    private final List<Rank> ranks;
    private final Practice practice;
    private final File ranksFile;
    private FileConfiguration config;

    public RankManager(Practice practice) {
        this.practice = practice;
        this.ranks = new ArrayList<>();
        this.ranksFile = new File(practice.getDataFolder(), "ranks.yml");
        loadRanks();
    }

    public void loadRanks() {
        if (!ranksFile.exists()) {
            practice.saveResource("ranks.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(ranksFile);
        ranks.clear();

        ConfigurationSection ranksSection = config.getConfigurationSection("ranks");
        if (ranksSection != null) {
            for (String key : ranksSection.getKeys(false)) {
                ConfigurationSection rankSection = ranksSection.getConfigurationSection(key);
                if (rankSection != null) {
                    ranks.add(new Rank(
                            key,
                            rankSection.getString("prefix", ""),
                            rankSection.getString("color", "&7"),
                            rankSection.getString("permission", "electron.rank." + key.toLowerCase()),
                            rankSection.getInt("weight", 0),
                            rankSection.getBoolean("default", false)
                    ));
                }
            }
        }

        ranks.sort(Comparator.comparingInt(Rank::getWeight).reversed());
    }

    public Rank getDefaultRank() {
        return ranks.stream()
                .filter(Rank::isDefaultRank)
                .findFirst()
                .orElse(null);
    }

    public Rank getRank(String name) {
        return ranks.stream()
                .filter(rank -> rank.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Rank getPlayerRank(Player player) {
        return ranks.stream()
                .filter(rank -> player.hasPermission(rank.getPermission()))
                .findFirst()
                .orElse(getDefaultRank());
    }

    public void saveRanks() {
        ConfigurationSection ranksSection = config.createSection("ranks");
        for (Rank rank : ranks) {
            ConfigurationSection rankSection = ranksSection.createSection(rank.getName());
            rankSection.set("prefix", rank.getPrefix());
            rankSection.set("color", rank.getColor());
            rankSection.set("permission", rank.getPermission());
            rankSection.set("weight", rank.getWeight());
            rankSection.set("default", rank.isDefaultRank());
        }
        
        try {
            config.save(ranksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
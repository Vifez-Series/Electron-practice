package lol.vifez.electron.ui.scoreboard;

import lol.vifez.electron.Practice;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class ScoreboardConfig {

    private FileConfiguration config;
    private File configFile;

    public ScoreboardConfig() {
        load();
    }

    public void load() {
        Practice plugin = Practice.getInstance();
        configFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!configFile.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
}
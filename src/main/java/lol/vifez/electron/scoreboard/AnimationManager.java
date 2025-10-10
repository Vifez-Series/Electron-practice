package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class AnimationManager {

    @Getter
    private final Map<String, Animation> animations;

    public AnimationManager() {
        this.animations = new ConcurrentHashMap<>();
        
        ScoreboardConfig scoreboardConfig = Practice.getInstance().getScoreboardConfig();
        List<String> scoreboardFrames = scoreboardConfig.getStringList("scoreboard.ANIMATION.LINES");
        int scoreboardInterval = scoreboardConfig.getInt("scoreboard.ANIMATION.INTERVAL");
        animations.put("scoreboard", new Animation(scoreboardFrames, scoreboardInterval));

        File tabFile = new File(Practice.getInstance().getDataFolder(), "tab.yml");
        FileConfiguration tabConfig = YamlConfiguration.loadConfiguration(tabFile);
        ConfigurationSection tabAnimations = tabConfig.getConfigurationSection("ANIMATIONS");
        if (tabAnimations != null) {
            for (String key : tabAnimations.getKeys(false)) {
                loadAnimationsFromConfig(tabConfig, "ANIMATIONS." + key);
            }
        }

        startAnimationTasks();
    }

    private void loadAnimationsFromConfig(FileConfiguration config, String path) {
        if (!config.contains(path + ".LINES")) return;
        
        List<String> frames = config.getStringList(path + ".LINES");
        int interval = config.getInt(path + ".INTERVAL", 20);
        String animationName = path.substring(path.lastIndexOf('.') + 1);
        
        animations.put(animationName.toLowerCase(), new Animation(frames, interval));
    }

    private void startAnimationTasks() {
        animations.forEach((name, animation) -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (animation.getFrames().isEmpty()) return;
                    animation.incrementIndex();
                }
            }.runTaskTimer(Practice.getInstance(), animation.getInterval(), animation.getInterval());
        });
    }

    public String getCurrentFrame(String animationName) {
        Animation animation = animations.get(animationName.toLowerCase());
        if (animation == null || animation.getFrames().isEmpty()) return "";
        return animation.getCurrentFrame();
    }

    @Getter
    private static class Animation {
        private final List<String> frames;
        private final int interval;
        private int currentIndex = 0;

        public Animation(List<String> frames, int interval) {
            this.frames = frames;
            this.interval = interval;
        }

        public String getCurrentFrame() {
            return frames.get(currentIndex);
        }

        public void incrementIndex() {
            currentIndex = (currentIndex + 1) % frames.size();
        }
    }
}
package lol.vifez.electron.ui.scoreboard;

import lol.vifez.electron.Practice;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class AnimationManager {

    @Getter
    private final List<String> frames;
    private final int interval;
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    public AnimationManager() {
        ScoreboardConfig config = Practice.getInstance().getScoreboardConfig();
        frames = config.getStringList("scoreboard.ANIMATION.LINES");
        interval = config.getInt("scoreboard.ANIMATION.INTERVAL");

        startAnimationTask();
    }

    private void startAnimationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (frames.isEmpty()) return;
                currentIndex.set((currentIndex.get() + 1) % frames.size());
            }
        }.runTaskTimer(Practice.getInstance(), interval, interval);
    }

    public String getCurrentFrame() {
        if (frames.isEmpty()) return "";
        return frames.get(currentIndex.get());
    }
}
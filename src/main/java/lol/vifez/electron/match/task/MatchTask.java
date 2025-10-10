package lol.vifez.electron.match.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.match.MatchManager;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.util.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@RequiredArgsConstructor
public class MatchTask extends BukkitRunnable {

    private final MatchManager matchHandler;

    @Override
    public void run() {
        matchHandler.getMatches().values().forEach(match -> {
            if (match.getMatchState() != MatchState.STARTING) return;
            if (match.isCountdownRunning()) return;

            match.setCountdownRunning(true);

            new BukkitRunnable() {
                int countdown = match.getCountdownTime();

                @Override
                public void run() {
                    if (countdown > 0) {
                        Arrays.asList(match.getPlayerOne(), match.getPlayerTwo()).forEach(profile -> {
                            profile.getPlayer().sendMessage(
                                    CC.colorize("&7Match Starting In &b" + countdown + "s")
                            );
                            profile.getPlayer().playSound(
                                    profile.getPlayer().getLocation(),
                                    Sound.NOTE_PIANO, 0.5f, 0.5f
                            );
                        });
                        countdown--;
                    } else {
                        match.setMatchState(MatchState.STARTED);

                        Arrays.asList(match.getPlayerOne(), match.getPlayerTwo()).forEach(profile -> {
                            profile.getPlayer().playSound(
                                    profile.getPlayer().getLocation(),
                                    Sound.NOTE_PLING, 0.5f, 0.5f
                            );
                        });

                        Bukkit.getScheduler().runTask(Practice.getInstance(), () -> {
                            match.allowMovement(match.getPlayerOne().getPlayer());
                            match.allowMovement(match.getPlayerTwo().getPlayer());
                        });

                        match.setCountdownRunning(false);
                        this.cancel();
                    }
                }
            }.runTaskTimer(Practice.getInstance(), 0L, 20L);
        });
    }
}
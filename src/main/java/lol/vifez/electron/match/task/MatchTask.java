package lol.vifez.electron.match.task;

import com.cryptomorin.xseries.messages.Titles;
import lol.vifez.electron.Practice;
import lol.vifez.electron.match.MatchManager;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class MatchTask extends BukkitRunnable {

    private final MatchManager matchHandler;

    @Override
    public void run() {
        matchHandler.getMatches().values().forEach(match -> {
            Profile[] profiles = {match.getPlayerOne(), match.getPlayerTwo()};

            if (match.getCountdownTime() > 0) {
                match.setCountdownTime(match.getCountdownTime() - 1);
            }

            if (match.getCountdownTime() <= 0 && match.getMatchState() == MatchState.STARTING) {
                match.setMatchState(MatchState.STARTED);

                Arrays.stream(profiles).forEach(profile -> {
                    Player player = profile.getPlayer();

                    Titles.sendTitle(player, CC.colorize("&a&lMatch Started!"), "");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.5f, 0.5f);
                });

                Bukkit.getScheduler().runTask(Practice.getInstance(), () -> {
                    match.allowMovement(match.getPlayerTwo().getPlayer());
                    match.allowMovement(match.getPlayerOne().getPlayer());
                });
            }

            if (match.getMatchState() == MatchState.STARTING && match.getCountdownTime() <= 5) {
                Arrays.stream(profiles).forEach(profile -> {
                    Player player = profile.getPlayer();

                    Titles.sendTitle(player, CC.colorize("&7Match Starting In &b" + match.getCountdownTime()), "");
                    player.playSound(player.getLocation(), Sound.NOTE_PIANO, 0.5F, 0.5F);
                });
            }
        });
    }
}
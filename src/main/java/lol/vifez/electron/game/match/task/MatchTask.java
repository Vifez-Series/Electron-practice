package lol.vifez.electron.game.match.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.game.match.MatchManager;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.game.match.event.MatchStartEvent;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class MatchTask extends BukkitRunnable {

    private final MatchManager matchHandler;

    public MatchTask(MatchManager matchHandler) {
        this.matchHandler = matchHandler;
    }

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
                    match.setCurrentCountdown(countdown);

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
                        match.setCurrentCountdown(0);
                        match.setMatchState(MatchState.STARTED);

                        Arrays.asList(match.getPlayerOne(), match.getPlayerTwo()).forEach(profile -> {
                            if (profile.getKitLoadout().containsKey(match.getKit().getName().toLowerCase())) {
                                profile.getPlayer().getInventory().setContents(
                                        profile.getKitLoadout().get(match.getKit().getName().toLowerCase())
                                );
                            } else {
                                profile.getPlayer().getInventory().setContents(match.getKit().getContents());
                            }

                            profile.getPlayer().getInventory().setArmorContents(match.getKit().getArmorContents());
                            profile.getPlayer().updateInventory();

                            match.allowMovement(profile.getPlayer());

                            profile.getPlayer().sendMessage(CC.colorize("&aMatch started!"));
                            profile.getPlayer().playSound(
                                    profile.getPlayer().getLocation(),
                                    Sound.NOTE_PLING, 1.0f, 1.0f
                            );
                        });

                        Bukkit.getPluginManager().callEvent(
                                new MatchStartEvent(
                                        match.getPlayerOne(), match.getPlayerTwo(), match
                                )
                        );

                        match.setCountdownRunning(false);
                        this.cancel();
                    }
                }
            }.runTaskTimer(Practice.getInstance(), 0L, 20L);
        });
    }
}
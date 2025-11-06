package lol.vifez.electron.scoreboard;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.assemble.AssembleAdapter;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.elo.EloUtil;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class PracticeScoreboard implements AssembleAdapter {

    private final AtomicInteger titleIndex = new AtomicInteger(0);
    private final ScoreboardConfig scoreboardConfig;
    private final AnimationManager animationManager;

    public PracticeScoreboard() {
        this.scoreboardConfig = Practice.getInstance().getScoreboardConfig();
        this.animationManager = new AnimationManager();
    }

    @Override
    public String getTitle(Player player) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        if (!profile.isScoreboardEnabled() || !scoreboardConfig.getBoolean("scoreboard.enabled")) return "";
        String title = scoreboardConfig.getString("scoreboard.title");
        if (title == null) return "";
        return title.replace("%animation%", animationManager.getCurrentFrame());
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> list = new ArrayList<>();
        Practice plugin = Practice.getInstance();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (!profile.isScoreboardEnabled() || !scoreboardConfig.getBoolean("scoreboard.enabled")) return list;

        String footer = scoreboardConfig.getString("scoreboard.footer");
        String globalElo = String.valueOf(EloUtil.getGlobalElo(profile));
        String division = profile.getDivision().getPrettyName();

        Match match = plugin.getMatchManager().getMatch(profile.getUuid());

        List<String> template;

        if (match != null) {
            int hits = match.getHitsMap().get(profile.getUuid());

            if (match.getMatchState() == MatchState.STARTED) {
                template = match.getKit().getKitType() == KitType.BOXING
                        ? scoreboardConfig.getStringList("scoreboard.in-boxing.lines")
                        : scoreboardConfig.getStringList("scoreboard.in-game.lines");

                for (String str : template) {
                    list.add(str
                            .replace("<ping>", String.valueOf(profile.getPing()))
                            .replace("<opponent-ping>", String.valueOf(match.getOpponent(player).getPing()))
                            .replace("<opponent>", match.getOpponent(player).getName())
                            .replace("<duration>", match.getDuration())
                            .replace("<difference>", (hits < 0 ? "&c" + hits : hits == 0 ? "&e" : "&a") + hits)
                            .replace("<their-hits>", String.valueOf(match.getHitsMap().get(match.getOpponent(player).getUuid())))
                            .replace("<your-hits>", String.valueOf(hits))
                            .replace("<global-elo>", globalElo)
                            .replace("<division>", division)
                            .replace("%animation%", animationManager.getCurrentFrame())
                            .replace("<footer>", footer)
                    );
                }
            } else if (match.getMatchState() == MatchState.ENDING) {
                template = scoreboardConfig.getStringList("scoreboard.match-ending.lines");
                for (String str : template) {
                    list.add(str
                            .replace("<winner>", match.getWinner() == null ? "None" : match.getWinner().getName())
                            .replace("<loser>", match.getWinner() == null
                                    ? player.getName() + " " + match.getOpponent(player).getName()
                                    : match.getOpponent(match.getWinner().getPlayer()).getName())
                            .replace("%animation%", animationManager.getCurrentFrame())
                            .replace("<footer>", footer)
                    );
                }
            } else if (match.getMatchState() == MatchState.STARTING) {
                template = scoreboardConfig.getStringList("scoreboard.match-starting.lines");
                for (String str : template) {
                    list.add(str
                            .replace("<winner>", match.getWinner() == null ? "None" : match.getWinner().getName())
                            .replace("<starting-c>", match.getCurrentCountdown() <= 0 ? "0" : match.getCurrentCountdown() + "s")
                            .replace("<opponent>", match.getOpponent(player).getName())
                            .replace("<loser>", match.getWinner() == null
                                    ? player.getName() + " " + match.getOpponent(player).getName()
                                    : match.getOpponent(match.getWinner().getPlayer()).getName())
                            .replace("%animation%", animationManager.getCurrentFrame())
                            .replace("<footer>", footer)
                    );
                }
            }

        } else if (plugin.getQueueManager().getQueue(profile.getUuid()) != null) {
            template = scoreboardConfig.getStringList("scoreboard.in-queue.lines");
            var queue = plugin.getQueueManager().getQueue(profile.getUuid());
            Kit queueKit = queue.getKit();
            boolean isRanked = queue.isRanked();
            String typeTag = isRanked ? "&c[R]" : "&7[UR]";

            for (String str : template) {
                list.add(str.replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("<in-queue>", String.valueOf(plugin.getQueueManager().getAllQueueSize()))
                        .replace("<kit>", queueKit.getName() + " " + typeTag)
                        .replace("<time>", queue.getQueueTime(profile.getUuid()))
                        .replace("<playing>", String.valueOf(plugin.getMatchManager().getAllMatchSize()))
                        .replace("<username>", player.getName())
                        .replace("<global-elo>", globalElo)
                        .replace("<division>", division)
                        .replace("%animation%", animationManager.getCurrentFrame())
                        .replace("<footer>", footer)
                );
            }

        } else {
            template = scoreboardConfig.getStringList("scoreboard.in-lobby.lines");
            for (String str : template) {
                list.add(str.replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("<in-queue>", String.valueOf(plugin.getQueueManager().getAllQueueSize()))
                        .replace("<playing>", String.valueOf(plugin.getMatchManager().getAllMatchSize()))
                        .replace("<ping>", String.valueOf(profile.getPing()))
                        .replace("<username>", player.getName())
                        .replace("<global-elo>", globalElo)
                        .replace("<division>", division)
                        .replace("%animation%", animationManager.getCurrentFrame())
                        .replace("<footer>", footer)
                );
            }
        }

        return list;
    }
}
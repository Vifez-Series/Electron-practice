package lol.vifez.electron.scoreboard;

import lol.vifez.electron.util.assemble.AssembleAdapter;
import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.elo.EloUtil;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

public class PracticeScoreboard implements AssembleAdapter {

    private final Practice instance;
    private final AtomicInteger titleIndex = new AtomicInteger(0);
    private final ScoreboardConfig scoreboardConfig;

    public PracticeScoreboard(Practice instance) {
        this.instance = instance;
        this.scoreboardConfig = instance.getScoreboardConfig();
    }

    @Override
    public String getTitle(Player player) {
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (!profile.isScoreboardEnabled() || !scoreboardConfig.getBoolean("scoreboard.enabled")) return "";
        String title = scoreboardConfig.getString("scoreboard.title");
        return title != null ? title : "";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> list = new ArrayList<>();
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());
        if (!profile.isScoreboardEnabled() || !scoreboardConfig.getBoolean("scoreboard.enabled")) return list;

        Match match = instance.getMatchManager().getMatch(profile.getUuid());
        String globalElo = String.valueOf(EloUtil.getGlobalElo(profile));
        String division = profile.getDivision().getPrettyName();

        if (match != null) {
            int hits = match.getHitsMap().get(profile.getUuid());

            if (match.getMatchState() == MatchState.STARTED) {
                List<String> template = match.getKit().getKitType() == KitType.BOXING
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
                            .replace("<division>", division));
                }

            } else if (match.getMatchState() == MatchState.ENDING) {
                for (String str : scoreboardConfig.getStringList("scoreboard.match-ending.lines")) {
                    list.add(str
                            .replace("<winner>", match.getWinner() == null ? "None" : match.getWinner().getName())
                            .replace("<loser>", match.getWinner() == null
                                    ? player.getName() + " " + match.getOpponent(player).getName()
                                    : match.getOpponent(match.getWinner().getPlayer()).getName()));
                }

            } else if (match.getMatchState() == MatchState.STARTING) {
                for (String str : scoreboardConfig.getStringList("scoreboard.match-starting.lines")) {
                    list.add(str
                            .replace("<winner>", match.getWinner() == null ? "None" : match.getWinner().getName())
                            .replace("<loser>", match.getWinner() == null
                                    ? player.getName() + " " + match.getOpponent(player).getName()
                                    : match.getOpponent(match.getWinner().getPlayer()).getName()));
                }
            }

        } else if (instance.getQueueManager().getQueue(profile.getUuid()) != null) {
            for (String str : scoreboardConfig.getStringList("scoreboard.in-queue.lines")) {
                Kit queueKit = instance.getQueueManager().getQueue(profile.getUuid()).getKit();
                boolean isRanked = instance.getQueueManager().getQueue(profile.getUuid()).isRanked();
                String typeTag = isRanked ? "&c[R]" : "&7[UR]";

                list.add(str.replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("<in-queue>", String.valueOf(instance.getQueueManager().getAllQueueSize()))
                        .replace("<kit>", queueKit.getName() + " " + typeTag)
                        .replace("<time>", instance.getQueueManager().getQueue(profile.getUuid()).getQueueTime(profile.getUuid()))
                        .replace("<playing>", String.valueOf(instance.getMatchManager().getAllMatchSize()))
                        .replace("<username>", player.getName())
                        .replace("<global-elo>", globalElo)
                        .replace("<division>", division));
            }

        } else {
            for (String str : scoreboardConfig.getStringList("scoreboard.in-lobby.lines")) {
                list.add(str.replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("<in-queue>", String.valueOf(instance.getQueueManager().getAllQueueSize()))
                        .replace("<playing>", String.valueOf(instance.getMatchManager().getAllMatchSize()))
                        .replace("<ping>", String.valueOf(profile.getPing()))
                        .replace("<username>", player.getName())
                        .replace("<global-elo>", globalElo)
                        .replace("<division>", division));
            }
        }

        return list;
    }
}
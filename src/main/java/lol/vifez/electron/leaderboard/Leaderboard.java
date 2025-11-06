package lol.vifez.electron.leaderboard;

import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.profile.ProfileManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class Leaderboard {

    private final ProfileManager profileManager;

    public Leaderboard(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    public List<Profile> getLeaderboard(Kit kit) {
        return profileManager.getProfiles().values().stream()
                .sorted(Comparator.comparingInt((Profile p) -> p.getElo(kit)).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public String[] getLeaderboardLayout(Kit kit) {
        List<Profile> leaderboard = getLeaderboard(kit);
        String[] layout = new String[10];

        for (int i = 0; i < layout.length; i++) {
            int rank = i + 1;
            if (i < leaderboard.size()) {
                Profile profile = leaderboard.get(i);
                layout[i] = getRankColor(rank) + "✩" + rank
                        + " &f" + profile.getName()
                        + " &7[&b" + profile.getElo(kit) + "&7]";
            } else {
                layout[i] = getRankColor(rank) + "✩" + rank + " &cN/A";
            }
        }
        return layout;
    }

    public static String getRankColor(int rank) {
        if (rank == 1) return "&6";
        if (rank == 2 || rank == 3) return "&e";
        return "&b";
    }
}
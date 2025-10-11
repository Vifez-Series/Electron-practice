package lol.vifez.electron.leaderboard;

import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.profile.ProfileManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
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
        String[] leaderboardLayout = new String[10];

        for (int i = 0; i < leaderboardLayout.length; i++) {
            if (i < leaderboard.size()) {
                Profile profile = leaderboard.get(i);
                leaderboardLayout[i] = "&7&l" + (i + 1) + ". &b" + profile.getName() + " &7- &b" + profile.getElo(kit);
            } else {
                leaderboardLayout[i] = "&7&l" + (i + 1) + ". &bN/A &7- &b0";
            }
        }

        return leaderboardLayout;
    }
}
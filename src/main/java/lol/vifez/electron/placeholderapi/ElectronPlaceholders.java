package lol.vifez.electron.placeholderapi;

import lol.vifez.electron.Practice;
import lol.vifez.electron.elo.EloUtil;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.TPSUtil;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.lang.management.ManagementFactory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class ElectronPlaceholders extends PlaceholderExpansion {

    private final Practice instance;

    @Override
    public @NotNull String getIdentifier() {
        return "practice";
    }

    @Override
    public @NotNull String getAuthor() {
        return "vifez";
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;
        
        String param = params.toLowerCase().replace("practice_", "");
        Profile profile = instance.getProfileManager().getProfile(player.getUniqueId());


        if (param.equals("elo_global")) {
            return String.valueOf(EloUtil.getGlobalElo(profile));
        }

        if (param.startsWith("elo_")) {
            String kitName = param.replace("elo_", "");
            return String.valueOf(profile.getElo(instance.getKitManager().getKit(kitName)));
        }

        if (param.equals("player_nickname")) {
            String nickname = profile.getNickname();
            return nickname != null && !nickname.isEmpty() ? nickname : player.getName();
        }

        if (param.equals("player_wins")) {
            return String.valueOf(profile.getWins());
        }

        if (param.equals("player_losses")) {
            return String.valueOf(profile.getLosses());
        }

        if (param.equals("server_online")) {
            return String.valueOf(Bukkit.getOnlinePlayers().size());
        }

        if (param.equals("server_tps")) {
            return String.format("%.1f", TPSUtil.getTPS());
        }

        if (param.equals("server_uptime")) {
            long serverUptime = ManagementFactory.getRuntimeMXBean().getUptime();
            return formatUptime(serverUptime);
        }

        if (param.equals("staff_list")) {
            StringBuilder staffList = new StringBuilder();
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission("electron.staff")) {
                    staffList.append("\n&7- &b").append(staff.getName());
                }
            }
            return staffList.length() > 0 ? CC.translate(staffList.toString()) : "&7No staff online";
        }

        if (param.equals("active_matches")) {
            StringBuilder matches = new StringBuilder();
            for (Match match : instance.getMatchManager().getMatches().values()) {
                if (match.getMatchState() == MatchState.STARTED) {
                    Profile profile1 = match.getPlayerOne();
                    Profile profile2 = match.getPlayerTwo();
                    if (profile1 != null && profile2 != null) {
                        matches.append("\n&7- &b")
                              .append(profile1.getPlayer().getName())
                              .append(" &7vs &b")
                              .append(profile2.getPlayer().getName())
                              .append(" &8(")
                              .append(match.getKit().getName())
                              .append(")");
                    }
                }
            }
            return matches.length() > 0 ? CC.translate(matches.toString()) : "&7No active matches";
        }

        return null;
    }

    private String formatUptime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }
}
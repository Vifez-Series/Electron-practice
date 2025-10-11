package lol.vifez.electron.game.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.elo.EloUtil;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.game.match.event.MatchEndEvent;
import lol.vifez.electron.game.match.event.MatchStartEvent;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@Getter
public class MatchManager {

    private final Map<UUID, Match> matches;

    public MatchManager() {
        this.matches = new ConcurrentHashMap<>();
    }

    public Match getMatch(UUID uuid) {
        return matches.get(uuid);
    }

    public int getPlayersInKitMatches(Kit kit) {
        return matches.values().stream()
                .filter(match -> match.getKit().equals(kit))
                .filter(match -> match.getMatchState() == MatchState.STARTED)
                .mapToInt(match -> 2)
                .sum();
    }

    public void remove(UUID uuid) {
        matches.remove(uuid);
    }

    public void remove(Match match) {
        match.getArena().setBusy(false);

        matches.remove(match.getPlayerOne().getUuid());
        matches.remove(match.getPlayerTwo().getUuid());
    }

    public int getAllMatchSize() {
        return matches.size() * 2;
    }

    public void start(Match match) {
        match.setMatchState(MatchState.STARTING);
        match.getArena().setBusy(true);

        Profile profileOne = match.getPlayerOne();
        Profile profileTwo = match.getPlayerTwo();
        Profile[] profiles = {profileOne, profileTwo};

        int index = 0;
        for (Profile profile : profiles) {
            profile.getPlayer().teleport(index == 0 ? match.getArena().getSpawnA() : match.getArena().getSpawnB());
            profile.getPlayer().getActivePotionEffects().clear();
            match.denyMovement(profile.getPlayer());

            ItemStack[] loadout = profile.getKitLoadout().getOrDefault(
                    match.getKit().getName().toLowerCase(),
                    match.getKit().getContents()
            );

            profile.getPlayer().getInventory().setContents(loadout);
            profile.getPlayer().getInventory().setArmorContents(match.getKit().getArmorContents());
            profile.getPlayer().updateInventory();

            if (profile.getQueue() != null) {
                profile.getQueue().remove(profile.getPlayer());
            }

            matches.put(profile.getUuid(), match);
            index++;
        }

        Bukkit.getPluginManager().callEvent(new MatchStartEvent(profileOne, profileTwo, match));
    }

    public int getTotalPlayersInMatches() {
        return matches.values().stream()
                .mapToInt(match -> match.getMatchState() == MatchState.STARTED ? 2 : 0)
                .sum();
    }

    public void end(Match match) {
        match.setMatchState(MatchState.ENDING);

        if (match.getWinner() == null) {
            Profile profileOne = match.getPlayerOne();
            Profile profileTwo = match.getPlayerTwo();
            Profile[] profiles = {profileOne, profileTwo};

            for (Profile profile : profiles) {
                CC.sendMessage(profile.getPlayer(), "&cMatch has ended!");
                profile.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.NOTE_PLING, 0.5f, 0.5f);
                Bukkit.getPluginManager().callEvent(new MatchEndEvent(profileOne, profileTwo, match));

                Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), () -> {
                    profile.getPlayer().getInventory().setArmorContents(null);
                    profile.getPlayer().getInventory().setContents(Hotbar.getSpawnItems());

                    profile.getPlayer().teleport(Practice.getInstance().getSpawnLocation());

                    match.setMatchState(MatchState.ENDED);
                    match.getArena().setBusy(false);

                    remove(match);
                }, 100L);
            }
        } else {
            Profile winner = match.getWinner();
            Profile loser = match.getOpponent(match.getWinner().getPlayer());
            Profile[] profiles = {winner, loser};

            if (match.isRanked()) {
                updateEloForRankedMatch(winner, loser, match.getKit());
            }

            for (Profile profile : profiles) {
                profile.getPlayer().playSound(profile.getPlayer().getLocation(), Sound.NOTE_PLING, 0.5f, 0.5f);

                Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), () -> {
                    profile.getPlayer().getInventory().setArmorContents(null);
                    profile.getPlayer().getInventory().setContents(Hotbar.getSpawnItems());

                    profile.getPlayer().teleport(Practice.getInstance().getSpawnLocation());

                    match.setMatchState(MatchState.ENDED);
                    match.getArena().setBusy(false);

                    match.allowMovement(profile.getPlayer());
                    remove(match);
                }, 100L);
            }
        }
    }

    /**
     * Updates ELO for both players in a ranked match
     * @param winner The winning player
     * @param loser The losing player
     * @param kit The kit used in the match
     */
    private void updateEloForRankedMatch(Profile winner, Profile loser, Kit kit) {
        int winnerElo = winner.getElo(kit);
        int loserElo = loser.getElo(kit);
        
        int newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
        int newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);
        
        winner.setElo(kit, newWinnerElo);
        loser.setElo(kit, newLoserElo);
        
        winner.checkDivision(kit);
        loser.checkDivision(kit);
        
        if (winner.getPlayer() != null) {
            int eloChange = newWinnerElo - winnerElo;
            String changeMsg = eloChange >= 0 ? "&a+" + eloChange : "&c" + eloChange;
            CC.sendMessage(winner.getPlayer(), "&aYou won! &7ELO: " + changeMsg + " &7(&e" + newWinnerElo + "&7)");
        }
        
        if (loser.getPlayer() != null) {
            int eloChange = newLoserElo - loserElo;
            String changeMsg = eloChange >= 0 ? "&a+" + eloChange : "&c" + eloChange;
            CC.sendMessage(loser.getPlayer(), "&cYou lost! &7ELO: " + changeMsg + " &7(&e" + newLoserElo + "&7)");
        }
    }
}
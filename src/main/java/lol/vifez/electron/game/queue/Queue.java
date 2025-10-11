package lol.vifez.electron.game.queue;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.match.Match;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.hotbar.Hotbar;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@Getter
public class Queue {

    private final Practice instance;
    private final Kit kit;
    private final boolean ranked;

    private final Map<UUID, Long> playerJoinTimes;

    private int rankedIndex = 1;

    public Queue(Practice instance, Kit kit, boolean ranked) {
        this.instance = instance;
        this.kit = kit;
        this.ranked = ranked;
        this.playerJoinTimes = new ConcurrentHashMap<>();
    }

    public String getFormattedQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) return "00:00";

        long elapsed = System.currentTimeMillis() - joinTime;
        long seconds = elapsed / 1000;
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void move() {
        Map<UUID, Queue> playersInQueue = instance.getQueueManager().getPlayersQueue();
        List<UUID> playersUUID = new ArrayList<>(playersInQueue.keySet());

        if (playersInQueue.isEmpty() || playersInQueue.size() < 2) return;

        Player one = Bukkit.getPlayer(playersUUID.get(0));
        Player two = Bukkit.getPlayer(playersUUID.get(1));

        Profile oneProfile = instance.getProfileManager().getProfile(one.getUniqueId());
        Profile twoProfile = instance.getProfileManager().getProfile(two.getUniqueId());

        Arena arena = instance.getArenaManager().getAllAvailableArenas(kit)
                .stream().filter(Objects::nonNull).findFirst().orElse(null);

        if (ranked) {
            int difference = oneProfile.getElo(kit) - twoProfile.getElo(kit);

            if (difference <= 250) {
                playersInQueue.remove(one.getUniqueId());
                playersInQueue.remove(two.getUniqueId());

                if (arena == null) {
                    CC.sendMessage(one, "&cThere are no available arenas for you to join!");
                    CC.sendMessage(two, "&cThere are no available arenas for you to join!");

                    one.getInventory().setContents(Hotbar.getSpawnItems());
                    two.getInventory().setContents(Hotbar.getSpawnItems());
                    return;
                }

                Bukkit.getScheduler().runTask(Practice.getInstance(), () -> new Match(instance, oneProfile, twoProfile, kit, arena, true));
            } else {
                if (rankedIndex > playersInQueue.size()) return;

                rankedIndex++;
                two = Bukkit.getPlayer(new ArrayList<>(playersInQueue.keySet()).get(rankedIndex));
            }
            return;
        }

        playersInQueue.remove(one.getUniqueId());
        playersInQueue.remove(two.getUniqueId());

        if (arena == null) {
            CC.sendMessage(one, "&cThere are no available arenas for you to join!");
            CC.sendMessage(two, "&cThere are no available arenas for you to join!");

            one.getInventory().setContents(Hotbar.getSpawnItems());
            two.getInventory().setContents(Hotbar.getSpawnItems());
            return;
        }

        final Player finalTwo = two;

        Bukkit.getScheduler().runTask(instance, () -> {
            Match match = new Match(instance,
                    instance.getProfileManager().getProfile(one.getUniqueId()),
                    instance.getProfileManager().getProfile(finalTwo.getUniqueId()),
                    kit, arena, ranked);

            instance.getMatchManager().start(match);
        });
    }

    public void add(Player player) {
        instance.getQueueManager().getPlayersQueue().put(player.getUniqueId(), this);
        instance.getProfileManager().getProfile(player.getUniqueId())
                .setCurrentQueue(ranked ? "ranked_" + this.getKit().getName() : this.getKit().getName());

        playerJoinTimes.put(player.getUniqueId(), System.currentTimeMillis());

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(Hotbar.getQueueItems());
    }

    public void remove(Player player) {
        instance.getQueueManager().getPlayersQueue().remove(player.getUniqueId());
        instance.getProfileManager().getProfile(player.getUniqueId()).setCurrentQueue("");

        playerJoinTimes.remove(player.getUniqueId());

        player.getInventory().setArmorContents(null);
        player.getInventory().setContents(Hotbar.getSpawnItems());
    }

    public int getQueueSize() {
        return playerJoinTimes.size();
    }

    public String getQueueTime(UUID playerId) {
        Long joinTime = playerJoinTimes.get(playerId);
        if (joinTime == null) return "0s";

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - joinTime;

        long seconds = elapsedTime / 1000;

        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return (seconds / 60) + "m";
        } else {
            return (seconds / 3600) + "h";
        }
    }
}
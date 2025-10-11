package lol.vifez.electron.game.match;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.kit.enums.KitType;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@Data
public class Match {

    private final Practice instance;
    private final Profile playerOne, playerTwo;
    private final Kit kit;
    private final Arena arena;
    private final boolean ranked, waterKill;

    private Profile winner = null;
    private MatchState matchState = MatchState.STARTING;

    private int countdownTime = 5;
    @Getter @Setter
    private int currentCountdown = -1;
    private Instant startTime;

    private Map<UUID, Integer> hitsMap = new HashMap<>();
    private boolean bedBrokenOne = false, bedBrokenTwo = false;

    public Match(Practice instance, Profile playerOne, Profile playerTwo, Kit kit, Arena arena, boolean ranked) {
        this.instance = instance;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.kit = kit;
        this.arena = arena;
        this.startTime = Instant.now();
        this.ranked = ranked;
        this.waterKill = kit.getKitType() == KitType.WATER_KILL;

        hitsMap.put(playerOne.getUuid(), 0);
        hitsMap.put(playerTwo.getUuid(), 0);
    }

    public String getDuration() {
        Instant now = Instant.now();
        Duration duration = Duration.between(startTime, now);
        long seconds = duration.getSeconds();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    @Getter
    @Setter
    private boolean countdownRunning = false;

    public Profile getOpponent(Player player) {
        return player.getUniqueId().equals(playerOne.getUuid()) ? playerTwo : playerOne;
    }

    public void denyMovement(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setWalkSpeed(0.0F);
        player.setFlySpeed(0.0F);
        player.setFoodLevel(0);

        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void allowMovement(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.2F);
        player.setFoodLevel(20);

        player.setSprinting(true);
        player.removePotionEffect(PotionEffectType.JUMP);
        player.setGameMode(GameMode.SURVIVAL);
    }
}

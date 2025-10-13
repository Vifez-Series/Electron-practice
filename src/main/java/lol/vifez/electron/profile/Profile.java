package lol.vifez.electron.profile;

import com.google.gson.annotations.SerializedName;
import lol.vifez.electron.Practice;
import lol.vifez.electron.duel.DuelRequest;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.divisions.Divisions;
import lol.vifez.electron.match.Match;
import lol.vifez.electron.queue.Queue;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.MessageBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
@Getter
@Setter
public class Profile {

    @SerializedName("_id")
    private final UUID uuid;

    private Player lastMessagedPlayer;

    private transient DuelRequest duelRequest;

    private String name = "", currentQueue = "";

    private Divisions division = Divisions.SILVER_I;

    private int wins = 0, losses = 0, winStreak = 0;

    private boolean editMode = false, buildMode = false;

    private final Map<String, ItemStack[]> kitLoadout = new HashMap<>();
    private final Map<String, Integer> kitWins = new HashMap<>();
    private final Map<String, Integer> eloMap = new HashMap<>();

    private boolean scoreboardEnabled = true;
    private boolean privateMessagingEnabled = true;
    private boolean duelRequestsEnabled = true;
    private String worldTime = "DAY";

    public Player getPlayer() {
        Player p = Bukkit.getPlayer(uuid);
        return (p != null && p.isOnline()) ? p : null;
    }

    public int getPing() {
        Player player = getPlayer();
        if (player != null) {
            try {
                Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public int getElo(Kit kit) {
        return eloMap.getOrDefault(kit.getName().toLowerCase(), 1000);
    }

    public void setElo(Kit kit, int elo) {
        eloMap.put(kit.getName().toLowerCase(), elo);
    }

    public boolean inMatch() {
        return Practice.getInstance().getMatchManager().getMatch(uuid) != null;
    }

    public Match getMatch() {
        return Practice.getInstance().getMatchManager().getMatch(uuid);
    }

    public Queue getQueue() {
        return Practice.getInstance().getQueueManager().getQueue(uuid);
    }

    public void checkDivision(Kit kit) {
        int elo = getElo(kit);
        Divisions playerDivision = division;

        for (Divisions d : Divisions.values()) {
            if (elo >= d.getMinimumElo()) {
                playerDivision = d;
            } else break;
        }

        if (division != playerDivision) {
            division = playerDivision;
            Player player = getPlayer();
            if (player != null) {
                CC.sendMessage(player, "&aYou are now in " + playerDivision.getPrettyName() + " &adivision!");
            }
        }
    }

    public void sendDuelRequest(Player target, Kit kit) {
        Player sender = getPlayer();
        Profile targetProfile = Practice.getInstance().getProfileManager().getProfile(target.getUniqueId());

        if (!duelRequestsEnabled) {
            CC.sendMessage(sender, "&cYou cannot send a duel request while you are not allowing duel requests!");
            return;
        }

        if (!targetProfile.isDuelRequestsEnabled()) {
            CC.sendMessage(sender, "&c" + target.getName() + " is not allowing duel requests!");
            return;
        }

        if (duelRequest != null && !duelRequest.isExpired()) {
            long seconds = (System.currentTimeMillis() - duelRequest.getRequestedAt()) / 1000;
            CC.sendMessage(sender, "&cYou have already sent a duel request to " + target.getName() + ". Please wait " + seconds + "s.");
            return;
        }

        DuelRequest request = new DuelRequest(Practice.getInstance(), this, targetProfile, kit, System.currentTimeMillis());
        this.duelRequest = request;
        targetProfile.setDuelRequest(request);

        CC.sendMessage(sender, CC.translate("\n&c&lDuel sent\n&7▪ Opponent: &c" + target.getName() + "\n&7▪ Kit: &c" + kit.getName() + "\n "));

        new MessageBuilder(CC.translate(
                "\n&c&lDuel Request" +
                        "\n&7▪ Opponent: &c" +
                        name +
                        "\n&7▪ Kit: &c" + kit.getName() +
                        "\n&a&lCLICK TO ACCEPT\n"))
                .hover(true)
                .clickable(true)
                .hoverText("&bClick to accept")
                .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + name))
                .sendMessage(target);
    }
}
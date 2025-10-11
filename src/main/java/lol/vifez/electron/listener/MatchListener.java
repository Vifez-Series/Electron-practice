package lol.vifez.electron.listener;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.match.enums.MatchState;
import lol.vifez.electron.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MatchListener implements Listener {

    public MatchListener() {
        Practice.getInstance().getServer().getPluginManager().registerEvents(this, Practice.getInstance());
    }

    @EventHandler
    public void onDamageWhileStart(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getName());

            if (profile.inMatch() &&
                    (profile.getMatch().getMatchState() == MatchState.STARTING
                            || profile.getMatch().getMatchState() == MatchState.ENDING
                            || profile.getMatch().getMatchState() == MatchState.ENDED)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getName());

        if (!profile.inMatch()) return;

        Profile killer = profile.getMatch().getOpponent(player);

        event.getDrops().clear();
        event.setDeathMessage(null);
        event.setNewExp(0);
        event.setNewLevel(0);
        event.setNewTotalExp(0);
        event.setKeepInventory(false);
        event.setKeepLevel(false);

        profile.getMatch().setWinner(killer);
        Practice.getInstance().getMatchManager().end(profile.getMatch());
    }
}
package lol.vifez.electron.util.conversation;

import lol.vifez.electron.Practice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ConversationListener implements Listener {
    
    private final ConversationManager manager;
    
    public ConversationListener(Practice practice) {
        this.manager = practice.getConversationManager();
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (manager.isInConversation(player)) {
            event.setCancelled(true);
            manager.handleInput(player, event.getMessage());
        }
    }
}
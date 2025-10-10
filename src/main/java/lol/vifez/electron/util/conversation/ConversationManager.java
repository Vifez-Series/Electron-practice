package lol.vifez.electron.util.conversation;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ConversationManager {
    
    @Getter
    private final Map<UUID, Consumer<String>> conversations;

    public ConversationManager() {
        this.conversations = new HashMap<>();
    }

    public void startConversation(Player player, Consumer<String> callback) {
        conversations.put(player.getUniqueId(), callback);
    }

    public boolean isInConversation(Player player) {
        return conversations.containsKey(player.getUniqueId());
    }

    public void handleInput(Player player, String message) {
        Consumer<String> callback = conversations.remove(player.getUniqueId());
        if (callback != null) {
            callback.accept(message);
        }
    }
}
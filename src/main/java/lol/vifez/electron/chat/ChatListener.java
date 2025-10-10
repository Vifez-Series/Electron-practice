package lol.vifez.electron.chat;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final Practice plugin;
    private final ChatFormat chatFormat;

    public ChatListener(Practice plugin) {
        this.plugin = plugin;
        this.chatFormat = new ChatFormat(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        String message = event.getMessage();

        // Check chat delay if enabled
        if (plugin.getConfig().getBoolean("chat.filter.enabled") &&
            !player.hasPermission("practice.chat.bypass.delay")) {
            
            long currentTime = System.currentTimeMillis();
            long lastMessageTime = profile.getLastMessageTime();
            int chatDelay = plugin.getConfig().getInt("chat.filter.chat-delay", 3) * 1000;

            if (lastMessageTime != 0 && currentTime - lastMessageTime < chatDelay) {
                event.setCancelled(true);
                player.sendMessage("§cYou must wait before sending another message.");
                return;
            }

            profile.setLastMessageTime(currentTime);
        }

        // Check for spam if enabled
        if (plugin.getConfig().getBoolean("chat.filter.enabled") &&
            plugin.getConfig().getBoolean("chat.filter.block-spam", true) &&
            !player.hasPermission("practice.chat.bypass.filter")) {
            
            int maxRepeatedChars = plugin.getConfig().getInt("chat.filter.max-repeated-chars", 3);
            String filteredMessage = filterRepeatedChars(message, maxRepeatedChars);
            
            if (!filteredMessage.equals(message)) {
                message = filteredMessage;
            }
        }

        // Format the message
        String formattedMessage = chatFormat.format(message, player);
        event.setFormat(formattedMessage);
    }

    private String filterRepeatedChars(String message, int maxRepeats) {
        if (maxRepeats <= 0) return message;
        
        StringBuilder result = new StringBuilder();
        char lastChar = '\0';
        int count = 0;

        for (char c : message.toCharArray()) {
            if (c == lastChar) {
                count++;
                if (count <= maxRepeats) {
                    result.append(c);
                }
            } else {
                lastChar = c;
                count = 1;
                result.append(c);
            }
        }

        return result.toString();
    }
}
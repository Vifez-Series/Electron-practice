package lol.vifez.electron.util.conversation;

import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

public class Conversation {
    private static final Plugin plugin = lol.vifez.electron.Practice.getInstance();
    private final ConversationFactory factory;

    private final Conversable conversable;

    public Conversation(Conversable who, Prompt firstPrompt) {
        this.conversable = who;
        this.factory = new ConversationFactory(plugin)
                .withFirstPrompt(firstPrompt)
                .withTimeout(30)
                .withLocalEcho(false)
                .withModality(false);
    }

    public void begin() {
        factory.buildConversation(conversable).begin();
    }
}
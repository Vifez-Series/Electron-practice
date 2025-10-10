package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class NicknameButton {

    public static Button createNicknameButton(Profile profile, Practice instance) {
        String currentNickname = profile.getNickname();
        ItemStack nicknameItem = new ItemBuilder(Material.NAME_TAG)
                .name(CC.translate("&eNickname"))
                .lore(Arrays.asList(
                        CC.translate("&7Change your display name."),
                        CC.translate("&7Current nickname: " + (currentNickname != null && !currentNickname.isEmpty() ? "&a" + currentNickname : "&cNone")),
                        CC.translate("&r"),
                        CC.translate("&eLeft-Click to set nickname!"),
                        CC.translate("&eRight-Click to remove nickname!")
                ))
                .build();

        return new EasyButton(nicknameItem, true, true, () -> {
            // Left click - Set nickname
            Player player = profile.getPlayer();
            player.closeInventory();

            ConversationFactory factory = new ConversationFactory(Practice.getInstance())
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        if (!player.hasPermission("electron.rank.vip")) {
                            CC.sendMessage(player, "&cYou need VIP rank to use nicknames!");
                            new SettingsMenu(instance, profile).openMenu(player);
                            return null;
                        }
                        return CC.translate("&eEnter your desired nickname (or 'cancel' to abort):");
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (!player.hasPermission("electron.rank.vip")) {
                            return Prompt.END_OF_CONVERSATION;
                        }
                        if (input.equalsIgnoreCase("cancel")) {
                            CC.sendMessage(player, "&cNickname change cancelled.");
                            new SettingsMenu(instance, profile).openMenu(player);
                            return Prompt.END_OF_CONVERSATION;
                        }

                        // Add your nickname validation here
                        if (input.length() < 3 || input.length() > 16) {
                            CC.sendMessage(player, "&cNickname must be between 3 and 16 characters.");
                            new SettingsMenu(instance, profile).openMenu(player);
                            return Prompt.END_OF_CONVERSATION;
                        }

                        profile.setNickname(input);
                        Practice.getInstance().getProfileManager().save(profile);
                        CC.sendMessage(player, "&aYour nickname has been set to: &e" + input);
                        new SettingsMenu(instance, profile).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .withTimeout(30)
                .withLocalEcho(false)
                .withModality(false);
            
            factory.buildConversation(player).begin();
        }, () -> {
            // Right click - Remove nickname
            profile.setNickname("");
            Practice.getInstance().getProfileManager().save(profile);
            CC.sendMessage(profile.getPlayer(), "&cYour nickname has been removed.");
            new SettingsMenu(instance, profile).openMenu(profile.getPlayer());
        });
    }
}
package lol.vifez.electron.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

@CommandAlias("reply|r")
@Description("Reply to someone.")
public class ReplyCommand extends BaseCommand {

    @Default
    public void onReplyCommand(Player sender, String message) {
        Profile senderProfile = Practice.get().getProfileManager().getProfile(sender.getUniqueId());
        Player recipient = senderProfile.getLastMessagedPlayer();

        if (recipient == null) {
            sender.sendMessage(CC.translate("&cThere is nobody to reply to."));
            return;
        }

        if (sender.equals(recipient)) {
            sender.sendMessage(CC.translate("&cYou cannot reply to yourself."));
            return;
        }

        if (!senderProfile.isPrivateMessagingEnabled()) {
            sender.sendMessage(CC.translate("&cPrivate messaging is disabled."));
            return;
        }

        recipient.sendMessage(CC.translate("&7(From &b" + sender.getName() + "&7) &f" + message));
        sender.sendMessage(CC.translate("&7(To &b" + recipient.getName() + "&7) &f" + message));
    }
}
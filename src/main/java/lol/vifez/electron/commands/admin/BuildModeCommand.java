package lol.vifez.electron.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.entity.Player;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

public class BuildModeCommand extends BaseCommand {

    @CommandAlias("buildmode|build|bm")
    @CommandPermission("electron.builder")
    public void buildMode(Player player) {
        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        profile.setBuildMode(!profile.isBuildMode());

        CC.sendMessage(player, profile.isBuildMode() ? "&aYou are now in build mode!" : "&cYou are no longer in build mode!");
    }
}

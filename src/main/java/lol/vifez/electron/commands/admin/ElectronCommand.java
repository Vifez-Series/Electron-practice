package lol.vifez.electron.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lol.vifez.electron.Practice;
import lol.vifez.electron.util.CC;
import org.bukkit.command.CommandSender;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

@CommandAlias("electron")
public class ElectronCommand extends BaseCommand {

    @Default
    @CommandPermission("electron.admin")
    public void onDefault(CommandSender sender) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lElectron Practice &7[Open SRC]"));
        sender.sendMessage(CC.translate("&7&oLightweight open-source practice core"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&fAuthors: &bvifez &f& &eMTR"));
        sender.sendMessage(CC.translate("&fVersion: &b1.0"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&fDiscord: &bhttps://discord.vifez.lol"));
        sender.sendMessage(CC.translate("&fGithub: &bhttps://electron.vifez.lol"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("reload")
    @CommandPermission("electron.admin")
    public void onReload(CommandSender sender) {
        Practice.getInstance().reloadConfig();
        sender.sendMessage(CC.translate("&b&lElectron &7| &fConfigurations reloaded."));
    }
}
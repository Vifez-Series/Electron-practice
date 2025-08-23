package lol.vifez.electron.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

@CommandAlias("elo")
@CommandPermission("electron.admin")
public class EloCommand extends BaseCommand {

    @HelpCommand
    @Subcommand("help")
    public void help(CommandSender sender) {
        CC.sendMessage(sender, "&b&lElo Commands",
                "&7* &b/elo set &7<player> <kit> <elo> &f- &fSet a player's elo for a kit",
                "&7* &b/elo add &7<player> <kit> <elo> &f- &fAdd elo to a player's elo for a kit",
                "&7* &b/elo remove &7<player> <kit> <elo> &f- &fRemove elo from a player's elo for a kit");
    }

    @Subcommand("set")
    public void set(CommandSender sender, @Name("player") String playerName, @Name("kit") String kitName, @Name("elo") int elo) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            CC.sendMessage(sender, "&cPlayer not found.");
            return;
        }

        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        Kit kit = Practice.getInstance().getKitManager().getKit(kitName.toLowerCase());
        if (kit == null) {
            CC.sendMessage(sender, "&cInvalid kit name.");
            return;
        }

        profile.setElo(kit, Math.max(0, elo));
        CC.sendMessage(sender, "&aYou have set &e" + player.getName() + "'s &aelo to " + Math.max(0, elo) + " for &e" + kit.getColor() + kit.getName());
    }

    @Subcommand("add")
    public void add(CommandSender sender, @Name("player") String playerName, @Name("kit") String kitName, @Name("elo") int elo) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            CC.sendMessage(sender, "&cPlayer not found.");
            return;
        }

        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        Kit kit = Practice.getInstance().getKitManager().getKit(kitName.toLowerCase());
        if (kit == null) {
            CC.sendMessage(sender, "&cInvalid kit name.");
            return;
        }

        int i = Math.max(0, profile.getElo(kit) + elo);
        profile.setElo(kit, i);
        CC.sendMessage(sender, "&aYou have added " + elo + " elo to &e" + player.getName() + " &afor &e" + kit.getColor() + kit.getName());
    }

    @Subcommand("remove")
    public void remove(CommandSender sender, @Name("player") String playerName, @Name("kit") String kitName, @Name("elo") int elo) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            CC.sendMessage(sender, "&cPlayer not found.");
            return;
        }

        Profile profile = Practice.getInstance().getProfileManager().getProfile(player.getUniqueId());
        Kit kit = Practice.getInstance().getKitManager().getKit(kitName.toLowerCase());
        if (kit == null) {
            CC.sendMessage(sender, "&cInvalid kit name.");
            return;
        }

        int i = Math.max(0, profile.getElo(kit) - elo);
        profile.setElo(kit, i);
        CC.sendMessage(sender, "&aYou have removed " + elo + " elo from &e" + player.getName() + " &afor &e" + kit.getColor() + kit.getName());
    }
}
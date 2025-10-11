package lol.vifez.electron.game.arena.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.game.arena.Arena;
import lol.vifez.electron.game.arena.manager.ArenaManager;
import lol.vifez.electron.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@CommandAlias("arena")
@CommandPermission("electron.admin")
public class ArenaCommand extends BaseCommand {

    private final ArenaManager arenaManager;

    public ArenaCommand(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @Default
    public void onArenaCommand(CommandSender sender) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lArena Commands"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&7▪ &b/arena create &7<arena> &f- &fCreate an arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena delete &7<arena> &f- &fDelete an arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena setfirstposition &7<arena> &f- &fSet a position for players to teleport for arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena setsecondposition &7<arena> &f- &fSet a position for players to teleport for arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena setmin &7<arena> &f- &fSet the minimum point for arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena setmax &7<arena> &f- &fSet the maximum point for arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena addkit &7<arena> <kit> &f- &fAdd kit to an arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena removekit &7<arena> <kit> &f- &fRemove kit from an arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena kits &7<arena> &f- &fList of kits allowed in an arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena status &7<arena> &f- &fCheck the status of an arena"));
        sender.sendMessage(CC.translate("&7▪ &b/arena save &f- &fSave all arenas"));
        sender.sendMessage(CC.translate("&7▪ &b/arenas &f- &fManage the arenas"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("create")
    @CommandPermission("electron.admin")
    public void createArena(CommandSender sender, @Single String arenaName, @Single String type) {
        if (arenaManager.getArena(arenaName) != null) {
            sender.sendMessage(CC.translate("&cArena already exists!"));
            return;
        }

        Arena arena = new Arena(arenaName);
        arena.setBusy(false);
        arenaManager.save(arena);

        sender.sendMessage(CC.translate("&aArena created successfully!"));
    }

    @Subcommand("delete")
    @CommandPermission("electron.admin")
    public void deleteArena(CommandSender sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arenaManager.delete(arena);
        sender.sendMessage(CC.translate("&aArena deleted successfully!"));
    }

    @Subcommand("status")
    @CommandPermission("electron.admin")
    public void statusArena(CommandSender sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        String statusMessage = CC.translate("&fArena&7: &b" + arena.getName() + "\n")
                + CC.translate("&7Spawn A: " + (arena.getPositionOne() != null ? "&aSet" : "&cNot Set")) + "\n"
                + CC.translate("&7Spawn B: " + (arena.getPositionTwo() != null ? "&aSet" : "&cNot Set")) + "\n"
                + CC.translate("&7Busy: " + (arena.isBusy() ? "&cYes" : "&aNo"));
        sender.sendMessage(statusMessage);
    }

    @Subcommand("kits")
    @CommandPermission("electron.admin")
    public void kitsArena(CommandSender sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        String kitsMessage = CC.translate("&fKits&7: &b" + arena.getName() + "\n");
        for (String kit : arena.getKits()) {
            kitsMessage += CC.translate("&7▪ &b" + kit + "\n");
        }
        sender.sendMessage(kitsMessage);
    }

    @Subcommand("addkit")
    @CommandPermission("electron.admin")
    public void addKitArena(CommandSender sender, @Single String arenaName, @Single String kitName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.getKits().add(kitName.toLowerCase());
        sender.sendMessage(CC.translate("&aKit added to arena!"));
    }

    @Subcommand("removekit")
    @CommandPermission("electron.admin")
    public void removeKitArena(CommandSender sender, @Single String arenaName, @Single String kitName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.getKits().remove(kitName.toLowerCase());
        sender.sendMessage(CC.translate("&aKit removed from arena!"));
    }

    @Subcommand("setpos1|setfirstposition")
    @CommandPermission("electron.admin")
    public void setFirstPositionArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setSpawnA(sender.getLocation());
        sender.sendMessage(CC.translate("&aFirst position set!"));
    }

    @Subcommand("setpos2|setsecondposition")
    @CommandPermission("electron.admin")
    public void setSecondPositionArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setSpawnB(sender.getLocation());
        sender.sendMessage(CC.translate("&aSecond position set!"));
    }

    @Subcommand("setmin")
    @CommandPermission("electron.admin")
    public void setMinArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setPositionOne(sender.getLocation());
        sender.sendMessage(CC.translate("&aMinimum point set!"));
    }

    @Subcommand("setmax")
    @CommandPermission("electron.admin")
    public void setMaxArena(Player sender, @Single String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            sender.sendMessage(CC.translate("&cArena not found!"));
            return;
        }

        arena.setPositionTwo(sender.getLocation());
        sender.sendMessage(CC.translate("&aMaximum point set!"));
    }

    @Subcommand("save")
    @CommandPermission("electron.admin")
    public void saveArenas(CommandSender sender) {
        arenaManager.close();
        sender.sendMessage(CC.translate("&b&lElectron &7┃ &fSaved to &bArenas.yml."));
    }
}
package lol.vifez.electron.ranks.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.ranks.Rank;
import lol.vifez.electron.ranks.menu.RankSelectionMenu;
import lol.vifez.electron.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("rank")
public class RankCommands extends BaseCommand {

    private final Practice practice;

    public RankCommands(Practice practice) {
        this.practice = practice;
    }

    @Default
    @CommandPermission("electron.rank.admin")
    public void onRank(Player player) {
        new RankSelectionMenu(practice).openMenu(player);
    }

    @Subcommand("create")
    @CommandPermission("electron.rank.admin")
    @Description("Create a new rank")
    public void onCreate(CommandSender sender, String name, String prefix, String color) {
        if (practice.getRankManager().getRank(name) != null) {
            sender.sendMessage(CC.translate("&cThat rank already exists!"));
            return;
        }

        Rank rank = new Rank(name, prefix, color, "electron.rank." + name.toLowerCase(), 0, false);
        practice.getRankManager().getRanks().add(rank);
        practice.getRankManager().loadRanks();
        
        sender.sendMessage(CC.translate("&aSuccessfully created rank " + rank.getDisplayName()));
    }

    @Subcommand("delete")
    @CommandPermission("electron.rank.admin")
    @CommandCompletion("@ranks")
    @Description("Delete a rank")
    public void onDelete(CommandSender sender, String name) {
        Rank rank = practice.getRankManager().getRank(name);
        if (rank == null) {
            sender.sendMessage(CC.translate("&cThat rank doesn't exist!"));
            return;
        }

        practice.getRankManager().getRanks().remove(rank);
        practice.getRankManager().loadRanks();
        
        sender.sendMessage(CC.translate("&aSuccessfully deleted rank " + rank.getDisplayName()));
    }
}
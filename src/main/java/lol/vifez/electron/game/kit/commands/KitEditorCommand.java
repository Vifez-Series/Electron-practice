package lol.vifez.electron.game.kit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.menu.editor.KitSelectionMenu;
import lol.vifez.electron.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("editor")
@CommandPermission("electron.user")
public class KitEditorCommand extends BaseCommand {

    @Default
    public void onKitEditorCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            CC.sendMessage(sender, "&cThis command can only be run by a player.");
            return;
        }

        Player player = (Player) sender;
        openKitEditor(player);
    }

    private void openKitEditor(Player player) {
        KitSelectionMenu kitMenu = new KitSelectionMenu(Practice.getInstance());
        kitMenu.openMenu(player);
    }
}
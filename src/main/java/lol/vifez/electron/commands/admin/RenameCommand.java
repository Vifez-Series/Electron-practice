package lol.vifez.electron.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@CommandAlias("rename")
@CommandPermission("electron.admin")
public class RenameCommand extends BaseCommand {

    @Default
    @Syntax("<name>")
    public void onRename(Player player, String[] args) {
        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou must be holding an item to rename."));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(CC.translate("&cUsage: /rename <newName>"));
            return;
        }

        String newName = CC.translate(String.join(" ", args));

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            player.sendMessage(CC.translate("&cFailed to rename item."));
            return;
        }

        meta.setDisplayName(newName);
        item.setItemMeta(meta);

        player.sendMessage(CC.translate("&fYour &bitem &fhas been renamed to " + newName));
    }
}
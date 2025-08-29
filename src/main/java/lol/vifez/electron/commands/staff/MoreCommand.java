package lol.vifez.electron.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.util.CC;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("more")
@CommandPermission("electron.staff")
public class MoreCommand extends BaseCommand {

    @Default
    public void onMore(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cNice try lil bro."));
            return;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(CC.translate("&cYou must have an item in your hand!"));
            return;
        }

        itemInHand.setAmount(64);
        player.setItemInHand(itemInHand);
    }
}

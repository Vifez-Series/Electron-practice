package lol.vifez.electron.kit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.messages.ErrorMessages;
import lol.vifez.electron.util.messages.SuccessMessages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@CommandAlias("kit")
@CommandPermission("electron.admin")
public class KitCommands extends BaseCommand {

    private final Practice instance = Practice.getInstance();

    @Default
    public void onKitCommand(CommandSender sender) {
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&b&lKit Commands"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&7▪ &b/kit create &7<kit> &f- &fCreate a new kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit delete &7<kit> &f- &fDelete an existing kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setType &7<kit> <type> &f- &fSet the type of a kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setInventory &7<kit> &f- &fSet the inventory of a kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setIcon &7<kit> &f- &fSet the icon for a kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setRanked &7<kit> &f- &fToggle whether a kit is ranked"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setDescription &7<kit> <description> &f- &fSet the description of a kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit list &f- &fList all kits"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("create")
    public void create(CommandSender sender, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit != null) {
            CC.sendMessage(sender, ErrorMessages.KIT_ALREADY_EXISTS);
            return;
        }

        kit = new Kit(kitSingle);
        kit.setIcon(Material.IRON_SWORD);
        instance.getKitManager().save(kit);

        CC.sendMessage(sender, String.format(SuccessMessages.KIT_CREATED, kit.getColor() + kit.getName()));
    }

    @Subcommand("delete")
    public void delete(CommandSender sender, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit == null) {
            CC.sendMessage(sender, ErrorMessages.KIT_DOES_NOT_EXIST);
            return;
        }

        instance.getKitManager().delete(kit);
        CC.sendMessage(sender, String.format(SuccessMessages.KIT_DELETED, kit.getColor() + kit.getName()));
    }

    @Subcommand("setinventory")
    public void setInventory(Player player, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, ErrorMessages.INVALID_KIT_NAME);
            return;
        }

        kit.setArmorContents(player.getInventory().getArmorContents());
        kit.setContents(player.getInventory().getContents());

        CC.sendMessage(player, String.format(SuccessMessages.INVENTORY_UPDATED, kitSingle));
    }

    @Subcommand("setIcon")
    public void setIcon(Player player, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, ErrorMessages.INVALID_KIT_NAME);
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            CC.sendMessage(player, ErrorMessages.NOTHING_IN_HAND);
            return;
        }

        kit.setIcon(itemInHand.getType());
        CC.sendMessage(player, String.format(SuccessMessages.ICON_UPDATED, kit.getName()));
    }

    @Subcommand("setType")
    public void setType(Player player, @Name("kit") @Single String kitName, @Name("type") @Single String type) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            CC.sendMessage(player, ErrorMessages.INVALID_KIT_NAME);
            return;
        }

        try {
            KitType typeEnum = KitType.valueOf(type.toUpperCase());
            kit.setKitType(typeEnum);
            CC.sendMessage(player, String.format(SuccessMessages.TYPE_UPDATED, kit.getName(), typeEnum.name()));
        } catch (IllegalArgumentException ignored) {
            CC.sendMessage(player, ErrorMessages.KIT_TYPE_DOES_NOT_EXIST);
        }
    }

    @Subcommand("setranked")
    public void ranked(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, ErrorMessages.INVALID_KIT_NAME);
            return;
        }

        kit.setRanked(!kit.isRanked());
        CC.sendMessage(player, String.format(SuccessMessages.RANKED_TOGGLE, kit.isRanked() ? "enabled" : "disabled", kit.getColor() + kit.getName()));
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        sender.sendMessage(CC.translate("&b&lElectron &7| &fKits"));
        sender.sendMessage(CC.translate(" "));

        for (Kit kit : instance.getKitManager().getKits().values()) {
            sender.sendMessage(CC.translate("&7▪ &b" + kit.getColor() + kit.getName() + "&7 &f- &f" + kit.getDescription()));
        }
    }

    @Subcommand("setDescription")
    public void setDescription(CommandSender sender, @Name("kit") @Single String kitName, String[] descriptionArgs) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            CC.sendMessage(sender, ErrorMessages.INVALID_KIT_NAME);
            return;
        }

        String descriptionLine = String.join(" ", descriptionArgs);
        List<String> lore = new ArrayList<>();
        lore.add(descriptionLine);

        kit.setDescription(lore);
        CC.sendMessage(sender, "&aUpdated description of kit &b" + kit.getName() + " &ato: &f" + descriptionLine);
    }
}
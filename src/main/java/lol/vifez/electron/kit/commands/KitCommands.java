package lol.vifez.electron.kit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import lol.vifez.electron.kit.enums.KitType;
import lol.vifez.electron.util.CC;
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
        sender.sendMessage(CC.translate("&7Once you've made changes, run:"));
        sender.sendMessage(CC.translate("&b/kit save"));
        sender.sendMessage(CC.translate(" "));
        sender.sendMessage(CC.translate("&f • &b/kit create &f<kit> - &fCreate a new kit"));
        sender.sendMessage(CC.translate("&f • &b/kit delete &f<kit> - &fDelete an existing kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setType &f<kit> <type> - &fSet the type of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setInventory &f<kit> - &fSet the inventory of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit getInventory &f<kit> - &fGet the inventory of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setIcon &f<kit> - &fSet the icon for a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit setRanked &f<kit> - &fToggle whether a kit is ranked"));
        sender.sendMessage(CC.translate("&f • &b/kit setDescription &f<kit> <description> - &fSet the description of a kit"));
        sender.sendMessage(CC.translate("&f • &b/kit list - &fList all kits"));
        sender.sendMessage(CC.translate(" "));
    }

    @Subcommand("create")
    public void create(CommandSender sender, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit != null) {
            CC.sendMessage(sender, "&cError: This kit already exists");
            return;
        }

        kit = new Kit(kitSingle);
        kit.setIcon(Material.BOOK);
        instance.getKitManager().save(kit);

        CC.sendMessage(sender, "&aKit &b" + kit.getColor() + kit.getName() + " &ahas been created");
    }

    @Subcommand("delete")
    public void delete(CommandSender sender, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit == null) {
            CC.sendMessage(sender, "&cThis kit does not exist.");
            return;
        }

        instance.getKitManager().delete(kit);
        CC.sendMessage(sender, "&aKit &b" + kit.getColor() + kit.getName() + " &ahas been deleted");
    }

    @Subcommand("save")
    @Description("Save all kits to kits.yml")
    public void save(CommandSender sender) {
        instance.getKitManager().saveAll();
        CC.sendMessage(sender, "&aAll kits saved.");
    }

    @Subcommand("setInventory")
    public void setInventory(Player player, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, "&cInvalid kit name.");
            return;
        }

        kit.setArmorContents(player.getInventory().getArmorContents());
        kit.setContents(player.getInventory().getContents());

        instance.getKitManager().save(kit);

        CC.sendMessage(player, "&fUpdated the kit layout for &b" + kit.getName());
    }

    @Subcommand("getInventory")
    public void getInventory(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, "&cInvalid kit name.");
            return;
        }

        player.getInventory().clear();

        player.getInventory().setContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmorContents());

        CC.sendMessage(player, "&fReceived the kit layout for &b" + kit.getName());
    }

    @Subcommand("setIcon")
    public void setIcon(Player player, @Name("kit") @Single String kitSingle) {
        Kit kit = instance.getKitManager().getKit(kitSingle.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, "&cInvalid kit name.");
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInHand();

        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            CC.sendMessage(player, "&cThere is nothing in your hand...");
            return;
        }

        kit.setIcon(itemInHand.getType());
        instance.getKitManager().save(kit);
        CC.sendMessage(player, "&fUpdated the icon for &b" + kit.getName());
    }

    @Subcommand("setType")
    public void setType(Player player, @Name("kit") @Single String kitName, @Name("type") @Single String type) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            CC.sendMessage(player, "&cInvalid kit name.");
            return;
        }

        try {
            KitType typeEnum = KitType.valueOf(type.toUpperCase());
            kit.setKitType(typeEnum);
            CC.sendMessage(player, "&fNew type for &b" + kit.getName() + "&f: &b" + typeEnum.name());
            instance.getKitManager().save(kit);
        } catch (IllegalArgumentException ignored) {
            CC.sendMessage(player, "&cInvalid kit type, Please use one of the following:");
            CC.sendMessage(player, "&f• REGULAR");
            CC.sendMessage(player, "&f• BUILD");
            CC.sendMessage(player, "&f• BOXING");
            CC.sendMessage(player, "&f• WATER_KILL");
        }
    }

    @Subcommand("setRanked")
    public void ranked(Player player, @Name("kit") @Single String kitName) {
        Kit kit = instance.getKitManager().getKit(kitName.toLowerCase());

        if (kit == null) {
            CC.sendMessage(player, "&cInvalid kit name.");
            return;
        }

        kit.setRanked(!kit.isRanked());
        CC.sendMessage(player, "&aYou have " + (kit.isRanked() ? "&aenabled" : "&cdisabled") + " &aranked for kit &b" + kit.getName());
        instance.getKitManager().save(kit);
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        sender.sendMessage(CC.translate("&b&lElectron &7| &fKits"));
        sender.sendMessage(CC.translate(" "));

        for (Kit kit : instance.getKitManager().getKits().values()) {
            sender.sendMessage(CC.translate("&f • " + kit.getColor() + kit.getName() + "&7 - &f" + kit.getDescription()));
        }
    }

    @Subcommand("setDescription")
    public void setDescription(CommandSender sender, @Name("kit") @Single String kitName, String[] descriptionArgs) {
        Kit kit = instance.getKitManager().getKit(kitName);

        if (kit == null) {
            CC.sendMessage(sender, "&cInvalid kit name.");
            return;
        }

        String descriptionLine = String.join(" ", descriptionArgs);
        List<String> lore = new ArrayList<>();
        lore.add(descriptionLine);

        kit.setDescription(lore);
        instance.getKitManager().save(kit);
        CC.sendMessage(sender, "&aUpdated description of kit &b" + kit.getName() + " &ato: &f" + descriptionLine);
    }
}
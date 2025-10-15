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
        sender.sendMessage(CC.translate("&7▪ &b/kit create &7<kit> &f- &fCreate a new kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit delete &7<kit> &f- &fDelete an existing kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setType &7<kit> <type> &f- &fSet the type of a kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit setInventory &7<kit> &f- &fSet the inventory of a kit"));
        sender.sendMessage(CC.translate("&7▪ &b/kit getInventory &7<kit> &f- &fGet the inventory of a kit"));
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
            CC.sendMessage(player, "&7▪ REGULAR");
            CC.sendMessage(player, "&7▪ BUILD");
            CC.sendMessage(player, "&7▪ BOXING");
            CC.sendMessage(player, "&7▪ WATER_KILL");
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
            sender.sendMessage(CC.translate("&7▪ &b" + kit.getColor() + kit.getName() + "&7 &f- &f" + kit.getDescription()));
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
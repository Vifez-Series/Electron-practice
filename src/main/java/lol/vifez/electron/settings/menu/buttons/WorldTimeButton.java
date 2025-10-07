package lol.vifez.electron.settings.menu.buttons;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.settings.menu.SettingsMenu;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ItemBuilder;
import lol.vifez.electron.util.menu.button.Button;
import lol.vifez.electron.util.menu.button.impl.EasyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.World;

import java.util.Arrays;

public class WorldTimeButton {

    public static Button createWorldTimeButton(Profile profile, Practice instance) {
        String worldTime = profile.getWorldTime();
        ItemStack worldTimeItem = new ItemBuilder(Material.NETHER_STAR)
                .name(CC.translate("&eWorld Time"))
                .lore(Arrays.asList(
                        CC.translate("&7Change the time (sky)"),
                        CC.translate("&7Current value: " + (worldTime.equals("DAY") ? "&aDay" : worldTime.equals("SUNSET") ? "&aSunset" : "&aNight")),
                        CC.translate("&r"),
                        CC.translate("&eClick to change!")
                ))
                .build();

        return new EasyButton(worldTimeItem, true, false, () -> {
            String newTime;
            if (profile.getWorldTime().equals("DAY")) {
                newTime = "SUNSET";
            } else if (profile.getWorldTime().equals("SUNSET")) {
                newTime = "NIGHT";
            } else {
                newTime = "DAY";
            }

            profile.setWorldTime(newTime);
            Practice.getInstance().getProfileManager().save(profile);

            Player player = profile.getPlayer();
            if (player != null) {
                updateWorldTime(player, newTime);
            }

            new SettingsMenu(instance, profile).openMenu(player);
        });
    }

    private static void updateWorldTime(Player player, String time) {
        World world = player.getWorld();

        switch (time) {
            case "DAY":
                world.setTime(1000);
                break;
            case "SUNSET":
                world.setTime(12500);
                break;
            case "NIGHT":
                world.setTime(18000);
                break;
            default:
                break;
        }
    }
}
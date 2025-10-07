package lol.vifez.electron.divisions.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import lol.vifez.electron.divisions.menus.DivisionsMenu;
import org.bukkit.entity.Player;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@CommandAlias("divisions")
public class DivisionsCommand extends BaseCommand {

    @Default
    public void onDivisions(Player player) {
        new DivisionsMenu().openMenu(player);
    }
}
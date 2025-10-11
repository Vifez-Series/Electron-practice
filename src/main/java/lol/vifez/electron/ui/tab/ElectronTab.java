package lol.vifez.electron.ui.tab;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.StringUtil;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.api.tablist.adapter.TabAdapter;
import xyz.refinedev.api.tablist.setup.TabEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class ElectronTab implements TabAdapter {

    private final Practice instance;
    private final boolean isPlaceholderAPI;

    @Override
    public String getHeader(Player player) {
        return StringUtil.listToString(
                instance.getTabFile().getConfiguration().getStringList("header")
        );
    }

    @Override
    public String getFooter(Player player) {
        return StringUtil.listToString(
                instance.getTabFile().getConfiguration().getStringList("footer")
        );
    }

    @Override
    public List<TabEntry> getLines(Player player) {
        List<TabEntry> entries = new ArrayList<>();

        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        int index = 0;

        for (Player online : players) {
            int col = index / 20;
            int row = index % 20;

            if (col > 3) break;

            String name = "&a" + online.getName();
            name = parse(player, name);

            entries.add(new TabEntry(col, row, name));
            index++;
        }

        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 20; row++) {
                final int slotIndex = col * 20 + row;
                if (slotIndex >= index) {
                    entries.add(new TabEntry(col, row, ""));
                }
            }
        }

        return entries;
    }

    private String parse(Player player, String str) {
        String line = CC.colorize(str);
        return isPlaceholderAPI ? PlaceholderAPI.setPlaceholders(player, line) : line;
    }
}
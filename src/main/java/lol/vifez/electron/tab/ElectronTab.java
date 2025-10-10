package lol.vifez.electron.tab;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.Profile;
import lol.vifez.electron.ranks.Rank;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.StringUtil;
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

public class ElectronTab implements TabAdapter {

    private final Practice instance;
    private final boolean isPlaceholderAPI;
    private final lol.vifez.electron.scoreboard.AnimationManager animationManager;

    public ElectronTab(Practice instance, boolean isPlaceholderAPI) {
        this.instance = instance;
        this.isPlaceholderAPI = isPlaceholderAPI;
        this.animationManager = new lol.vifez.electron.scoreboard.AnimationManager();
    }

    @Override
    public String getHeader(Player player) {
        String header = StringUtil.listToString(
                instance.getTabFile().getConfiguration().getStringList("header")
        );
        return header.replace("%animation%", animationManager.getCurrentFrame("main"));
    }

    @Override
    public String getFooter(Player player) {
        String footer = StringUtil.listToString(
                instance.getTabFile().getConfiguration().getStringList("footer")
        );
        return footer.replace("%footer_animation%", animationManager.getCurrentFrame("footer"));
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

            String name = online.getName();
            Profile onlineProfile = instance.getProfileManager().getProfile(online.getUniqueId());
            
            if (onlineProfile != null) {
                Rank rank = instance.getRankManager().getPlayerRank(online);
                if (rank != null) {
                    name = rank.getFormattedPrefix() + rank.getColor() + name;
                } else {
                    name = "&a" + name;
                }
            } else {
                name = "&a" + name;
            }
            
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
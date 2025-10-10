package lol.vifez.electron.util.nametag.adapter;

import lol.vifez.electron.Practice;
import lol.vifez.electron.ranks.Rank;
import org.bukkit.entity.Player;

public class RankNametagAdapter implements NameTagAdapter {
    
    private final Practice practice;

    public RankNametagAdapter(Practice practice) {
        this.practice = practice;
    }

    @Override
    public String getPrefix(Player player) {
        Rank rank = practice.getRankManager().getPlayerRank(player);
        return rank != null ? rank.getFormattedPrefix() : "";
    }

    @Override
    public String getSuffix(Player player) {
        return "";
    }
}
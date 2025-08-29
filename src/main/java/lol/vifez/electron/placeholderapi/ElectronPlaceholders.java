package lol.vifez.electron.placeholderapi;

import lol.vifez.electron.Practice;
import lol.vifez.electron.elo.EloUtil;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@RequiredArgsConstructor
public class ElectronPlaceholders extends PlaceholderExpansion {

    private final Practice instance;

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String param = params.toLowerCase().replace("practice_", "");

        if (param.equals("elo_global")) {
            return String.valueOf(EloUtil.getGlobalElo(instance.getProfileManager().getProfile(player.getUniqueId())));
        }

        if (param.startsWith("elo_")) {
            String kitName = param.replace("elo_", "");

            return String.valueOf(instance.getProfileManager().getProfile(player.getUniqueId()).getElo(instance.getKitManager().getKit(kitName)));
        }

        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "practice";
    }

    @Override
    public @NotNull String getAuthor() {
        return "vifez";
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getDescription().getVersion();
    }
}

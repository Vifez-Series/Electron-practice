package lol.vifez.electron.match.event;

import lol.vifez.electron.match.Match;
import lol.vifez.electron.profile.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */
@Getter
@RequiredArgsConstructor
public class MatchStartEvent extends Event {

    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Profile profileOne, profileTwo;
    private final Match match;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

package lol.vifez.electron.game.kit.enums;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public enum KitType {
    REGULAR,
    BUILD,
    BED_FIGHT,
    BOXING,
    WATER_KILL;

    public KitType[] getAll() {
        return new KitType[] {REGULAR, BUILD, BED_FIGHT, BOXING, WATER_KILL};
    }
}
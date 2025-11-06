package lol.vifez.electron.kit.enums;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
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
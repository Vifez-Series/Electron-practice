package lol.vifez.electron.arena;

import lol.vifez.electron.Practice;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@Data
public class Arena {

    private final String name;
    private String type;
    private Location spawnA;
    private Location spawnB;
    private Material icon;

    private List<String> kits = new ArrayList<>();
    private boolean busy = false;

    private List<Block> blocksBuilt = new ArrayList<>();
    private Map<Block, Material> blockBroken = new HashMap<>();

    private Location positionOne;
    private Location positionTwo;

    public Arena(String name) {
        this.name = name;
        this.type = "default";
        this.icon = Material.PAPER;
        this.kits = new ArrayList<>();
        this.busy = false;
        this.blocksBuilt = new ArrayList<>();
        this.blockBroken = new HashMap<>();
        this.positionOne = null;
        this.positionTwo = null;
    }

    public Arena(String name, String type, String spawnA, String spawnB, String icon, String positionOne, String positionTwo) {
        this.name = name;
        this.type = type;
        this.icon = Material.getMaterial(icon);

        this.spawnA = parseLocation(spawnA);
        this.spawnB = parseLocation(spawnB);
        this.positionOne = parseLocation(positionOne);
        this.positionTwo = parseLocation(positionTwo);
    }

    private Location parseLocation(String locationStr) {
        if (locationStr == null || locationStr.isEmpty()) return null;
        String[] parts = locationStr.split(",");
        if (parts.length < 6) return null;

        org.bukkit.World world = org.bukkit.Bukkit.getWorld(parts[0]);
        if (world == null) return null;

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void fixArena() {
        blocksBuilt.stream().filter(Objects::nonNull).forEach(block -> block.setType(Material.AIR));
        blocksBuilt.clear();

        blockBroken.forEach((block, material) -> block.setType(material));
        blockBroken.clear();
    }

    public void kitsCleanup() {
        Practice instance = JavaPlugin.getPlugin(Practice.class);

        kits.removeIf(str -> instance.getKitManager().getKit(str.toLowerCase()) == null);
    }
}
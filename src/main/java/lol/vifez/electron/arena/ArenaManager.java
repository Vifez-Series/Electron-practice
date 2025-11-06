package lol.vifez.electron.arena;

import lol.vifez.electron.Practice;
import lol.vifez.electron.kit.Kit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class ArenaManager {

    private final Map<String, Arena> arenas = new ConcurrentHashMap<>();

    public ArenaManager() {
        ConfigurationSection section = Practice.getInstance().getArenasFile()
                .getConfiguration()
                .getConfigurationSection("arenas");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                String name = key.toLowerCase();
                String type = section.getString(name + ".type");
                String spawnA = section.getString(name + ".spawnA");
                String spawnB = section.getString(name + ".spawnB");
                String icon = section.getString(name + ".icon");
                String positionOne = section.getString(name + ".positionOne");
                String positionTwo = section.getString(name + ".positionTwo");

                Arena arena = new Arena(name, type, spawnA, spawnB, icon, positionOne, positionTwo);
                arena.setKits(section.getStringList(name + ".kits"));
                arenas.put(name, arena);
            });
        }
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Set<Arena> getAllAvailableArenas(Kit kit) {
        return CompletableFuture.supplyAsync(() -> arenas.values().stream()
                        .filter(arena -> arena.getKits().contains(kit.getName().toLowerCase()) && !arena.isBusy())
                        .collect(Collectors.toSet()))
                .join();
    }

    public Arena getAvailableArena(Kit kit) {
        Set<Arena> available = getAllAvailableArenas(kit);
        if (available.isEmpty()) return null;

        int index = new Random().nextInt(available.size());
        return available.stream().skip(index).findFirst().orElse(null);
    }

    public void save(Arena arena) {
        arenas.putIfAbsent(arena.getName().toLowerCase(), arena);
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public void delete(Arena arena) {
        arenas.remove(arena.getName().toLowerCase());

        Practice.getInstance().getArenasFile().getConfiguration()
                .set("arenas." + arena.getName().toLowerCase(), null);
        Practice.getInstance().getArenasFile().save();
    }

    public void close() {
        arenas.values().forEach(arena -> {
            String path = "arenas." + arena.getName().toLowerCase();
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".type", arena.getType());
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".spawnA", formatLocation(arena.getSpawnA()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".spawnB", formatLocation(arena.getSpawnB()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".icon", arena.getIcon().toString());
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".positionOne", formatLocation(arena.getPositionOne()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".positionTwo", formatLocation(arena.getPositionTwo()));
            Practice.getInstance().getArenasFile().getConfiguration().set(path + ".kits", arena.getKits());
        });
        Practice.getInstance().getArenasFile().save();
    }

    private String formatLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }
}
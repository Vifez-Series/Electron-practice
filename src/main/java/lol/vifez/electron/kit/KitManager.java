package lol.vifez.electron.kit;

import lol.vifez.electron.Practice;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

@Getter
public class KitManager {

    private final Map<String, Kit> kits;

    public KitManager() {
        this.kits = new ConcurrentHashMap<>();

        ConfigurationSection section = Practice.getInstance().getKitsFile().getConfiguration().getConfigurationSection("kits");

        if (section != null) {
            section.getKeys(false).forEach(key -> {
                ConfigurationSection kitSection = section.getConfigurationSection(key);
                if (kitSection != null) {
                    Kit kit = Kit.fromConfig(key, kitSection);
                    kits.put(key.toLowerCase(), kit);
                }
            });
        }
    }

    public Kit getKit(String name) {
        return kits.get(name.toLowerCase());
    }

    public void save(Kit kit) {
        kits.putIfAbsent(kit.getName().toLowerCase(), kit);
    }

    public void delete(Kit kit) {
        kits.remove(kit.getName().toLowerCase());

        Practice.getInstance().getKitsFile().getConfiguration().set("kits." + kit.getName().toLowerCase(), null);
        Practice.getInstance().getKitsFile().save();
    }

    public void close() {
        ConfigurationSection section = Practice.getInstance().getKitsFile().getConfiguration().createSection("kits");

        kits.values().forEach(kit -> {
            ConfigurationSection kitSection = section.createSection(kit.getName().toLowerCase());
            kit.toConfig(kitSection);
        });

        Practice.getInstance().getKitsFile().save();
    }
}
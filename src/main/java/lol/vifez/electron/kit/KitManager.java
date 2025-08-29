package lol.vifez.electron.kit;

import lol.vifez.electron.Practice;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
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
        kits.put(kit.getName().toLowerCase(), kit);

        ConfigurationSection section = Practice.getInstance().getKitsFile().getConfiguration()
                .createSection("kits." + kit.getName().toLowerCase());
        kit.toConfig(section);
        Practice.getInstance().getKitsFile().save();
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
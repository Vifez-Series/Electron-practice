package lol.vifez.electron.hotbar;

import lol.vifez.electron.Practice;
import lol.vifez.electron.util.ConfigFile;
import lol.vifez.electron.util.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@Getter
public enum Hotbar {
    QUEUES("HOTBAR.LOBBY.QUEUES"),
    UNRANKED("HOTBAR.LOBBY.UNRANKED"),
    RANKED("HOTBAR.LOBBY.RANKED"),
    LEADERBOARDS("HOTBAR.LOBBY.LEADERBOARDS"),
    KIT_EDITOR("HOTBAR.LOBBY.KIT-EDITOR"),
    SETTINGS("HOTBAR.LOBBY.SETTINGS"),
    NAVIGATOR("HOTBAR.LOBBY.NAVIGATOR"),
    LEAVE_QUEUE("HOTBAR.IN-QUEUE.LEAVE_QUEUE");

    private final String path;
    private ItemStack item;

    public void load() {
        if (path == null) return;
        ConfigFile hotbarFile = new ConfigFile(Practice.getInstance(), "hotbar.yml");

        Material mat = Material.valueOf(hotbarFile.getString(path + ".MATERIAL"));
        String name = hotbarFile.getString(path + ".NAME");
        boolean enabled = hotbarFile.getBoolean(path + ".ENABLED");

        this.item = enabled ? new ItemBuilder(mat).name(name).build() : null;
    }

    public static void loadAll() {
        for (Hotbar hotbar : values()) hotbar.load();
    }

    public static ItemStack[] getSpawnItems() {
        ConfigFile hotbarFile = new ConfigFile(Practice.getInstance(), "hotbar.yml");
        ItemStack[] items = new ItemStack[9];

        for (Hotbar hotbar : values()) {
            if (hotbar.item == null || !hotbar.path.contains("LOBBY")) continue;

            int slot = hotbarFile.getInt(hotbar.path + ".SLOT") - 1;
            if (slot < 0 || slot >= 9) continue;
            items[slot] = hotbar.item;
        }
        return items;
    }

    public static ItemStack[] getQueueItems() {
        ConfigFile hotbarFile = new ConfigFile(Practice.getInstance(), "hotbar.yml");
        ItemStack[] items = new ItemStack[9];

        for (Hotbar hotbar : values()) {
            if (hotbar.item == null || !hotbar.path.contains("IN-QUEUE")) continue;

            int slot = hotbarFile.getInt(hotbar.path + ".SLOT") - 1;
            if (slot < 0 || slot >= 9) continue;
            items[slot] = hotbar.item;
        }
        return items;
    }

}
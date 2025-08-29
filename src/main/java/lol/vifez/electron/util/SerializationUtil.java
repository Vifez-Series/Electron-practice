package lol.vifez.electron.util;

import com.google.gson.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@UtilityClass
public class SerializationUtil {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new JsonSerializer<ItemStack>() {
                @Override
                public JsonElement serialize(ItemStack item, java.lang.reflect.Type type, JsonSerializationContext context) {
                    JsonObject obj = new JsonObject();
                    if (item == null || item.getType() == Material.AIR) return JsonNull.INSTANCE;
                    obj.addProperty("type", item.getType().name());
                    obj.addProperty("amount", item.getAmount());
                    return obj;
                }
            })
            .registerTypeAdapter(ItemStack.class, new JsonDeserializer<ItemStack>() {
                @Override
                public ItemStack deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
                    if (json.isJsonNull()) return new ItemStack(Material.AIR);
                    JsonObject obj = json.getAsJsonObject();
                    Material mat = Material.valueOf(obj.get("type").getAsString());
                    int amount = obj.get("amount").getAsInt();
                    return new ItemStack(mat, amount);
                }
            })
            .create();

    public String serializeItemStackArray(ItemStack[] items) {
        if (items == null) return "[]";
        return gson.toJson(items);
    }

    public ItemStack[] deserializeItemStackArray(String data) {
        if (data == null || data.isEmpty()) return new ItemStack[0];
        try {
            return gson.fromJson(data, ItemStack[].class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }

    public String serializeLocation(Location loc) {
        if (loc == null || loc.getWorld() == null) return null;
        return loc.getWorld().getName() + "," +
                loc.getX() + "," +
                loc.getY() + "," +
                loc.getZ() + "," +
                loc.getYaw() + "," +
                loc.getPitch();
    }

    public Location deserializeLocation(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] parts = data.split(",");
        if (parts.length < 6) return null;

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;

        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
package lol.vifez.electron.profile;

import lol.vifez.electron.Practice;
import lol.vifez.electron.profile.listener.ProfileListener;
import lol.vifez.electron.profile.repository.ProfileRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

@Getter
public class ProfileManager {

    private final ProfileRepository profileRepository;
    private final Map<UUID, Profile> profiles;

    public ProfileManager(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
        this.profiles = new ConcurrentHashMap<>();

        new ProfileListener(JavaPlugin.getPlugin(Practice.class));
    }

    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public Profile getProfile(String name) {
        return profiles.values().stream().filter(profile -> profile.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void save(Profile profile) {
        profiles.putIfAbsent(profile.getUuid(), profile);
    }

    public void delete(Profile profile) {
        profiles.remove(profile.getUuid());
        profileRepository.deleteData(profile.getUuid().toString());
    }

    public void close() {
        // Save all profiles in memory to the database if available
        if (profileRepository != null) {
            profiles.values().forEach(profile -> 
                profileRepository.saveData(profile.getUuid().toString(), profile));
        }
    }
}

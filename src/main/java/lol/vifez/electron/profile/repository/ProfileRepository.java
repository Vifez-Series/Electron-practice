package lol.vifez.electron.profile.repository;

import com.google.gson.Gson;
import lol.vifez.electron.storage.MongoAPI;
import lol.vifez.electron.storage.repository.MongoRepository;
import lol.vifez.electron.profile.Profile;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class ProfileRepository extends MongoRepository<Profile> {

    public ProfileRepository(MongoAPI mongoAPI, Gson gson) {
        super(mongoAPI, gson);

        setCollection(mongoAPI.getCollection("profile"));
    }
}

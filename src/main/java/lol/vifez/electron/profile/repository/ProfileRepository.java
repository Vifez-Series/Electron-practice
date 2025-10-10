package lol.vifez.electron.profile.repository;

import com.google.gson.Gson;
import lol.vifez.electron.mongo.MongoAPI;
import lol.vifez.electron.mongo.repository.MongoRepository;
import lol.vifez.electron.profile.Profile;

/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
 */

public class ProfileRepository extends MongoRepository<Profile> {

    public ProfileRepository(MongoAPI mongoAPI, Gson gson) {
        super(mongoAPI, gson);
        
        if (mongoAPI != null && mongoAPI.isConnected()) {
            setCollection(mongoAPI.getCollection("profile"));
        }
    }
}

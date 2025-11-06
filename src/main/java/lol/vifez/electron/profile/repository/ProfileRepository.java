package lol.vifez.electron.profile.repository;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lol.vifez.electron.mongo.MongoAPI;
import lol.vifez.electron.mongo.repository.MongoRepository;
import lol.vifez.electron.profile.Profile;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;

public class ProfileRepository extends MongoRepository<Profile> {

    public ProfileRepository(MongoAPI mongoAPI, Gson gson) {
        super(mongoAPI, gson);
        setCollection(mongoAPI.getDatabase().getCollection("profiles"));
    }

    @Override
    public CompletableFuture<Void> saveData(String id, Profile profile) {
        return CompletableFuture.runAsync(() -> {
            try {
                Document doc = Document.parse(getGson().toJson(profile));

                doc.put("_id", id);
                doc.put("uuid", id);

                getCollection().replaceOne(
                        Filters.eq("_id", id),
                        doc,
                        new ReplaceOptions().upsert(true)
                );
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[Mongo] Failed to save profile " + id + ": " + e.getMessage());
            }
        });
    }
}
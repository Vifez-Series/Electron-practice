package lol.vifez.electron.profile.repository;

import com.google.gson.Gson;
import lol.vifez.electron.mongo.MongoAPI;
import lol.vifez.electron.mongo.repository.MongoRepository;
import lol.vifez.electron.profile.Profile;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public class ProfileRepository extends MongoRepository<Profile> {

    public ProfileRepository(MongoAPI mongoAPI, Gson gson) {
        super(mongoAPI, gson);
        setCollection(mongoAPI.getDatabase().getCollection("profiles"));
    }
}
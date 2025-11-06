package lol.vifez.electron.mongo.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import lol.vifez.electron.mongo.MongoAPI;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@Getter
@Setter
public abstract class MongoRepository<T> {

    private final MongoAPI mongoAPI;
    private final Gson gson;
    private final UpdateOptions updateOptions;
    private MongoCollection<Document> collection;

    public MongoRepository(MongoAPI mongoAPI, Gson gson) {
        this.mongoAPI = mongoAPI;
        this.gson = gson;
        this.updateOptions = new UpdateOptions().upsert(true);
    }

    public CompletableFuture<T> getData(String id, Type type) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection.find(Filters.eq("_id", id)).first();
            return document == null ? null : gson.fromJson(document.toJson(), type);
        });
    }

    public CompletableFuture<T> getData(String key, Object value, Type type) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection.find(Filters.eq(key, value)).first();
            return document == null ? null : gson.fromJson(document.toJson(), type);
        });
    }

    public CompletableFuture<List<T>> getAll(Type type) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> result = new ArrayList<>();
            for (Document document : collection.find()) {
                result.add(gson.fromJson(document.toJson(), type));
            }
            return result;
        });
    }

    public CompletableFuture<Void> saveData(String id, T t) {
        return CompletableFuture.runAsync(() ->
                collection.replaceOne(
                        Filters.eq("_id", id),
                        Document.parse(gson.toJson(t)),
                        new ReplaceOptions().upsert(true)
                )
        );
    }

    public CompletableFuture<Void> deleteData(String id) {
        return CompletableFuture.runAsync(() ->
                collection.deleteOne(Filters.eq("_id", id))
        );
    }
}
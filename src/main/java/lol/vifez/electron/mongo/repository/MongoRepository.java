package lol.vifez.electron.mongo.repository;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lol.vifez.electron.mongo.MongoAPI;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public abstract class MongoRepository<T> {

    private MongoAPI mongoAPI;
    private Gson gson;
    private UpdateOptions updateOptions;
    private MongoCollection<Document> collection;

    public MongoRepository(MongoAPI mongoAPI, Gson gson) {
        this.mongoAPI = mongoAPI;
        this.gson = gson;
        this.updateOptions = new UpdateOptions().upsert(true);
    }

    /**
     * Retrieves a document by the provided ID.
     *
     * @param id   The document's ID.
     * @param type The type of the object to be returned.
     * @return A CompletableFuture with the deserialized object.
     */
    public CompletableFuture<T> getData(String id, Type type) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.collection == null) return null;
            Document document = this.collection.find(Filters.eq("_id", id)).first();
            if (document == null) return null;
            return gson.fromJson(document.toJson(), type);
        });
    }

    /**
     * Retrieves a document by the provided key-value pair.
     *
     * @param key   The key to search by.
     * @param value The value to search for.
     * @param type  The type of the object to be returned.
     * @return A CompletableFuture with the deserialized object.
     */
    public CompletableFuture<T> getData(String key, Object value, Type type) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.collection == null) return null;
            Document document = this.collection.find(Filters.eq(key, value)).first();
            if (document == null) return null;
            return gson.fromJson(document.toJson(), type);
        });
    }

    /**
     * Retrieves all documents and returns them as a list of CompletableFutures.
     *
     * @param type The type of the object to be returned for each document.
     * @return A CompletableFuture that contains a list of the deserialized objects.
     */
    public CompletableFuture<List<T>> getAll(Type type) {
        return CompletableFuture.supplyAsync(() -> {
            List<T> resultList = new ArrayList<>();
            if (this.collection == null) return resultList;
            for (Document document : this.collection.find()) {
                T data = gson.fromJson(document.toJson(), type);
                resultList.add(data);
            }
            return resultList;
        });
    }

    /**
     * Saves or replaces a document with the provided ID.
     * If MongoDB is not available, this operation is silently ignored.
     *
     * @param id   The ID of the document.
     * @param t    The object to be saved (serialized to JSON).
     */
    public CompletableFuture<Void> saveData(String id, T t) {
        return CompletableFuture.runAsync(() -> {
            if (this.collection != null) {
                Document doc = Document.parse(gson.toJson(t));
                collection.findOneAndReplace(
                    Filters.eq("_id", id),
                    doc,
                    new com.mongodb.client.model.FindOneAndReplaceOptions().upsert(true)
                );
            }
        });
    }

    /**
     * Deletes the document by the provided ID.
     * If MongoDB is not available, this operation is silently ignored.
     *
     * @param id The ID of the document to delete.
     */
    public CompletableFuture<Void> deleteData(String id) {
        return CompletableFuture.runAsync(() -> {
            if (this.collection != null) {
                this.collection.deleteOne(Filters.eq("_id", id));
            }
        });
    }
}
package lol.vifez.electron.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Collections;

@Getter
public class MongoAPI {

    private final MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private final MongoCredentials credentials;

    public MongoAPI(MongoCredentials credentials) {
        this.credentials = credentials;

        if (credentials.isUseUri() && credentials.getUri() != null && !credentials.getUri().isEmpty()) {
            mongoClient = new MongoClient(new MongoClientURI(credentials.getUri()));
            mongoDatabase = mongoClient.getDatabase(credentials.getDatabase());
        } else {
            ServerAddress serverAddress = new ServerAddress(credentials.getHost(), credentials.getPort());
            if (credentials.isAuth()) {
                MongoCredential credential = MongoCredential.createCredential(
                    credentials.getUser(), 
                    credentials.getDatabase(), 
                    credentials.getPassword().toCharArray()
                );
                MongoClientOptions options = MongoClientOptions.builder().build();
                mongoClient = new MongoClient(serverAddress, credential, options);
            } else {
                mongoClient = new MongoClient(serverAddress);
            }
            mongoDatabase = mongoClient.getDatabase(credentials.getDatabase());
        }

    }

    public MongoCollection<Document> getCollection(String name) {
        return this.mongoDatabase.getCollection(name);
    }

    public FindIterable<Document> find(String collectionName) {
        return this.getCollection(collectionName).find();
    }
}
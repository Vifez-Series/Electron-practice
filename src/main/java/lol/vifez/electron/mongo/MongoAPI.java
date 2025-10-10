package lol.vifez.electron.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoTimeoutException;
import com.mongodb.MongoSecurityException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Getter
public class MongoAPI {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private final MongoCredentials credentials;

    public MongoAPI(MongoCredentials credentials) {
        this.credentials = credentials;

        try {
            if (credentials.isUseUri() && credentials.getUri() != null && !credentials.getUri().isEmpty()) {
                mongoClient = new MongoClient(new MongoClientURI(credentials.getUri()));
            } else {
                ServerAddress serverAddress = new ServerAddress(credentials.getHost(), credentials.getPort());
                MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder()
                    .connectTimeout(5000)
                    .serverSelectionTimeout(5000)
                    .retryWrites(true);

                if (credentials.isAuth()) {
                    MongoCredential credential = MongoCredential.createCredential(
                        credentials.getUser(), 
                        credentials.getDatabase(), 
                        credentials.getPassword().toCharArray()
                    );
                    mongoClient = new MongoClient(serverAddress, credential, optionsBuilder.build());
                } else {
                    mongoClient = new MongoClient(serverAddress, optionsBuilder.build());
                }
            }

            mongoDatabase = mongoClient.getDatabase(credentials.getDatabase());
            mongoDatabase.runCommand(new org.bson.Document("ping", 1));
            
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Electron] Successfully connected to MongoDB database '" + credentials.getDatabase() + "'");
        } catch (Exception e) {
            String errorMsg = "";
            if (e instanceof MongoTimeoutException) {
                errorMsg = "Could not connect to MongoDB server. Please ensure MongoDB is running on " + 
                          credentials.getHost() + ":" + credentials.getPort();
            } else if (e instanceof MongoSecurityException) {
                errorMsg = "MongoDB authentication failed. Please check your credentials in database.yml";
            } else {
                errorMsg = "Failed to initialize MongoDB connection: " + e.getMessage();
            }
            
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Electron] " + errorMsg);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Electron] The plugin will continue to run, but database features will be disabled.");
        
            mongoClient = null;
            mongoDatabase = null;
        }

    }

    public MongoCollection<Document> getCollection(String name) {
        if (mongoDatabase == null) {
            return null;
        }
        return this.mongoDatabase.getCollection(name);
    }

    public FindIterable<Document> find(String collectionName) {
        if (mongoDatabase == null) {
            return null;
        }
        return this.getCollection(collectionName).find();
    }
    
    public boolean isConnected() {
        return mongoClient != null && mongoDatabase != null;
    }
}
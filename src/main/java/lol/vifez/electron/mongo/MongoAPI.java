package lol.vifez.electron.mongo;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@Getter
public class MongoAPI {

    private MongoClient client;
    private MongoDatabase database;
    private boolean connected;

    public MongoAPI(String uri, String databaseName) {
        try {
            this.client = MongoClients.create(uri);
            this.database = client.getDatabase(databaseName);
            this.connected = true;
        } catch (MongoTimeoutException e) {
            this.connected = false;
        } catch (Exception e) {
            this.connected = false;
        }
    }

    public void close() {
        if (client != null) client.close();
    }
}
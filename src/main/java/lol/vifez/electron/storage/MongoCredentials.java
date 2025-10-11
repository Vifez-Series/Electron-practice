package lol.vifez.electron.storage;

import lombok.Data;

@Data
public class MongoCredentials {

    private final String host;
    private final int port;
    private final String database, user, password;

    public boolean isAuth() {
        return (user != null && !user.isEmpty()) && (password != null && !password.isEmpty());
    }
}
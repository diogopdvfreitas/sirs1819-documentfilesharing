package a47.server.model;

import java.util.Date;

public class Session {
    private long token;

    private String username;

    private Date expirationDate;

    public Session(long token, String username, Date expirationDate) {
        this.token = token;
        this.username = username;
        this.expirationDate = expirationDate;
    }

    public long getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }
}

package a47.server.model.replication;

public class ServerInfo {
    private String URL;

    public ServerInfo(String URL) {
        this.URL = URL;
    }

    public ServerInfo() {
    }

    public String getURL() {
        return URL;
    }
}

package a47.server.model.replication;

import a47.server.model.File;
import a47.server.model.FileMetaData;
import a47.server.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ServerSnapshot {
    private Date timestamp;


    private HashMap<String, User> registeredUsers;


    private HashMap<String, List<String>> userFiles;

    private HashMap<String, FileMetaData> filesMetaData;

    private List<File> files;

    public ServerSnapshot(HashMap<String, User> registeredUsers, HashMap<String, List<String>> userFiles, HashMap<String, FileMetaData> filesMetaData, List<File> files) {
        this.timestamp = new Date();
        this.registeredUsers = registeredUsers;
        this.userFiles = userFiles;
        this.filesMetaData = filesMetaData;
        this.files = files;
    }

    public ServerSnapshot() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public HashMap<String, User> getRegisteredUsers() {
        return registeredUsers;
    }


    public HashMap<String, List<String>> getUserFiles() {
        return userFiles;
    }

    public HashMap<String, FileMetaData> getFilesMetaData() {
        return filesMetaData;
    }

    public List<File> getFiles() {
        return files;
    }
}

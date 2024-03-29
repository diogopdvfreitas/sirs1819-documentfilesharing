package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.model.response.UserFileResponse;
import a47.client.shell.service.DownloadFileService;
import a47.client.shell.service.ListFilesService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CheckUpdatesCommand extends AbstractCommand {

    public CheckUpdatesCommand(ClientShell sh, String name) {
        super(sh, name, "check updates given a fileID");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can upload files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to list files!");
            return;
        }

        if (args.length != 1) {
            shell.println(getUsage());
            return;
        }

        ListFilesService listFilesService = new ListFilesService();
        List<UserFileResponse> files = listFilesService.ListFiles(shell.getActiveSessionId());
        if(!ClientShell.isValidToken()){
            shell.println("Session expired, please login again");
            AuxMethods.logout(shell);
            return;
        }
        for(UserFileResponse file : files){
            if(file.getFileId().equals(args[0])){
                // CHECK IF FILES EXISTS ON FOLDER
                Path path = Paths.get(shell.getPathToDownload() + AuxMethods.getFileName(file));
                if(!Files.exists(path)){
                    shell.println("File: " + file.getFileName() + " not found on remote folder.");
                    return;
                }
                path = Paths.get(shell.getPathToDownload() + AuxMethods.getFileName(file) + ".metadata");
                if(!Files.exists(path)){
                    shell.println("Metadata from: " + file.getFileName() + " not found on remote folder.");
                    return;
                }

                DownloadFileService downloadFileService = new DownloadFileService();
                if(downloadFileService.checkUpdates(shell.getPathToDownload() + AuxMethods.getFileName(file) + ".metadata", shell.getActiveSessionId())){
                    shell.println("File: " + file.getFileName() + " has updates on server");
                    return;
                }
                if(!ClientShell.isValidToken()){
                    shell.println("Session expired, please login again");
                    AuxMethods.logout(shell);
                    return;
                }
                shell.println("File: " + file.getFileName() + " is up-to-date");
                return;
            }
        }
        shell.println("FileID not found");
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <fileid>";
    }
}

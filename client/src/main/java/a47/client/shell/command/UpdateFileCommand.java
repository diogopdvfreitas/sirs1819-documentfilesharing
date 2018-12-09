package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.model.response.UserFileResponse;
import a47.client.shell.service.ListFilesService;
import a47.client.shell.service.UpdateFileService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class UpdateFileCommand extends AbstractCommand {

    public UpdateFileCommand(ClientShell sh, String name) {
        super(sh, name, "update a new file");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can update files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to update files!");
            return;
        }

        if (args.length != 1) {
            shell.println(getUsage());
            return;
        }

        ListFilesService listFilesService = new ListFilesService();
        List<UserFileResponse> files = listFilesService.ListFiles(shell.getActiveSessionId());
        for(UserFileResponse file : files){
            if(file.getFileId().equals(args[0])){
                // CHECK IF FILES EXISTS ON FOLDER
                Path path = Paths.get(shell.getPathToDownload() + AuxMethods.getFileName(file));
                if(!Files.exists(path)){
                    shell.println("File: " + file.getFileName() + " not found.");
                    return;
                }
                path = Paths.get(shell.getPathToDownload() + AuxMethods.getFileName(file) + ".metadata");
                if(!Files.exists(path)){
                    shell.println("Metadata from: " + file.getFileName() + " not found.");
                    return;
                }

                UpdateFileService updateFileService = new UpdateFileService();
                try {
                    if(updateFileService.UpdateFile(args[0],shell.getPathToDownload() + AuxMethods.getFileName(file), shell.getActiveSessionId())){
                        shell.println("File updated");
                        return;
                    }
                    shell.println("File: " + file.getFileName() + " has updates on server. Please download the last version");
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                return;
            }
        }
        shell.println("FileID not found");
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <fileID>";
    }

}

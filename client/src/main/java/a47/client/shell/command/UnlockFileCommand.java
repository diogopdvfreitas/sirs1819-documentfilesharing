package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.model.response.UserFileResponse;
import a47.client.shell.service.ListFilesService;
import a47.client.shell.service.LockFileService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class UnlockFileCommand extends AbstractCommand {

    public UnlockFileCommand(ClientShell sh, String name) {
        super(sh, name, "unlock file on disk");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can upload files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to unlock files!");
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

                LockFileService lockFileService = new LockFileService();
                Path unlockedPath = lockFileService.UnlockFile(shell.getPathToDownload() + AuxMethods.getFileName(file), args[0], shell.getActiveSessionId());
                if(unlockedPath != null){
                    shell.println("File unlocked: " + unlockedPath + " . After you finish, please do not forget to lock again to ensure your file security");
                    return;
                }
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

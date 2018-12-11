package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.model.fileAbstraction.FileMetaData;
import a47.client.shell.service.ListFilesService;
import a47.client.shell.service.UpdateFileService;

import java.util.Set;

public class InfoCommand extends AbstractCommand {

    public InfoCommand(ClientShell sh, String name) {
        super(sh, name, "get info about file");
    }

    @Override
    void execute(String[] args) {
        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to get info about files!");
            return;
        }

        if (args.length != 1) {
            shell.println(getUsage());
            return;
        }

        ListFilesService listFilesService = new ListFilesService();
        FileMetaData fileMetaData = listFilesService.getInfo(shell.getActiveSessionId(), args[0]);

        UpdateFileService updateFileService = new UpdateFileService();
        Set<String> listAccess = updateFileService.getListAccess(shell.getActiveSessionId(), args[0]);

        if(!ClientShell.isValidToken()){
            shell.println("Session expired, please login again");
            AuxMethods.logout(shell);
            return;
        }

        if(fileMetaData != null && !listAccess.isEmpty()){
            shell.println("------------------------------------------------");
            shell.println("File ID: " + fileMetaData.getFileId());
            shell.println("Name: " + fileMetaData.getFileName());
            shell.println("Owner: " + fileMetaData.getOwner());
            shell.println("Last Modification: " + fileMetaData.getLastModifiedBy());
            shell.println("Shared with: ");
            for(String user : listAccess){
                shell.println("-> " + user);
            }
            shell.println("------------------------------------------------");
        }



    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

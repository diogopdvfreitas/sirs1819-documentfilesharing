package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.model.response.UserFileResponse;
import a47.client.shell.service.ListFilesService;

import java.util.List;

public class ListFilesCommand extends AbstractCommand {

    public ListFilesCommand(ClientShell sh, String name) {
        super(sh, name, "list files");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can upload files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to list files!");
            return;
        }
        ListFilesService listFilesService = new ListFilesService();
        List<UserFileResponse> files = listFilesService.ListFiles(shell.getActiveSessionId());
        if(!ClientShell.isValidToken()){
            shell.println("Session expired, please login again");
            AuxMethods.logout(shell);
            return;
        }
        shell.println("Name\t\tOwner\t\tLastModif.\tId");
        for(UserFileResponse file : files){
            shell.println(file.getFileName() + "\t\t" + file.getFileOwner() + "\t\t\t" + file.getLastMod()+ "\t\t" + file.getFileId());
        }
    }
}

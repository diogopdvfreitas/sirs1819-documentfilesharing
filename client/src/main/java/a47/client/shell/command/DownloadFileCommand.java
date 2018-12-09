package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.service.DownloadFileService;

import java.nio.file.Path;

public class DownloadFileCommand extends AbstractCommand {

    public DownloadFileCommand(ClientShell sh, String name) {
        super(sh, name, "download a new file");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can download files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to download files!");
            return;
        }

        if (args.length != 1) {
            shell.println(getUsage());
            return;
        }

        DownloadFileService downloadFileService = new DownloadFileService();
        Path store = downloadFileService.downloadFile(shell.getActiveUser(), shell.getPathToDownload(), args[0], shell.getActiveSessionId());
        if(!ClientShell.isValidToken()){
                shell.println("Session expired, please login again");
                AuxMethods.logout(shell);
                return;
        }
        if(store==null){
            shell.println("Problem with download file");
            return;
        }
        shell.println("File downloaded: " + store);
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <fileID>";
    }

}

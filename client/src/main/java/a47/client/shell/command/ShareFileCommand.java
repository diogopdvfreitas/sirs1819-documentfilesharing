package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.service.ShareFileService;

public class ShareFileCommand extends AbstractCommand {

    public ShareFileCommand(ClientShell sh, String name) {
        super(sh, name, "share file with another user");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can upload files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to share files!");
            return;
        }

        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }

        ShareFileService shareFileService = new ShareFileService();
        if(shareFileService.shareFile(shell.getActiveUser(), args[1], shell.getActiveSessionId(), args[0])){
            shell.println("File: "+ args[0] + " is now shared with: "+ args[1]);
            return;
        }
        if(!ClientShell.isValidToken()){
            shell.println("Session expired, please login again");
            AuxMethods.logout(shell);
            return;
        }
        shell.println("Error sharing with: " + args[1]+". Maybe already shared."); //TODO ver o erro corretamente
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <fileid> <userToShare>";
    }

}

package a47.client.shell.command;

import a47.client.shell.ClientShell;
import a47.client.shell.service.ShareFileService;

public class UnShareFileCommand extends AbstractCommand {

    public UnShareFileCommand(ClientShell sh, String name) {
        super(sh, name, "unshare file with another user");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can upload files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to unshare files!");
            return;
        }

        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }

        ShareFileService shareFileService = new ShareFileService();
        if(shareFileService.unshareFile(shell.getActiveSessionId(), args[1], args[0])){
            shell.println("File: "+ args[0] + " is anymore shared with: "+ args[1]);
            return;
        }
        shell.println("Error unsharing with: " + args[1]+". Maybe already unshared."); //TODO ver o erro corretamente
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <fileid> <userToShare>";
    }

}

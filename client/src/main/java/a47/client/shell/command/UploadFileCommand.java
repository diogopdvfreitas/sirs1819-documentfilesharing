package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.shell.ClientShell;
import a47.client.shell.service.UploadFileService;

public class UploadFileCommand extends AbstractCommand {

    public UploadFileCommand(ClientShell sh, String name) {
        super(sh, name, "upload a new file");
    }

    @Override
    void execute(String[] args) {

        // Only logged in can upload files
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You must be logged in to add files!");
            return;
        }

        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }
        UploadFileService uploadFileService = new UploadFileService();
        String FileID = uploadFileService.UploadFile(args[0], args[1], shell.getActiveSessionId());
        if (FileID == null) {
            shell.println("Problem uploading file: " + args[0]);
            return;
        }
        if(!ClientShell.isValidToken()){
            shell.println("Session expired, please login again");
            AuxMethods.logout(shell);
            return;
        }
        shell.println("File added with ID: " + FileID);
        shell.println("You should download the remote file in order to edit");
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <namefile> <path>";
    }

}

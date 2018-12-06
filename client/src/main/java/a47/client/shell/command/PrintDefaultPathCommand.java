package a47.client.shell.command;

import a47.client.shell.ClientShell;

public class PrintDefaultPathCommand extends AbstractCommand {

    public PrintDefaultPathCommand(ClientShell sh, String name) {
        super(sh, name, "print default path to download files");
    }

    @Override
    void execute(String[] args) {
        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();
        shell.println("Path to download is: " + shell.getPathToDownload() );
    }
}

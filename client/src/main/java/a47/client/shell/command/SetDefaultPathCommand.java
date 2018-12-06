package a47.client.shell.command;

import a47.client.shell.ClientShell;

public class SetDefaultPathCommand extends AbstractCommand {

    public SetDefaultPathCommand(ClientShell sh, String name) {
        super(sh, name, "set default path to download files");
    }

    @Override
    void execute(String[] args) {
        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();

        // check (and parse) arguments - username and password
        if (args.length != 1) {
            shell.println(getUsage());
            return;
        }

        shell.setPathToDownload(args[0]);
        shell.println("Path to download is now: " + shell.getPathToDownload() );
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <path>";
    }

}

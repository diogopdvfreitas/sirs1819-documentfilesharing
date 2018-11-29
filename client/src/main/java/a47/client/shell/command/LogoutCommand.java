package a47.client.shell.command;

import a47.client.shell.ClientShell;

public class LogoutCommand extends AbstractCommand {

    public LogoutCommand(ClientShell sh, String name) {
        super(sh, name, "ends session");
    }

    @Override
    void execute(String[] args) {

        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();
        if (shell.isLoggedIn()) {
            shell.println("You are already logged in!");
            return;
        }


    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

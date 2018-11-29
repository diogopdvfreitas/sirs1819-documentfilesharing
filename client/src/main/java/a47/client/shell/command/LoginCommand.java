package a47.client.shell.command;

import a47.client.shell.ClientShell;

public class LoginCommand extends AbstractCommand {

    public LoginCommand(ClientShell sh, String name) {
        super(sh, name, "start a new session");
    }

    @Override
    void execute(String[] args) {

        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();
        if (shell.isLoggedIn()) {
            shell.println("You are already logged in!");
            return;
        }

        // check (and parse) arguments - username and password
        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }
        String username = args[0];
        String password = args[1];


    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

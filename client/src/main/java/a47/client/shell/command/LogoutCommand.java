package a47.client.shell.command;

import a47.client.shell.ClientShell;
import a47.client.shell.service.LogoutService;

public class LogoutCommand extends AbstractCommand {

    public LogoutCommand(ClientShell sh, String name) {
        super(sh, name, "ends session");
    }

    @Override
    void execute(String[] args) {

        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You are not logged in");
            return;
        }

        LogoutService logoutService = new LogoutService();
        logoutService.LogoutServer(shell.getActiveSessionId());
        shell.setActiveSessionId(-1);
        shell.setActiveUser("");
        shell.println("Logged out");
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

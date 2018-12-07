package a47.client.shell.command;

import a47.client.shell.ClientShell;
import a47.client.shell.service.LogoutService;

public class LogoutCommand extends AbstractCommand {

    public LogoutCommand(ClientShell sh, String name) {
        super(sh, name, "ends session");
    }

    @Override
    void execute(String[] args) {

        // Only logout when logged in
        ClientShell shell = (ClientShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("You are not logged in");
            return;
        }

        LogoutService logoutService = new LogoutService();
        logoutService.LogoutServer(shell.getActiveSessionId());

        shell.setActiveSessionId(-1);
        shell.setActiveUser("");
        ClientShell.keyManager.setPrivateKey(null);
        ClientShell.keyManager.setPublicKey(null);

        shell.println("Logged out");
    }
}

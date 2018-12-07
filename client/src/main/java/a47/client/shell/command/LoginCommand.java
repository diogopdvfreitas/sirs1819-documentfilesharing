package a47.client.shell.command;

import a47.client.shell.ClientShell;
import a47.client.shell.service.LoginService;

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

        if(ClientShell.keyManager.getPrivateKey() == null || ClientShell.keyManager.getPublicKey() == null){
            shell.println("You should load your key pair first!");
            return;
        }

        String username = args[0];
        String password = args[1];

        LoginService loginService = new LoginService();

        if(loginService.LoginServer(username,password)){
            shell.setActiveUser(username);
            shell.setActiveSessionId(loginService.getToken());
            shell.setPathToDownload(shell.getPathToDownload() + username);
        }else{
            shell.println("Wrong user/password");
        }

    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

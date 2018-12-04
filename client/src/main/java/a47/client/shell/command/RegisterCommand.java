package a47.client.shell.command;

import a47.client.shell.ClientShell;
import a47.client.shell.service.RegisterService;

import java.security.NoSuchAlgorithmException;

public class RegisterCommand extends AbstractCommand {

    public RegisterCommand(ClientShell sh, String name) {
        super(sh, name, "register a new user and password");
    }

    @Override
    void execute(String[] args) {

        // Only register when not logged in
        ClientShell shell = (ClientShell) getShell();
        if (shell.isLoggedIn()) {
            shell.println("You are already logged in! Cannot register a new user now.");
            return;
        }

        // check (and parse) arguments - username and password
        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }
        String username = args[0];
        String password = args[1];

        RegisterService registerService = new RegisterService();
        try {
            if(!registerService.registerCA(username, ClientShell.keyManager.getPublicKey())) {
                shell.println("Error registering on CA");
                return;
            }
            shell.println("Registed on CA");
            if(!registerService.registerServer(username,password)){
                shell.println("Error registering on Server");
                return;
            }
            shell.println("Registed on Server");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

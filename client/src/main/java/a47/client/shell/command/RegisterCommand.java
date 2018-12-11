package a47.client.shell.command;

import a47.client.shell.ClientShell;
import a47.client.shell.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class RegisterCommand extends AbstractCommand {

    public RegisterCommand(ClientShell sh, String name) {
        super(sh, name, "register a new user");
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

        if(ClientShell.keyManager.getPrivateKey() == null || ClientShell.keyManager.getPublicKey() == null){
            shell.println("You should load your key pair first!");
            return;
        }

        RegisterService registerService = new RegisterService();
        try {
            if(!registerService.registerCA(username, ClientShell.keyManager.getPublicKey())) {
                shell.println("PublicKey already registered on CA");
            }else{
                shell.println("PublicKey registered on CA");
            }
            if(!registerService.registerServer(username,password)){
                shell.println("Error registering on Server");
                return;
            }
            shell.println("Registered on Server");

        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.CONFLICT)
                shell.println("User already registered on server");
        } catch (Exception e) {
                shell.println("Problems with registration");
        }
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

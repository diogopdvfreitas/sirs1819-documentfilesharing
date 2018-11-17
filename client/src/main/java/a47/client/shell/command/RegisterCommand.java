package a47.client.shell.command;

import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.service.RegisterService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

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

        ////// REMOVER //////////////////////////////
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            kpg.initialize(2048);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        ///////////////////////////////////////////////////

        RegisterService registerService = new RegisterService();
        try {
            registerService.registerCA(username,publicKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <password>";
    }

}

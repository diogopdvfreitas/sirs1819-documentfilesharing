package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;
import a47.client.shell.service.RegisterService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoadKeyPairCommand extends AbstractCommand {

    public LoadKeyPairCommand(ClientShell sh, String name) {
        super(sh, name, "load a key pair");
    }

    @Override
    void execute(String[] args) {
        ClientShell shell = (ClientShell) getShell();

        // check (and parse) arguments - username and password
        if (args.length != 2) {
            shell.println(getUsage());
            return;
        }
        String username = args[0];
        String passwordToDecodePrivateKey = args[1];

        Path pathpriv = Paths.get(Constants.Keys.KEYS_LOCATION + username + ".priv");
        if(!Files.exists(pathpriv)){
            shell.println("PrivateKey not found. Expected on: " + Constants.Keys.KEYS_LOCATION + username + ".priv");
            return;
        }

        Path pathpub = Paths.get(Constants.Keys.KEYS_LOCATION + username + ".pub");
        if(!Files.exists(pathpub)){
            shell.println("PublicKey not found. Expected on: " + Constants.Keys.KEYS_LOCATION + username + ".pub");
            return;
        }

        try {
            ClientShell.keyManager.setPublicKey(AuxMethods.loadPubKey(pathpub, username));
            ClientShell.keyManager.setPrivateKey(AuxMethods.loadPrivKey(pathpriv, username, passwordToDecodePrivateKey));
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            ClientShell.keyManager.setPrivateKey(null);
            ClientShell.keyManager.setPublicKey(null);
            shell.println("Error: loading keys from disk");
            return;
        }

        shell.println("Trying to register Public Key on CA");
        RegisterService registerService = new RegisterService();
        try {
            if(registerService.registerCA(username, ClientShell.keyManager.getPublicKey())){
                shell.println("Key Pair successfully loaded");
            }else{
                shell.println("Key Pair already registered on CA");
            }
        } catch (Exception e) {
            shell.println("Problems with registration on CA");
            ClientShell.keyManager.setPrivateKey(null);
            ClientShell.keyManager.setPublicKey(null);
            return;
        }

        shell.println("Ready to login/register on Server");
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <passwordToProtectPrivateKey>";
    }

}

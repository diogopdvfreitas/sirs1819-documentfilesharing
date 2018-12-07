package a47.client.shell.command;

import a47.client.AuxMethods;
import a47.client.Constants;
import a47.client.shell.ClientShell;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class GenerateKeyPairCommand extends AbstractCommand {

    public GenerateKeyPairCommand(ClientShell sh, String name) {
        super(sh, name, "generate a key pair");
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
        String passwordToProtectPrivateKey = args[1];

        KeyPairGenerator kpg = null;
        PublicKey publicKey;
        PrivateKey privateKey;
        try {
            kpg = KeyPairGenerator.getInstance(Constants.Keys.CA_KEYSTORE_CIPHER);
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (NullPointerException| NoSuchAlgorithmException e) {
            shell.println("Error: generating keys");
            return;
        }

        try {
            AuxMethods.savePrivKey(privateKey, username, passwordToProtectPrivateKey);
            AuxMethods.savePubKey(publicKey, username);
        } catch (NullPointerException | IOException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            shell.println("Error: saving keys on disk");
        }
    }

    @Override
    public String getUsage() {
        return "Usage: " + getName() + " <username> <passwordToProtectPrivateKey>";
    }

}

package a47.client;

import a47.client.shell.ClientShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String args[]) {
        ClientShell shell = new ClientShell(System.in, System.out, true);
        try {
            shell.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package a47.client;

import a47.client.shell.ClientShell;
import a47.client.shell.service.PingService;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        ClientShell shell = new ClientShell(System.in, System.out, true);
        new PingService(shell);
        try {
            shell.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

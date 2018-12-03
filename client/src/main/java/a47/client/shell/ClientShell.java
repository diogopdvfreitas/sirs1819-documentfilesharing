package a47.client.shell;

import a47.client.KeyManager;
import a47.client.shell.command.AbstractShell;
import a47.client.shell.command.LoginCommand;
import a47.client.shell.command.LogoutCommand;
import a47.client.shell.command.RegisterCommand;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientShell extends AbstractShell {
    private long activeSessionId = -1;
    private String activeUser = "";
    public static KeyManager keyManager;

    private static Logger logger = Logger.getLogger(AbstractShell.class);

    public ClientShell(InputStream is, PrintStream w, boolean flush) {
        super("client", is, w, flush);

        //REMOVER
        keyManager = new KeyManager();
        ///
        new RegisterCommand(this, "register");
        new LoginCommand(this, "login");
        new LogoutCommand(this, "logout");
    }


    @Override
    protected String getPrompt() {
        String prompt = super.getPrompt();
        String user = getActiveUser();
        if (!user.trim().equals("")) {
            prompt = user + "@" + prompt;
        }
        return prompt;
    }

    public boolean isLoggedIn() {
        return activeSessionId != -1;
    }

    public String getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(String activeUser) {
        this.activeUser = activeUser;
    }

    public long getActiveSessionId() {
        return activeSessionId;
    }

    public void setActiveSessionId(long activeSessionId) {
        this.activeSessionId = activeSessionId;
    }
}

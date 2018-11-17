package a47.client.shell;

import a47.client.shell.command.RegisterCommand;
import a47.client.shell.command.AbstractShell;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientShell extends AbstractShell {
    private String activeSessionid = null;
    private String activeUser = "";

    private static Logger logger = Logger.getLogger(AbstractShell.class);

    public ClientShell(InputStream is, PrintStream w, boolean flush) {
        super("client", is, w, flush);

        new RegisterCommand(this, "register");
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
        return activeSessionid != null;
    }

    public String getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(String activeUser) {
        this.activeUser = activeUser;
    }
}

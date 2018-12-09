package a47.client.shell;

import a47.client.KeyManager;
import a47.client.shell.command.*;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.io.PrintStream;

public class ClientShell extends AbstractShell {
    private long activeSessionId = -1;
    private String activeUser = "";
    public static KeyManager keyManager = new KeyManager();

    public static boolean validToken = false;

    private String pathToDownload = "/var/remote/";

    private static Logger logger = Logger.getLogger(AbstractShell.class);

    public ClientShell(InputStream is, PrintStream w, boolean flush) {
        super("client", is, w, flush);

        new RegisterCommand(this, "register");
        new LoginCommand(this, "login");
        new LogoutCommand(this, "logout");
        new UploadFileCommand(this, "upload");
        new ListFilesCommand(this, "listfiles");
        new DownloadFileCommand(this, "download");
        new PrintDefaultPathCommand(this, "printpath");
        new SetDefaultPathCommand(this, "setpath");
        new ShareFileCommand(this, "sharefile");
        new UnShareFileCommand(this, "unsharefile");
        new GenerateKeyPairCommand(this, "generatekeys");
        new LoadKeyPairCommand(this, "loadkeys");
        new CheckUpdatesCommand(this, "checkupdates");
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

    public String getPathToDownload() {
        return pathToDownload;
    }

    public void setPathToDownload(String pathToDownload) {
        this.pathToDownload = pathToDownload;
    }

    public static boolean isValidToken() {
        return validToken;
    }

    public static void setValidToken(boolean validToken) {
        ClientShell.validToken = validToken;
    }
}

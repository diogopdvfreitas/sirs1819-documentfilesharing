package a47.client.shell.command;

import org.jboss.logging.Logger;

/**
 * Class AbstractCommand
 * an abstract command
 */
public abstract class AbstractCommand {
    private static Logger logger = Logger.getLogger(AbstractCommand.class);

    private AbstractShell sh;
    private String name;
    private String help;

    public AbstractCommand(AbstractShell sh, String name, String help) {
        this.name = name;
        this.help = help;
        this.sh = sh;
        sh.addCommand(this);
    }

    abstract void execute(String[] args);

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public AbstractShell getShell() {
        return sh;
    }

    public String getUsage() {
        return "Usage: " + getName() + " ???";
    }

    protected static Logger getLogger() {
        return AbstractCommand.logger;
    }
}

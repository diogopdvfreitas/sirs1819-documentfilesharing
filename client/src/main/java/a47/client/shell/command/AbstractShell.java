package a47.client.shell.command;

import org.jboss.logging.Logger;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

public abstract class AbstractShell {
    private static Logger logger = Logger.getLogger(AbstractShell.class);
    private String name;
    private PrintWriter output;
    private InputStream input;
    /** commands that this shell accepts */
    private Map<String, AbstractCommand> commands = new TreeMap<>();

    private String prompt;

    public static Logger getLogger() {
        return logger;
    }

    public AbstractShell(String name, InputStream is, PrintStream w, boolean flush) {
        this.name = name;
        input = is;
        output = new PrintWriter(w, flush);
        prompt = name + " $ ";

        AbstractCommand exit = new AbstractCommand(this, "exit", "exit the application") {
            @Override
            void execute(String[] args) {
                onQuitCommand();
            }
            @Override
            public String getUsage() {
                return "Usage: " + getName();
            }
        };

        new AbstractCommand(this, "help", "list available commands or command help") {
            @Override
            void execute(String[] args) {
                if(args.length == 0) {
                    println(getShell().getName() + " available commands:");
                    println("");
                    List<String> availableCmds = availableCommands();
                    Collections.sort(availableCmds);
                    for(String cmdName : availableCmds) {
                        AbstractCommand c = getShell().getCommand(cmdName);
                        println("\t" + whatIs(c));
                    }
                } else {
                    String cmdName = args[0];
                    AbstractCommand c = getShell().getCommand(cmdName);
                    if(c == null) {
                        println(cmdName + ": nothing appropriate.");
                    } else {
                        println(whatIs(c));
                        println("");
                        println(c.getUsage());
                    }
                }
            }

            @Override
            public String getUsage() {
                return "Usage: " + this.getName() + " [command]";
            }
        };
    }

    protected void onQuitCommand() {
        System.exit(0);
    }

    protected String getPrompt() {
        return prompt;
    }

    protected void printPrompt() {
        print(getPrompt());
        output.flush();
    }

    protected String whatIs(AbstractCommand cmd) {
        return cmd.getName() + "\t\t\t" + cmd.getHelp();
    }

    public void println(String s) {
        output.println(s);
    }

    public void print(String s) {
        output.print(s);
    }

    protected List<String> availableCommands() {
        Collection<String> av = Collections.unmodifiableCollection(commands.keySet());
        return new ArrayList<>(av);
    }

    boolean addCommand(AbstractCommand c) {
        AbstractCommand prev = commands.put(c.getName(), c);
        return prev != null;
    }

    public AbstractCommand getCommand(String name) {
        return commands.get(name);
    }

    public void run() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(this.input));
        printPrompt();
        String line;
        while((line = input.readLine()) != null) {
            String args[] = line.trim().split(" ");
            {   // remove empty arguments, e.g. "a  b     c" -> "a b c"
                List<String> list = new ArrayList<String>(Arrays.asList(args));
                list.removeIf(StringUtils::isEmpty);
                args = (String[]) list.toArray(new String[0]);
            }
            if(args.length > 0) {
                String cmdName = args[0];
                AbstractCommand c = getCommand(cmdName);
                if(c == null) {
                    if(!cmdName.isEmpty()) {
                        println(getName() + ": " + cmdName + ": command not found");
                    }
                } else {
                    try {
                        c.execute(Arrays.copyOfRange(args, 1, args.length));
                    } catch (RuntimeException e) {
                        System.err.print(cmdName + " error: " + e);
                        e.printStackTrace();
                    }
                }
            }
            printPrompt();
        }
        /*
        if(line == null) {
            onQuitCommand();
        }
        */
        println("EOF... bye!");
    }

    public String getName() {
        return name;
    }
}

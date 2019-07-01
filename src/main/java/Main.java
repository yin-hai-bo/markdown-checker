import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        try {
            Checker.Counter counter = Checker.execute(new File(args[0]));
            Logger.add(counter.toString());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: markdown-checker filename");
    }

}

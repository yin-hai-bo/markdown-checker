import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.PrintStream;

class Logger {

    private static PrintStream output = System.out;

    static PrintStream setOutput(PrintStream printStream) {
        PrintStream result = output;
        output = printStream;
        return result;
    }

    static void add(@NotNull String msg) {
        PrintStream ps = output;
        if (ps != null) {
            ps.println(msg);
        }
    }

    static void add(@NotNull String fmt, @Nullable Object... args) {
        String s = String.format(Utils.LOCALE_FOR_NUMBER_FORMAT, fmt, args);
        add(s);
    }
}

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

class Utils {

    final static Locale LOCALE_FOR_NUMBER_FORMAT = Locale.US;

    static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
            }
        }
    }
}

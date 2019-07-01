import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class LoggerTest {

    @org.junit.Test
    public void add() {
        MockPrintStream ps = new MockPrintStream();
        PrintStream oldPrintStream = Logger.setOutput(ps);
        try {
            Logger.add("%d", 1234);
        } finally {
            Logger.setOutput(oldPrintStream);
        }
        assertEquals("1234", ps.getOutput());
    }

    private static class MockPrintStream extends PrintStream {

        MockPrintStream() {
            super(new ByteArrayOutputStream(1024));
        }

        String getOutput() {
            this.flush();
            String s = this.out.toString();
            int len = s.length();
            while (len > 0) {
                char c = s.charAt(len - 1);
                if (c == '\n' || c == '\r') {
                    --len;
                    s = s.substring(0, len);
                } else {
                    break;
                }
            }
            return s;
        }
    }

}
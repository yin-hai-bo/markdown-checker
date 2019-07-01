import java.io.Closeable;
import java.io.IOException;

import static org.junit.Assert.*;

public class UtilsTest {

    @org.junit.Test
    public void close() {
        MockClosable mc = new MockClosable(false);
        assertFalse(mc.closeMethodInvoked);
        Utils.close(mc);
        assertTrue(mc.closeMethodInvoked);
        //
        mc = new MockClosable(true);
        Utils.close(mc);
        assertTrue(mc.closeMethodInvoked);
    }

    private static class MockClosable implements Closeable {

        private final boolean throwException;

        boolean closeMethodInvoked;

        private MockClosable(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        public void close() throws IOException {
            closeMethodInvoked = true;
            if (this.throwException) {
                throw new IOException();
            }
        }
    }
}
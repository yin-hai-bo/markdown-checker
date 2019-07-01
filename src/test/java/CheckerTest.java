import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class CheckerTest {

    private static final String TEXT =
        "[a](#a) <a name=\"a\"></a>\n" +
            "[b](#b) <a name=\"b\" />\n" +
            "[c](#c)\n" +
            "[xxx](#xxx)\n" +
            "[aaa](#)\n" +
            "[hhh](  )\n" +
            "[d](http://www.example.com)\n" +
            "[e](https://wwww)\n" +
            "[f](a.jpg)\n" +
            "[](b.png)\n" +
            "[](c.png)";

    @Test
    public void execute() throws IOException {
        Checker.Counter counter;
        ByteArrayInputStream input = new ByteArrayInputStream(TEXT.getBytes());
        try {
            counter = Checker.execute(input);
        } finally {
            Utils.close(input);
        }
        //
        assertEquals(5, counter.innerLink);
        assertEquals(3, counter.innerLinkNotFound);
        assertEquals(1, counter.emptyLink);
        assertEquals(2, counter.outerLink);
        assertEquals(3, counter.unknownLink);
    }
}
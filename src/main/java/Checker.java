import com.sun.istack.internal.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Checker {

    private static final Pattern PATTERN_ANCHOR = Pattern.compile("<a\\s+name=\"(.+?)\"");
    private static final Pattern PATTERN_LINK = Pattern.compile("]\\s*\\((.+?)\\)", Pattern.MULTILINE);

    private Checker() {
    }

    @NotNull
    static Counter execute(@NotNull File file) throws IOException {
        String s;
        InputStream input = new FileInputStream(file);
        try {
            s = readEntireInputStream(input);
        } finally {
            Utils.close(input);
        }
        return execute(s);
    }

    @NotNull
    static Counter execute(@NotNull InputStream input) throws IOException {
        String s = readEntireInputStream(input);
        return execute(s);
    }

    @NotNull
    private static Counter execute(String text) {
        Anchors anchors = Anchors.build(Item.findAll(text, PATTERN_ANCHOR, false));
        List<Item> hyperLinks = Item.findAll(text, PATTERN_LINK, true);
        Counter counter = new Counter();
        for (Item link : hyperLinks) {
            String value = link.value;
            int size = value.length();
            if (size == 0) {
                ++counter.emptyLink;
                Logger.add("Warning: Empty hyper link on %s.", link.position);
                continue;
            }
            if (value.charAt(0) == '#') {
                ++counter.innerLink;
                if (size == 1) {
                    ++counter.innerLinkNotFound;
                    Logger.add("Warning: Single '#' link on %s.", link.position);
                    continue;
                }
                String target = value.substring(1);
                if (!anchors.contains(target)) {
                    ++counter.innerLinkNotFound;
                    Logger.add("Warning: Target not found, '%s' on %s.", value, link.position);
                }
                continue;
            }
            //
            if (value.startsWith("http://") || value.startsWith("https://")) {
                ++counter.outerLink;
                continue;
            }
            //
            ++counter.unknownLink;
            Logger.add("Warning: Unknown target, '%s' on %s.", value, link.position);
        }

        return counter;
    }

    private static String readEntireInputStream(@NotNull InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024 * 1024);
        try {
            byte[] buf = new byte[16 * 1024];
            while (true) {
                int size = input.read(buf);
                if (size <= 0) {
                    break;
                }
                output.write(buf, 0, size);
            }
        } finally {
            Utils.close(output);
        }
        return output.toString(StandardCharsets.UTF_8.toString());
    }

    static class Counter {
        int innerLink, innerLinkNotFound;
        int outerLink;
        int unknownLink;
        int emptyLink;

        @Override
        public String toString() {
            return String.format(
                "InnerLink: %d, not found=%d\nOuterLink: %d\n  Unknown: %d\n    Empty: %d",
                innerLink, innerLinkNotFound,
                outerLink,
                unknownLink,
                emptyLink);
        }
    }

    private static class Anchors {

        @NotNull
        private final Map<String, Position> map;

        private Anchors(@NotNull Map<String, Position> map) {
            this.map = map;
        }

        @NotNull
        static Anchors build(Iterable<Item> items) {
            Logger.add("Parse all anchors ...");
            Map<String, Position> map = new HashMap<>();
            for (Item item : items) {
                if (item.value.length() == 0) {
                    Logger.add("Warning: Empty anchor on %s.", item.position);
                } else {
                    map.put(item.value, item.position);
                }
            }
            Logger.add("%d anchor[s] parsed.", map.size());
            return new Anchors(map);
        }

        boolean contains(@NotNull String name) {
            return map.containsKey(name);
        }

    }

    private static class Position {
        private final int pos;

        private Position(int pos) {
            this.pos = pos;
        }

        @Override
        public String toString() {
            return String.format(Utils.LOCALE_FOR_NUMBER_FORMAT, "[Position: %d]", pos);
        }
    }

    private static class Item {

        @NotNull
        final Position position;

        @NotNull
        final String value;

        private Item(@NotNull Position position, @NotNull String value) {
            this.position = position;
            this.value = value;
        }

        @NotNull
        static List<Item> findAll(@NotNull CharSequence content, @NotNull Pattern pattern, boolean trim) {
            List<Item> result = new ArrayList<>(128);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                int pos = matcher.start();
                String value = matcher.group(1);
                if (trim) {
                    value = value.trim();
                }
                result.add(new Item(new Position(pos), value));
            }
            return result;
        }
    }

}

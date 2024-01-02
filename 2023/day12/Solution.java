import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    public static void main(String[] args) {
        final List<String> inits = parseFile("ex_1");
        System.out.println("Solution part 1: " + inits.stream().map(p -> hashCode(p, 17,256)).reduce(0L,Long::sum));
    }

    private static long hashCode(String string, final int currentMultiplier, final int reminderDivisor) {
        long out = 0;
        for (int i = 0; i < string.length(); i++) {
            out += string.charAt(i);
            out *= currentMultiplier;
            out = out % reminderDivisor;
        }
        return out;
    }

    private static List<String> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return Arrays.stream(fileStream.reduce("", String::concat).split(",")).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

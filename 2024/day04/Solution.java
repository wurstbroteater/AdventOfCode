import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    public static void main(String[] args) {
        for (final String input : List.of("ex_0")) {
            final String prefix = "[" + input + "] ";
            final List<List<String>> grid = parseFile(input);
            for (int y = 0; y < grid.size(); y++) {
                for (int x = 0; x < grid.get(y).size(); x++) {
                    final char curr = grid.get(y).get(x).charAt(0);
                    //TODO: code here
                }
            }

            System.out.println(prefix + "Solution part 1: " + grid);
            System.out.println(prefix + "Solution part 2: ");
        }
    }


    private static List<List<String>> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return fileStream.map(line -> Arrays.stream(line.trim().split("\\s+")).map(element ->
                    element.replace("X", "1")
                            .replace("M", "2")
                            .replace("A", "3")
                            .replace("S", "4")).toList()).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing file: " + ex.getMessage(), ex);
        }
    }
}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    private record IdRange(long start, long end) {

        @Override
        public String toString() {
            return start + "-" + end;
        }

        private List<Long> find() {
            List<Long> out = new ArrayList<>();
            for (long i = start; i <= end(); i++) {
                final String current = String.valueOf(i);
                if (current.length() % 2 != 0) {
                    continue;
                }
                final int mid = current.length() / 2;
                final String first = current.substring(0, mid);
                if (first.equals(current.substring(mid))) {
                    out.add(i);
                }
            }
            return out;
        }
    }


    public static void main(final String[] args) {

        for (final String input : List.of("ex_0", "puzzle")) {
            List<IdRange> ranges = parseFile(input);

            System.out.printf("[%s] Solution part 1: %d%n", input, ranges.stream()
                    .map(IdRange::find)
                    .filter(l -> !l.isEmpty())
                    .flatMap(List::stream).toList().stream()
                    .reduce(0L, Long::sum));

        }
    }

    private static List<IdRange> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return Arrays.stream(fileStream.reduce("", String::concat).split(",")).map(s -> {
                final String[] numbers = s.split("-");
                return new IdRange(Long.parseLong(numbers[0]), Long.parseLong(numbers[1]));
            }).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }

}
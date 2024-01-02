import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    private record Histories(List<long[]> history) {}

    public static void main(String[] args) {
        final Histories histories = parseFile("puzzle");
        System.out.println("Solution part 1: " + histories.history.stream().map(p -> solvePyramid(createPyramid(p))).map(v -> v[v.length - 1]).reduce(0L, Long::sum));
        System.out.println("Solution part 2: " + histories.history.stream().map(p -> solvePyramid(createPyramid(p))).map(v -> v[0]).reduce(0L, Long::sum));
    }

    private static long[] solvePyramid(final List<long[]> pyramid) {
        //start in penultimate row, then the missing value is the sum of left and bottom neighbour
        for (int i = pyramid.size() - 2; i >= 0; i--) {
            final long[] row = pyramid.get(i);
            row[row.length - 1] = row[row.length - 2] + pyramid.get(i + 1)[row.length - 2];
            row[0] = row[1] - pyramid.get(i + 1)[0];
        }
        return pyramid.getFirst();
    }

    private static List<long[]> createPyramid(long[] values) {
        final List<long[]> out = new ArrayList<>();
        out.add(rightShift(Arrays.copyOf(values, values.length + 2)));
        while (!Arrays.stream(values).allMatch(l -> l == 0L)) {
            long[] diffs = new long[values.length - 1];
            for (int i = 0; i < values.length - 1; i++) {
                diffs[i] = values[i + 1] - values[i];
            }
            out.add(rightShift(Arrays.copyOf(diffs, diffs.length + 2)));
            values = diffs;
        }
        return out;
    }

    private static long[] rightShift(final long[] array) {
        final long last = array[array.length - 1];
        for (int i = array.length - 1; i > 0; i--) {
            array[i] = array[i - 1];
        }
        array[0] = last;
        return array;
    }

    private static Histories parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return new Histories(fileStream.map(l -> Arrays.stream(l.trim().split(" ")).mapToLong(Long::parseLong).toArray()).toList());
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

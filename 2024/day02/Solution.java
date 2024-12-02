import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Solution {
    record Report(List<Integer> levels) {

        boolean isSave() {
            return hasValidNeighbours() && isIncreasing() || isDecreasing();
        }

        private boolean hasValidNeighbours() {
            for (int i = 1; i < levels.size(); i++) {
                final int prev = levels.get(i - 1);
                final int curr = levels.get(i);
                final int adjacentDiff = Math.abs(prev - curr);
                if (adjacentDiff > 3) {
                    return false;
                }
            }
            return true;
        }

        private boolean isIncreasing() {
            return IntStream.range(1, levels.size())
                    .allMatch(i -> levels.get(i) > levels.get(i - 1));
        }

        private boolean isDecreasing() {
            return IntStream.range(1, levels.size())
                    .allMatch(i -> levels.get(i) < levels.get(i - 1));
        }

        @Override
        public String toString() {
            return isSave() + " " + levels.toString();
        }

    }

    public static void main(final String[] args) {
        for (final String input : List.of("ex_0", "puzzle")) {
            final String prefix = "[" + input + "] ";
            final List<Report> reports = parseFile(input);
            System.out.println(prefix + "Solution part 1: " + reports.stream().filter(Report::isSave).count());
        }
    }

    private static List<Report> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return fileStream.map(line -> new Report(Arrays.stream(line.split(" ")).map(Integer::parseInt).toList())).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

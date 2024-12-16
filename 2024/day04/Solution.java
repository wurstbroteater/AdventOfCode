import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solution {
    private record Point(int x, int y) {
        boolean isValid() {
            return x != -1 && y != -1;
        }

        char getChar(final List<String> grid) {
            final String row = grid.get(y);
            return row.charAt(x);
        }
    }

    private enum Direction {
        North, East, South, West;

        Point next(final Point current, final List<String> grid) {
            final String row = grid.get(current.y());
            return switch (this) {
                case North -> {
                    final int nextX = current.x() + 1 < row.length() ? current.x() + 1 : -1;
                    final int nextY = current.y() - 1 >= 0 ? current.y() - 1 : -1;
                    yield new Point(nextX, nextY);
                }
                case West -> {
                    final int nextX = current.x() - 1 >= 0 ? current.x() - 1 : -1;
                    final int nextY = current.y() - 1 >= 0 ? current.y() - 1 : -1;
                    yield new Point(nextX, nextY);
                }
                case East -> {
                    final int nextX = current.x() + 1 < row.length() ? current.x() + 1 : -1;
                    final int nextY = current.y() + 1 < grid.size() ? current.y() + 1 : -1;
                    yield new Point(nextX, nextY);
                }
                case South -> {
                    final int nextX = current.x() - 1 >= 0 ? current.x() - 1 : -1;
                    final int nextY = current.y() + 1 < grid.size() ? current.y() + 1 : -1;
                    yield new Point(nextX, nextY);
                }

            };
        }
    }

    private static final int END = 4;

    public static void main(String[] args) {
        for (final String input : List.of("ex_0", "puzzle")) {
            final String prefix = "[" + input + "] ";
            final List<String> grid = parseFile(input);

            System.out.println(prefix + "Solution part 1: " + findXMAS(grid));
        }
    }

    static long findXMAS(final List<String> grid) {
        final char start = '1';
        long occurrences = 0;
        for (int y = 0; y < grid.size(); y++) {
            final String row = grid.get(y);
            //ex0 part 1-> 5
            occurrences += countLineOccurrences(row, start);
            for (int x = 0; x < row.length(); x++) {
                final char curr = row.charAt(x);
                if (curr == start) {
                    occurrences += findVertical(start, x, y, grid);
                    occurrences += findDiagonal(start, x, y, grid);
                }
            }
        }
        return occurrences;
    }

    private static long findDiagonal(final char start, final int x, final int y, final List<String> grid) {
        final String row = grid.get(y);
        final char curr = row.charAt(x);
        long occurrences = 0;
        if (curr == start) {
            final Point startPosition = new Point(x, y);
            occurrences += findDiagonal(Direction.North, startPosition, grid);
            occurrences += findDiagonal(Direction.East, startPosition, grid);
            occurrences += findDiagonal(Direction.South, startPosition, grid);
            occurrences += findDiagonal(Direction.West, startPosition, grid);
        } else {
            System.out.println("Invalid start for vertical search " + curr);
        }
        return occurrences;
    }

    private static long findDiagonal(final Direction direction, final Point position, final List<String> grid) {
        final String row = grid.get(position.y());
        final int curr = Integer.parseInt(String.valueOf(row.charAt(position.x())));
        final Point nextPosition = direction.next(position, grid);
        if (curr == END) {
            return 1L;
        }
        if (nextPosition.isValid() && Integer.parseInt(String.valueOf(nextPosition.getChar(grid))) == curr + 1) {
            return findDiagonal(direction, nextPosition, grid);
        }
        return 0L;
    }

    private static long findVertical(final char start, final int x, final int y, final List<String> grid) {
        final String row = grid.get(y);
        final char curr = row.charAt(x);
        long occurrences = 0;
        if (curr == start) {
            final Point startPosition = new Point(x, y);
            occurrences += findVertical(1, startPosition, grid);
            occurrences += findVertical(-1, startPosition, grid);
        } else {
            System.out.println("Invalid start for vertical search " + curr);
        }
        return occurrences;
    }

    private static long findVertical(final int step, final Point position, final List<String> grid) {
        final int curr = Integer.parseInt(String.valueOf(position.getChar(grid)));
        final int nextY = validY(step, position.y(), grid.size() - 1);
        if (curr == END) {
            return 1L;
        }
        if (nextY != -1 && Integer.parseInt(String.valueOf(grid.get(nextY).charAt(position.x()))) == curr + 1) {
            return findVertical(step, new Point(position.x(), nextY), grid);
        }
        return 0L;
    }

    private static int validY(final int step, final int currentY, final int maxY) {
        final int nextY = currentY + step;
        if (nextY > maxY || nextY < 0) {
            return -1;
        }
        return nextY;
    }

    private static long countLineOccurrences(final String row, final char start) {
        final List<String> target = generatePair(start);
        long count = 0;
        for (int i = 0; i <= row.length() - target.getFirst().length(); i++) {
            if (row.startsWith(target.getFirst(), i) || row.startsWith(target.getLast(), i)) {
                count++;
            }
        }
        return count;
    }

    private static List<String> generatePair(final char start) {
        final int startIndex = Integer.parseInt(String.valueOf(start));
        StringBuilder ordered = new StringBuilder();
        IntStream.rangeClosed(startIndex, END).forEach(ordered::append);
        return List.of(ordered.toString(), ordered.reverse().toString());
    }

    private static List<String> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return fileStream.map(String::trim).map(element ->
                    element.replace("X", "1")
                            .replace("M", "2")
                            .replace("A", "3")
                            .replace("S", "4")).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing file: " + ex.getMessage(), ex);
        }
    }
}

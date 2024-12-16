import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
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
        //DO NOT CHANGE THIS ORDER!
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
            System.out.println(prefix + "Solution part 2: " + findMAS(grid));
        }
    }

    static long findXMAS(final List<String> grid) {
        long occurrences = 0;
        for (int y = 0; y < grid.size(); y++) {
            final String row = grid.get(y);
            //ex0 part 1-> 5
            occurrences += horizontalOccurrences(row);
            for (int x = 0; x < row.length(); x++) {
                final char curr = row.charAt(x);
                if (curr == '1') {
                    occurrences += findVertical(x, y, grid);
                    occurrences += findDiagonal(x, y, grid);
                }
            }
        }
        return occurrences;
    }

    static long findMAS(final List<String> grid) {
        final char aChar = '3';
        long occurrences = 0;
        for (int y = 0; y < grid.size(); y++) {
            final String row = grid.get(y);
            for (int x = 0; x < row.length(); x++) {
                final char curr = row.charAt(x);
                if (curr == aChar) {
                    final var sp = new Point(x, y);
                    final var validNeighbours = Arrays.stream(Direction.values())
                            .map(d -> d.next(sp, grid))
                            .filter(Point::isValid)
                            .toList();
                    if (validNeighbours.size() == 4) {
                        /*
                         * the 4 valid neighbours are the diagonal neighbours of 'A' aka '3'. A valid combination of
                         * such neighbours have to from the X with any shape "MAS". There are only 4 cases which fulfill
                         * that criteria. The first is given the beginning of part two. By translating this example in
                         * our int representation and start reading the numbers clockwise beginning at the top right, we
                         * receive the String 4422. Doing so for the remaining 3 matrix transpositions yields in the
                         * strings checked here. This works because the order of validNeighbours is assured by the order
                         * Direction enum. Changing this the enum may break this implementation.
                         */
                        occurrences += Stream.of(validNeighbours.stream()
                                        .map(p -> String.valueOf(p.getChar(grid)))
                                        .reduce("", String::concat))
                                .mapToLong(s -> "4422".equals(s) || "2442".equals(s) || "2244".equals(s) || "4224".equals(s) ? 1L : 0L)
                                //there is always exactly 1 item, so this reduce is always 0 + 1 or 0 + 0
                                .reduce(0L, Long::sum);
                    }
                }
            }
        }
        return occurrences;
    }

    private static long findDiagonal(final int x, final int y, final List<String> grid) {
        long occurrences = 0;
        final Point startPosition = new Point(x, y);
        occurrences += findDiagonal(Direction.North, startPosition, grid);
        occurrences += findDiagonal(Direction.East, startPosition, grid);
        occurrences += findDiagonal(Direction.South, startPosition, grid);
        occurrences += findDiagonal(Direction.West, startPosition, grid);
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

    private static long findVertical(final int x, final int y, final List<String> grid) {
        long occurrences = 0;
        final Point startPosition = new Point(x, y);
        occurrences += findVertical(1, startPosition, grid);
        occurrences += findVertical(-1, startPosition, grid);
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

    private static long horizontalOccurrences(final String row) {
        final List<String> target = List.of("1234", "4321");
        long count = 0;
        for (int i = 0; i <= row.length() - target.getFirst().length(); i++) {
            if (row.startsWith(target.getFirst(), i) || row.startsWith(target.getLast(), i)) {
                count++;
            }
        }
        return count;
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

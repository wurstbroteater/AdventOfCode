import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solution {

    private static class Tile {
        String label;
        int x;
        int y;
        Set<Beam> visited;

        Tile(String label, int x, int y) {
            this.label = label;
            this.x = x;
            this.y = y;
            visited = new HashSet<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tile tile = (Tile) o;
            return x == tile.x && y == tile.y && Objects.equals(label, tile.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, x, y);
        }

        @Override
        public String toString() {
            return STR."Tile[label='\{label}\{'\''}, energy=\{visited.size()}\{']'}";
        }
    }

    private record Beam(int x, int y, int currentDir, boolean inMotion) {

        private Beam performStep(Beam beam) {
            return switch (beam.currentDir()) {
                case 0 -> new Beam(beam.x(), beam.y() - 1, beam.currentDir(), beam.inMotion());
                case 1 -> new Beam(beam.x() + 1, beam.y(), beam.currentDir(), beam.inMotion());
                case 2 -> new Beam(beam.x(), beam.y() + 1, beam.currentDir(), beam.inMotion());
                default -> new Beam(beam.x() - 1, beam.y(), beam.currentDir(), beam.inMotion());
            };
        }

        private Beam changeDir(Beam beam, String tile, boolean oldBeam) {
            return (!beam.inMotion) ? beam : switch (tile) {
                case "/" -> switch (beam.currentDir()) {
                    case 0 -> new Beam(beam.x(), beam.y(), 1, beam.inMotion());
                    case 1 -> new Beam(beam.x(), beam.y(), 0, beam.inMotion());
                    case 2 -> new Beam(beam.x(), beam.y(), 3, beam.inMotion());
                    default -> new Beam(beam.x(), beam.y(), 2, beam.inMotion());
                };
                case "\\" -> switch (beam.currentDir()) {
                    case 0 -> new Beam(beam.x(), beam.y(), 3, beam.inMotion());
                    case 1 -> new Beam(beam.x(), beam.y(), 2, beam.inMotion());
                    case 2 -> new Beam(beam.x(), beam.y(), 1, beam.inMotion());
                    default -> new Beam(beam.x(), beam.y(), 0, beam.inMotion());
                };
                case "-" -> {
                    if (beam instanceof Beam(int cx, int cy, int dir, boolean cm) && (dir == 0 || dir == 2)) {
                        //left first for old beam
                        if (dir == 0) {
                            yield new Beam(cx, cy, oldBeam ? 3 : 1, cm);
                        } else {
                            yield new Beam(cx, cy, oldBeam ? 1 : 3, cm);
                        }
                    }
                    yield beam;
                }
                case "|" -> {
                    if (beam instanceof Beam(int cx, int cy, int dir, boolean cm) && (dir == 1 || dir == 3)) {
                        //left first for old beam
                        if (dir == 1) {
                            yield new Beam(cx, cy, oldBeam ? 0 : 2, cm);
                        } else {
                            yield new Beam(cx, cy, oldBeam ? 2 : 0, cm);
                        }
                    }
                    yield beam;
                }
                default -> beam;
            };
        }

        Beam move(final Tile tile, final boolean oldBeam, final int maxX, final int maxY) {
            if (tile.visited.contains(this)) {
                return new Beam(this.x(), this.y(), this.currentDir(), false);
            }
            final int oldX = this.x();
            final int oldY = this.y();
            Beam out = tile.label.equals(".") ? performStep(this) : performStep(changeDir(this, tile.label, oldBeam));
            out = out.x() < 0 || out.x() > maxX || out.y() < 0 || out.y() > maxY ? this : out;
            if (oldX == out.x() && oldY == out.y()) {
                return new Beam(out.x(), out.y(), out.currentDir(), false);
            }
            return out;
        }
    }

    private record StartPos(int x, int y, int dir, List<List<Tile>> grid) {
    }

    // Directions: North = 0, East = 1, South = 2, West = 3
    public static void main(String[] args) {
        final List<List<Tile>> grid = parseFile("puzzle");
        final int maxX = grid.getFirst().size() - 1;
        final int maxY = grid.size() - 1;
        final List<StartPos> startPositions = createStartPositions(maxX, maxY, grid);
        System.out.println(STR."Solution part 1: \{findEnergizedTiles(new StartPos(0, 0, 1, copyInitialGrid(grid)))}");
        // Calculation time is up to 45 s.
        System.out.println(STR."Solution part 2: \{startPositions.stream().parallel().map(Solution::findEnergizedTiles).max(Integer::compare).orElseThrow()}");
    }

    private static List<List<Tile>> copyInitialGrid(final List<List<Tile>> grid) {
        final List<List<Tile>> out = new ArrayList<>();
        for (List<Tile> row : grid) {
            out.add(new ArrayList<>(row.stream().map(t -> new Tile(t.label, t.x, t.y)).toList()));
        }
        return out;
    }

    private static List<StartPos> createStartPositions(final int maxX, final int maxY, final List<List<Tile>> grid) {
        final List<StartPos> out = new ArrayList<>();
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                if ((y == 0 || x == 0) || (y == maxY || x == maxX)) {
                    out.addAll(createStartPositions(x, y, maxX, maxY, grid));
                }
            }
        }
        return out;
    }

    private static List<StartPos> createStartPositions(final int x, final int y, final int maxX, final int maxY, final List<List<Tile>> grid) {
        final List<StartPos> out = new ArrayList<>();
        IntStream.rangeClosed(0, 3).forEach(dir -> {
            switch (dir) {
                case 0:
                    if (x <= maxX && y == maxY || y > 0 && y + 1 <= maxY) {
                        out.add(new StartPos(x, y, dir, copyInitialGrid(grid)));
                    }
                    break;
                case 1:
                    if (x < maxX) {
                        out.add(new StartPos(x, y, dir, copyInitialGrid(grid)));
                    }
                    break;
                case 2:
                    if (y < maxY) {
                        out.add(new StartPos(x, y, dir, copyInitialGrid(grid)));
                    }
                    break;
                default:
                    if (x - 1 >= 0) {
                        out.add(new StartPos(x, y, dir, copyInitialGrid(grid)));
                    }
                    break;
            }
        });
        return out;
    }

    private static Set<Tile> findEnergizedTiles(final List<List<Solution.Tile>> grid) {
        final Set<Tile> out = new HashSet<>();
        for (List<Tile> row : grid) {
            out.addAll(row.stream().filter(t -> !t.visited.isEmpty()).toList());
        }
        return out;
    }

    private static int findEnergizedTiles(final StartPos start) {
        final List<List<Tile>> grid = start.grid();
        final int maxX = grid.getFirst().size() - 1;
        final int maxY = grid.size() - 1;
        List<Beam> beams = new ArrayList<>();
        beams.add(new Beam(start.x(), start.y(), start.dir(), true));
        Set<Tile> energizedTiles = findEnergizedTiles(grid);
        while (!beams.isEmpty()) {
            final List<Beam> oldBeams = new ArrayList<>();
            final List<Beam> newBeams = new ArrayList<>();
            for (Beam beam : beams) {
                final Tile tile = grid.get(beam.y()).get(beam.x());
                if (beam.inMotion()) {
                    if ((tile.label.equals("|") || tile.label.equals("-"))) {
                        newBeams.add(beam.move(tile, false, maxX, maxY));
                    }
                    final Beam movedOldBeam = beam.move(tile, true, maxX, maxY);
                    if (movedOldBeam.inMotion()) {
                        oldBeams.add(movedOldBeam);
                    }
                }
                tile.visited.add(beam);
            }
            oldBeams.addAll(newBeams);
            beams = oldBeams;
            energizedTiles = findEnergizedTiles(grid);
        }
        return energizedTiles.size();
    }

    private static List<List<Tile>> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final List<String> input = fileStream.toList();
            final List<List<Tile>> out = new ArrayList<>();
            for (int y = 0; y < input.size(); y++) {
                final String[] intermediate = input.get(y).split("");
                final List<Tile> row = new ArrayList<>();
                for (int x = 0; x < intermediate.length; x++) {
                    row.add(new Tile(intermediate[x], x, y));
                }
                out.add(row);
            }
            return out;
        } catch (IOException ex) {
            throw new IllegalStateException(STR."Error while parsing schematic file: \{ex.getCause()}");
        }
    }
}

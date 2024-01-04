import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solution {
    private static final AtomicInteger idGenerator = new AtomicInteger(0);

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

    private record Beam(int id, int x, int y, int currentDir, boolean inMotion) {

        private Beam performStep(Beam beam) {
            return switch (beam.currentDir()) {
                case 0 -> new Beam(beam.id(), beam.x(), beam.y() - 1, beam.currentDir(), beam.inMotion());
                case 1 -> new Beam(beam.id(), beam.x() + 1, beam.y(), beam.currentDir(), beam.inMotion());
                case 2 -> new Beam(beam.id(), beam.x(), beam.y() + 1, beam.currentDir(), beam.inMotion());
                case 3 -> new Beam(beam.id(), beam.x() - 1, beam.y(), beam.currentDir(), beam.inMotion());
                default -> throw new IllegalStateException(STR."Unknown direction \{beam.currentDir()}");
            };
        }

        private Beam changeDir(Beam beam, String tile, boolean oldBeam) {
            return (!beam.inMotion) ? beam : switch (tile) {
                case "/" -> switch (beam.currentDir()) {
                    case 0 -> new Beam(beam.id(), beam.x(), beam.y(), 1, beam.inMotion());
                    case 1 -> new Beam(beam.id(), beam.x(), beam.y(), 0, beam.inMotion());
                    case 2 -> new Beam(beam.id(), beam.x(), beam.y(), 3, beam.inMotion());
                    case 3 -> new Beam(beam.id(), beam.x(), beam.y(), 2, beam.inMotion());
                    default -> throw new IllegalStateException(STR."Unknown direction \{beam.currentDir()}");
                };
                case "\\" -> switch (beam.currentDir()) {
                    case 0 -> new Beam(beam.id(), beam.x(), beam.y(), 3, beam.inMotion());
                    case 1 -> new Beam(beam.id(), beam.x(), beam.y(), 2, beam.inMotion());
                    case 2 -> new Beam(beam.id(), beam.x(), beam.y(), 1, beam.inMotion());
                    case 3 -> new Beam(beam.id(), beam.x(), beam.y(), 0, beam.inMotion());
                    default -> throw new IllegalStateException(STR."Unknown direction \{beam.currentDir()}");
                };
                case "-" -> {
                    if (beam instanceof Beam(int cid, int cx, int cy, int dir, boolean cm) && (dir == 0 || dir == 2)) {
                        //left first for old beam
                        if (dir == 0) {
                            yield oldBeam ? new Beam(cid, cx, cy, 3, cm) : new Beam(idGenerator.getAndIncrement(), cx, cy, 1, cm);
                        } else {
                            yield oldBeam ? new Beam(cid, cx, cy, 1, cm) : new Beam(idGenerator.getAndIncrement(), cx, cy, 3, cm);
                        }
                    }
                    yield beam;
                }
                case "|" -> {
                    if (beam instanceof Beam(int cid, int cx, int cy, int dir, boolean cm) && (dir == 1 || dir == 3)) {
                        //left first for old beam
                        if (dir == 1) {
                            yield oldBeam ? new Beam(cid, cx, cy, 0, cm) : new Beam(idGenerator.getAndIncrement(), cx, cy, 2, cm);
                        } else {
                            yield oldBeam ? new Beam(cid, cx, cy, 2, cm) : new Beam(idGenerator.getAndIncrement(), cx, cy, 0, cm);
                        }
                    }
                    yield beam;

                }
                default -> beam;
            };
        }

        Beam move(final Tile tile, final boolean oldBeam, final int maxX, final int maxY) {
            if (tile.visited.contains(this)) {
                return new Beam(this.id(), this.x(), this.y(), this.currentDir(), false);
            }
            final int oldX = this.x();
            final int oldY = this.y();
            Beam out = switch (tile.label) {
                case "." -> performStep(this);
                case "/", "\\", "|", "-" -> performStep(changeDir(this, tile.label, oldBeam));
                default -> throw new IllegalStateException(STR."Unknown tile \{tile}");
            };
            out = out.x() < 0 || out.x() > maxX || out.y() < 0 || out.y() > maxY ? this : out;
            if (oldX == out.x() && oldY == out.y()) {
                return new Beam(out.id(), out.x(), out.y(), out.currentDir(), false);
            }
            return out;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Beam beam = (Beam) o;
            return id == beam.id && x == beam.x && y == beam.y && currentDir == beam.currentDir;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, currentDir);
        }
    }

    private record StartPos(int x, int y, int dir) {
    }

    //North = 0, East = 1, South = 2, West = 3

    private static Set<Tile> findEnergizedTiles(final List<List<Solution.Tile>> grid) {
        final Set<Tile> out = new HashSet<>();
        for (List<Tile> row : grid) {
            out.addAll(row.stream().filter(t -> !t.visited.isEmpty()).toList());
        }
        return out;
    }

    public static void main(String[] args) {
        final String name = "ex_1";
        final List<List<Tile>> grid = parseFile(name);
        final int maxX = grid.getFirst().size() - 1;
        final int maxY = grid.size() - 1;
        //System.out.println(createStartPositions(0,0, 1,1));
        System.out.println(STR."Solution part 1: \{Stream.of(new StartPos(0, 0, 1)).map(startPos -> findEnergizedTiles(startPos, copyInitialGrid(grid))).reduce(0, Integer::sum)}");
        System.out.println(STR."Solution part 2: \{createStartPositions(maxX, maxY).stream().map(startPos -> findEnergizedTiles(startPos, copyInitialGrid(grid))).max(Integer::compare)}");
    }

    private static List<List<Tile>> copyInitialGrid(List<List<Tile>> grid) {
        final List<List<Tile>> out = new ArrayList<>();
        for (List<Tile> row : grid) {
            out.add(new ArrayList<>(row.stream().map(t -> new Tile(t.label, t.x, t.y)).toList()));
        }
        return out;
    }

    private static List<StartPos> createStartPositions(int maxX, int maxY) {
        final List<StartPos> out = new ArrayList<>();
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                out.addAll(createStartPositions(x, y, maxX, maxY));
            }
        }
        return out;
    }

    private static List<StartPos> createStartPositions(final int x, final int y, final int maxX, final int maxY) {
        final List<StartPos> out = new ArrayList<>();
        IntStream.rangeClosed(0, 3).forEach(dir -> {
            switch (dir) {
                case 0:
                    if (y > 0 && y + 1 <= maxY) {
                        out.add(new StartPos(x, y, dir));
                    }
                    break;
                case 1:
                    if (x < maxX) {
                        out.add(new StartPos(x, y, dir));
                    }
                    break;
                case 2:
                    if (y <= maxY) {
                        out.add(new StartPos(x, y, dir));
                    }
                    break;
                default:
                    if (x - 1 >= 0) {
                        out.add(new StartPos(x, y, dir));
                    }
                    break;
            }
        });
        return out;
    }

    private static int findEnergizedTiles(final StartPos start, final List<List<Tile>> grid) {
        final int maxX = grid.getFirst().size() - 1;
        final int maxY = grid.size() - 1;
        List<Beam> beams = new ArrayList<>();
        beams.add(new Beam(idGenerator.getAndIncrement(), start.x(), start.x(), start.dir(), true));
        int steps = 0;
        int times = 0;
        Set<Tile> energizedTiles = findEnergizedTiles(grid);
        while (times != 20) {
            final List<Beam> oldBeams = new ArrayList<>();
            final List<Beam> newBeams = new ArrayList<>();
            final int energizedTilesOccurrences = energizedTiles.size();
            final int foo = energizedTiles.stream().map(t -> t.visited.size()).reduce(0, Integer::sum);
            int beamsInMotion = 0;
            for (Beam beam : beams) {
                //System.out.println(beam);
                final Tile tile = grid.get(beam.y()).get(beam.x());
                if (beam.inMotion()) {
                    //System.out.println(tile);
                    if ((tile.label.equals("|") || tile.label.equals("-"))) {
                        newBeams.add(beam.move(tile, false, maxX, maxY));
                        // System.out.println(newBeams.size());
                    }
                    final Beam movedOldBeam = beam.move(tile, true, maxX, maxY);
                    oldBeams.add(movedOldBeam);
                    beamsInMotion++;
                }
                tile.visited.add(beam);
            }
            oldBeams.addAll(newBeams);
            beams = oldBeams;
            energizedTiles = findEnergizedTiles(grid);
            if (steps % 10 == 0) {
                //System.out.println("steps " + steps + " beams " + beams.size() + " moving " + beamsInMotion + ", e-tiles " + energizedTiles.size() + " foo " + (energizedTiles.stream().map(t -> t.visited.size()).reduce(0, Integer::sum) - foo));
            }
            times += energizedTilesOccurrences == energizedTiles.size() ? 1 : -1 * times;
            steps++;
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
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

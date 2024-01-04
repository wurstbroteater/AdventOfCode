import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Solution {

    //North = 0, East = 1, South = 2, West = 3
    private static class Tile {
        String label;
        int x;
        int y;
        int energy;

        Tile(String label, int x, int y) {
            this.label = label;
            this.x = x;
            this.y = y;
            energy = 0;
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
            return "Tile[" +
                    "label='" + label + '\'' +
                    ", energy=" + energy +
                    ']';
        }
    }

    private record Beam(int x, int y, int currentDir) {

        private Beam performStep(Beam beam) {
            return switch (beam.currentDir()) {
                case 0 -> new Beam(beam.x(), beam.y() - 1, beam.currentDir());
                case 1 -> new Beam(beam.x() + 1, beam.y(), beam.currentDir());
                case 2 -> new Beam(beam.x(), beam.y() + 1, beam.currentDir());
                case 3 -> new Beam(beam.x() - 1, beam.y(), beam.currentDir());
                default -> throw new IllegalStateException(STR."Unknown direction \{beam.currentDir()}");
            };
        }

        private Beam changeDir(Beam beam, String tile, boolean oldBeam) {
            if (tile.equals("/")) {
                switch (beam.currentDir()) {
                    case 0 -> new Beam(beam.x(), beam.y(), 1);
                    case 1 -> new Beam(beam.x(), beam.y(), 2);
                    case 2 -> new Beam(beam.x(), beam.y(), 3);
                    case 3 -> new Beam(beam.x(), beam.y(), 0);
                    default -> throw new IllegalStateException(STR."Unknown direction \{beam.currentDir()}");
                }
            } else if (tile.equals("\\")) {
                switch (beam.currentDir()) {
                    case 0 -> new Beam(beam.x(), beam.y(), 3);
                    case 1 -> new Beam(beam.x(), beam.y(), 0);
                    case 2 -> new Beam(beam.x(), beam.y(), 1);
                    case 3 -> new Beam(beam.x(), beam.y(), 2);
                    default -> throw new IllegalStateException(STR."Unknown direction \{beam.currentDir()}");
                }
            } else if (tile.equals("-") && beam instanceof Beam(int cx, int cy, int dir) && (dir == 0 || dir == 2)) {
                //left first for old beam
                if (dir == 0) {
                    return oldBeam ? new Beam(cx, cy, 3) : new Beam(cx, cy, 1);
                } else {
                    return oldBeam ? new Beam(cx, cy, 1) : new Beam(cx, cy, 3);
                }
            } else if (tile.equals("|") && beam instanceof Beam(int cx, int cy, int dir) && (dir == 1 || dir == 3)) {
                //left first for old beam
                if (dir == 1) {
                    return oldBeam ? new Beam(cx, cy, 0) : new Beam(cx, cy, 2);
                } else {
                    return oldBeam ? new Beam(cx, cy, 2) : new Beam(cx, cy, 0);
                }
            }
            return beam;
        }

        Beam move(final String tile, final boolean oldBeam, final int maxX, final int maxY) {
            Beam out = switch (tile) {
                case "." -> performStep(this);
                case "/", "\\", "|", "-" -> performStep(changeDir(this, tile, oldBeam));
                default -> throw new IllegalStateException(STR."Unknown tile \{tile}");
            };
            return out.x() < 0 || out.x() > maxX || out.y() < 0 || out.y() > maxY ? this : out;
        }
    }

    private static Set<Tile> findEnergizedTiles(final List<List<Solution.Tile>> grid) {
        final Set<Tile> out = new HashSet<>();
        for (List<Tile> row : grid) {
            out.addAll(row.stream().filter(t -> t.energy > 0).toList());
        }
        return out;
    }

    public static void main(String[] args) {
        final List<List<Tile>> grid = parseFile("ex_1");
        final int maxX = grid.getFirst().size() - 1;
        final int maxY = grid.size() - 1;
        List<Beam> beams = new ArrayList<>();
        beams.add(new Beam(0, 0, 1));
        int steps = 200;
        Set<Tile> energizedTiles = findEnergizedTiles(grid);
        while (steps > 0) {
            final List<Beam> oldBeams = new ArrayList<>();
            final List<Beam> newBeams = new ArrayList<>();

            final int energizedTilesOccurrences = energizedTiles.size();
            for (Beam beam : beams) {
                //System.out.println(beam);

                final Tile tile = grid.get(beam.y()).get(beam.x());
                tile.energy = tile.energy + 1;
                //System.out.println(tile);

                if ((tile.label.equals("|") || tile.label.equals("-"))) {
                    newBeams.add(beam.move(tile.label, false, maxX, maxY));
                    System.out.println(newBeams.size());
                }
                oldBeams.add(beam.move(tile.label, true, maxX, maxY));
            }
            oldBeams.addAll(newBeams);
            beams = oldBeams;
            energizedTiles = findEnergizedTiles(grid);
            steps--;
            if (energizedTilesOccurrences == energizedTiles.size()) {
                //break;
            }
        }
        System.out.println("steps " + steps);
        System.out.println(energizedTiles.size());
        grid.forEach(r -> {
            for (Tile t : r) {
                if (t.energy > 0) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        });
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
            //return fileStream.map(l -> Arrays.stream(l.split("")).map(Tile::new).toList()).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;


//USE JAVA 21 OR HIGHER
public class Solution {
    private record Galaxy(long id, long x, long y) {
        long distance(Galaxy galaxy) {
            final long dx = Math.abs(galaxy.x() - this.x);
            final long dy = Math.abs(galaxy.y() - this.y);
            return dx + dy;
        }
    }

    private record Universe(List<Galaxy> galaxies, List<Integer> emptyRows, List<Integer> emptyColumn) {
        HashMap<String, Long> findShortestPaths() {
            final HashMap<String, Long> shortestPaths = new HashMap<>();
            galaxies.forEach(g -> {
                for (Galaxy galaxy : galaxies) {
                    if (g.equals(galaxy)) {
                        continue;
                    }
                    final long distance = g.distance(galaxy);
                    final String pathName = STR."\{Math.max(g.id(), galaxy.id())}<>\{Math.min(g.id(), galaxy.id())}";
                    final Long currentBest = shortestPaths.getOrDefault(pathName, null);
                    if (currentBest == null || currentBest > distance) {
                        shortestPaths.put(pathName, distance);
                    }
                }
            });
            return shortestPaths;
        }
    }

    public static void main(String[] args) {
        final List<Integer> universeExpansionFactors = List.of(2, 1000000);
        for (int i = 0; i < universeExpansionFactors.size(); i++) {
            System.out.println(STR."Solution part \{i == 0 ? 1 : 2}: \{parseFile("puzzle", universeExpansionFactors.get(i)).findShortestPaths().values().stream().reduce(0L, Long::sum)}");
        }
    }


    private static Universe parseFile(final String name, final int universeExpansionFactor) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final List<List<String>> grid = new ArrayList<>(fileStream.map(l -> new ArrayList<>(Arrays.stream(l.trim().split("")).toList())).toList());
            return new Universe(findGalaxies(grid, universeExpansionFactor), findEmptyRows(grid), findEmptyColumns(grid));
        } catch (IOException ex) {
            throw new IllegalStateException(STR."Error while parsing schematic file: \{ex.getCause()}");
        }
    }

    private static List<Galaxy> findGalaxies(final List<List<String>> grid, final int universeExpansionFactor) {
        // both lists are sorted in descending order (max is first)
        final List<Integer> emptyColumns = findEmptyColumns(grid);
        final List<Integer> emptyRows = findEmptyRows(grid);
        final List<Galaxy> out = new ArrayList<>();
        int id = 1;
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(y).size(); x++) {
                if (grid.get(y).get(x).equals("#")) {
                    int finalX = x;
                    long xOffset = (universeExpansionFactor - 1) * emptyColumns.stream().filter(r -> r < finalX).count();
                    int finalY = y;
                    long yOffset = (universeExpansionFactor - 1) * emptyRows.stream().filter(r -> r < finalY).count();
                    out.add(new Galaxy(id, x + xOffset, y + yOffset));
                    id++;
                }
            }
        }
        return out;
    }


    private static List<Integer> findEmptyRows(final List<List<String>> grid) {
        final List<Integer> emptyRows = new ArrayList<>();
        for (int i = grid.size() - 1; i >= 0; i--) {
            if (grid.get(i).stream().allMatch(e -> e.equals("."))) {
                emptyRows.add(i);
            }
        }
        return emptyRows;
    }

    private static List<Integer> findEmptyColumns(final List<List<String>> grid) {
        final List<Integer> emptyRows = new ArrayList<>();
        for (int x = grid.getFirst().size() - 1; x >= 0; x--) {
            boolean addIt = true;
            for (final List<String> strings : grid) {
                if (strings.get(x).equals("#")) {
                    addIt = false;
                    break;
                }
            }
            if (addIt) {
                emptyRows.add(x);
            }
        }
        return emptyRows;
    }


}

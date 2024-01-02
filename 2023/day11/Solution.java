import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Solution {
    private record Galaxy(int id, int x, int y) {
        int distance(Galaxy galaxy) {
            final int dx = Math.abs(galaxy.x() - this.x);
            final int dy = Math.abs(galaxy.y() - this.y);
            return dx + dy;
        }
    }

    public static void main(String[] args) {
        final List<Galaxy> galaxies = parseFile("puzzle");
        final HashMap<String, Integer> shortestPaths = new HashMap<>();
        galaxies.forEach(g -> {
            for (Galaxy galaxy : galaxies) {
                if (g.equals(galaxy)) {
                    continue;
                }
                final int distance = g.distance(galaxy);
                final String pathName = Math.max(g.id(), galaxy.id()) + "<>" + Math.min(g.id(), galaxy.id());
                final Integer currentBest = shortestPaths.getOrDefault(pathName, null);
                if (currentBest == null || currentBest > distance) {
                    shortestPaths.put(pathName, distance);
                }
            }
        });
        System.out.println("Solution part 1: " + shortestPaths.values().stream().reduce(0, Integer::sum));
    }


    private static List<Galaxy> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final List<List<String>> grid = new ArrayList<>(fileStream.map(l -> new ArrayList<>(Arrays.stream(l.trim().split("")).toList())).toList());
            expandGrid(grid);
            return findGalaxies(grid);
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }

    private static List<Galaxy> findGalaxies(final List<List<String>> grid) {
        final List<Galaxy> out = new ArrayList<>();
        int id = 1;
        for (int y = 0; y < grid.size(); y++) {
            for (int x = 0; x < grid.get(y).size(); x++) {
                if (grid.get(y).get(x).equals("#")) {
                    out.add(new Galaxy(id, x, y));
                    id++;
                }
            }
        }
        return out;
    }

    private static void expandGrid(List<List<String>> grid) {
        final List<Integer> emptyColumns = findEmptyColumn(grid);
        findEmptyRow(grid).forEach(r -> grid.add(r + 1, createEmptyRow(grid.getFirst().size())));
        emptyColumns.forEach(c -> grid.forEach(l -> l.add(c + 1, ".")));
    }

    private static List<Integer> findEmptyRow(final List<List<String>> grid) {
        final List<Integer> emptyRows = new ArrayList<>();
        for (int i = grid.size() - 1; i >= 0; i--) {
            if (grid.get(i).stream().allMatch(e -> e.equals("."))) {
                emptyRows.add(i);
            }
        }
        return emptyRows;
    }

    private static List<Integer> findEmptyColumn(final List<List<String>> grid) {
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

    private static List<String> createEmptyRow(final int length) {
        final List<String> out = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            out.add(".");
        }
        return out;
    }

}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

class Solution {

    private record SignificantLocations(PriorityQueue<Integer> left,
                                        PriorityQueue<Integer> right,
                                        Map<Integer, Integer> occurrences) {

        long distance() {
            long out = 0;
            final int size = left.size();
            final List<Integer> lRestore = new ArrayList<>(size);
            final List<Integer> rRestore = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                final int l = left.poll();
                lRestore.add(l);
                final int r = right.poll();
                rRestore.add(r);
                out += Math.abs(l - r);
            }
            left.addAll(lRestore);
            right.addAll(rRestore);
            return out;
        }

        long similarityScore() {
            long out = 0;
            final int size = left.size();
            final List<Integer> lRestore = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                final int l = left.poll();
                lRestore.add(l);
                out += l * occurrences.get(l);
            }
            left.addAll(lRestore);
            return out;
        }
    }


    public static void main(final String[] args) {
        for (final String input : List.of("ex_0", "puzzle")) {
            final SignificantLocations locations = parseFile(input);
            final String prefix = "[" + input + "] ";
            System.out.println(prefix + "Solution part 1: " + locations.distance());
            System.out.println(prefix + "Solution part 2: " + locations.similarityScore());
        }
    }

    private static SignificantLocations parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final PriorityQueue<Integer> left = new PriorityQueue<>();
            final PriorityQueue<Integer> right = new PriorityQueue<>();
            fileStream.forEach(l -> {
                final String[] intermediate = l.trim().split(" {3}");
                if (intermediate.length != 2) {
                    throw new IllegalArgumentException("Invalid input: " + l);
                }
                left.add(Integer.parseInt(intermediate[0].trim()));
                right.add(Integer.parseInt(intermediate[1].trim()));
            });
            return new SignificantLocations(left, right, countOccurrences(left, right));
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }

    private static Map<Integer, Integer> countOccurrences(final PriorityQueue<Integer> left,
                                                          final PriorityQueue<Integer> right) {
        final Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (final Integer num : right) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }
        final Map<Integer, Integer> resultMap = new HashMap<>();
        for (final Integer num : left) {
            resultMap.put(num, frequencyMap.getOrDefault(num, 0));
        }
        return resultMap;
    }

}

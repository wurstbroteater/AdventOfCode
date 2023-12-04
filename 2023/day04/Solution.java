import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    record Card(int id, Set<Integer> numbers, Set<Integer> winningNumbers) {
        private static Set<Integer> matchedNumbers;

        Card {
            matchedNumbers = new HashSet<>(numbers);
            matchedNumbers.retainAll(winningNumbers);
        }

        Set<Integer> getMatchedNumbers() {
            return matchedNumbers;
        }
    }

    ;

    public static void main(String[] args) {
        final List<Card> puzzle = readFile("ex_1");
        System.out.println(puzzle.get(0));
        System.out.println(puzzle.get(0).getMatchedNumbers());
    }

    private static List<Card> readFile(final String name) {
        List<Card> out = new ArrayList<>();
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {

            fileStream.forEach(l -> {
                String[] intermediate = l.split(": ");
                final int id = Integer.parseInt(intermediate[0].split(" ")[1]);
                intermediate = intermediate[1].split(" \\| ");
                System.out.println(id);

                out.add(new Card(id, Arrays.stream(intermediate[0].replaceAll(" {2}", " 0").split(" ")).map(Integer::parseInt).collect(Collectors.toSet()), Arrays.stream(intermediate[1].replaceAll(" {2}", " 0").split(" ")).map(Integer::parseInt).collect(Collectors.toSet())));
            });

        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
        return out;
    }
}

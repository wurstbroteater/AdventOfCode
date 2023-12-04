import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {
    private static final List<Card> puzzle = readFile("ex_1");

    private record Card(int id, int points, Set<Integer> winningNumbers) {

        Set<Integer> getEarnedScratchcardIds() {
            final Set<Integer> out = new HashSet<>();
            for (int i = 1; i <= winningNumbers.size(); i++) {
                out.add(this.id + i);
            }
            return out;
        }

        List<Card> getEarnedScratchcards() {
            final List<Card> out = new ArrayList<>();
            for (int i = 0; i < winningNumbers.size(); i++) {
                out.add(puzzle.get(this.id + i));
            }
            return out;
        }

        @Override
        public boolean equals(Object c) {
            return c instanceof Card && this.id == ((Card) c).id;
        }
    }


    public static void main(String[] args) {
        final List<Card> copies = new ArrayList<>();
        puzzle.forEach(it -> {
            var l = it.getEarnedScratchcards();
            System.out.println(l.stream().map(Card::id).toList());
            copies.addAll(l);
        });
        System.out.println("Solution part 1: " + puzzle.stream().map(Card::points).reduce(0, Integer::sum));
        System.out.println(copies.stream().map(Card::id).toList());

    }

    private static List<Card> readFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return fileStream.map(l -> {
                String[] intermediate = l.split(": ");
                final int id = Integer.parseInt(intermediate[0].split("Card ")[1].trim());
                intermediate = intermediate[1].split(" \\| ");
                final Set<Integer> winning = Arrays.stream(intermediate[0].replaceAll(" {2}", " ").trim().split(" ")).map(e -> Integer.parseInt(e.trim()))
                        .collect(Collectors.toSet());
                winning.retainAll(Arrays.stream(intermediate[1].replaceAll(" {2}", " ").trim().split(" ")).map(e -> Integer.parseInt(e.trim()))
                        .collect(Collectors.toSet()));
                return new Card(id, winning.size() <= 1 ? winning.size() : 2 << winning.size() - 2, winning);
            }).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Solution {

    public static void main(String[] args) {
        final List<Hand> hands = parseFile("ex_1");
        hands.sort((a, b) -> a.getKind().ordinal() - b.getKind().ordinal());
        hands.sort(Hand::compareTo);
        long winning = 0;
        for (int i = 0; i < hands.size(); i++) {
            final Hand hand = hands.get(i);
            hand.setRank(i + 1);
            winning += (long) hand.getBid() * hand.getRank();
        }

        hands.forEach(System.out::println);
        //252485914 to low
        System.out.println("Solution part 1: " + winning);

    }

    private static List<Hand> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            //final List<String> fileContent = new ArrayList<>(fileStream.toList());
            return new ArrayList<>(fileStream.toList().stream().map(l -> new Hand(l.split(" ")[0], Integer.parseInt(l.split(" ")[1]))).toList());
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

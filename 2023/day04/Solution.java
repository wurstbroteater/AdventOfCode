import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {
	private record Card(int id, Set<Integer> numbers, Set<Integer> winningNumbers) {
		Set<Integer> getMatchedNumbers() {
			final Set<Integer> matchedNumbers = new HashSet<>(Set.copyOf(numbers));
			matchedNumbers.retainAll(winningNumbers);
			return matchedNumbers;
		}

		int getPoints() {
			return getMatchedNumbers().size() <= 1 ? getMatchedNumbers().size() : 2 << (getMatchedNumbers().size() - 2);
		}
	}

	public static void main(String[] args) {
		final List<Card> puzzle = readFile("puzzle");
		// System.out.println(puzzle.get(0));
		// System.out.println(puzzle.get(0).getMatchedNumbers());
		// System.out.println(puzzle.get(0).getPoints());
		System.out.println("Solution part 1: " + puzzle.stream().map(Card::getPoints).reduce(0, Integer::sum));
	}

	private static List<Card> readFile(final String name) {
		List<Card> out = new ArrayList<>();
		try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {

			fileStream.forEach(l -> {
				String[] intermediate = l.split(": ");
				final int id = Integer.parseInt(intermediate[0].split("Card ")[1].trim());
				intermediate = intermediate[1].split(" \\| ");
				// System.out.println( Arrays.stream(intermediate[0].replaceAll(" {2}", " ").trim().split(" ")).toList());
				// System.out.println( Arrays.stream(intermediate[1].replaceAll(" {2}", " ").trim().split(" ")).toList());
				// System.out.println(id);
				out.add(new Card(id, Arrays.stream(intermediate[0].replaceAll(" {2}", " ").trim().split(" ")).map(e -> Integer.parseInt(e.trim()))
						.collect(Collectors.toSet()),
						Arrays.stream(intermediate[1].replaceAll(" {2}", " ").trim().split(" ")).map(e -> Integer.parseInt(e.trim()))
								.collect(Collectors.toSet())));
			});
		} catch (IOException ex) {
			throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
		}
		return out;
	}
}

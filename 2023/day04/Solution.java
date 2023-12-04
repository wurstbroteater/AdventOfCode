import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {
	private record Card(int id, Set<Integer> winningNumbers, Set<Integer> numbers) {
		Set<Integer> getMatchedNumbers() {
			final Set<Integer> matchedNumbers = new HashSet<>(Set.copyOf(numbers));
			matchedNumbers.retainAll(winningNumbers);
			return matchedNumbers;
		}

		int getPoints() {
			return getMatchedNumbers().size() <= 1 ? getMatchedNumbers().size() : 2 << (getMatchedNumbers().size() - 2);
		}

		Set<Integer> getEarnedScratchcardIds() {
			final Set<Integer> out = new HashSet<>();
			out.add(id);
			for (int i = 1; i <= getMatchedNumbers().size(); i++) {
				out.add(this.id + i);
			}
			if (out.size() == 1) {
				final int idToResolve = out.stream().toList().get(0);
				return puzzle.get(idToResolve - 1).getMatchedNumbers();
			}
			return out;
		}
	}
	static final List<Card> puzzle = readFile("ex_1");

	public static void main(String[] args) {
		puzzle.forEach(it -> System.out.println(it.getEarnedScratchcardIds()));
		System.out.println("Solution part 1: " + puzzle.stream().map(Card::getPoints).reduce(0, Integer::sum));
	}

	private static List<Card> readFile(final String name) {
		List<Card> out = new ArrayList<>();
		try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
			fileStream.forEach(l -> {
				String[] intermediate = l.split(": ");
				final int id = Integer.parseInt(intermediate[0].split("Card ")[1].trim());
				intermediate = intermediate[1].split(" \\| ");
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

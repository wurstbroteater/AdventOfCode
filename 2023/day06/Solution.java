import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Solution {
    private record Tuple(long time, long best) {
    }

    private record RaceHistory(List<Long> times, List<Long> distances) {
        private static List<Tuple> races;

        RaceHistory {
            races = new ArrayList<>();
            for (int i = 0; i < times.size(); i++) {
                races.add(new Tuple(times.get(i), distances.get(i)));
            }
        }

        public List<Tuple> getRaces() {
            return races;
        }

        public Tuple getRace() {
            return new Tuple(Long.parseLong(times.stream().map(Object::toString).reduce("", String::concat)), Long.parseLong(distances.stream().map(Object::toString).reduce("", String::concat)));
        }
    }

    public static void main(String[] args) {
        final RaceHistory raceHistory = parseFile("puzzle");
        System.out.println(raceHistory);
        System.out.println("Solution part 1: " + raceHistory.getRaces().stream().map(Solution::calcWinning).reduce(1L, (a, b) -> a * b));
        System.out.println("Solution part 2: " + calcWinning(raceHistory.getRace()));
    }

    private static long calcWinning(Tuple race) {
        long winning = 0;
        for (long pushT = 1; pushT <= race.time(); pushT++) {
            final long distance = pushT * (race.time() - pushT);
            winning += distance > race.best() ? 1 : 0;
        }
        return winning;
    }

    private static RaceHistory parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final List<String> fileContent = new ArrayList<>(fileStream.toList());
            return new RaceHistory(Arrays.stream(fileContent.removeFirst().replaceAll("Time: *", "").replaceAll(" +", " ").split(" ")).map(Long::parseLong).toList(),
                    Arrays.stream(fileContent.removeFirst().replaceAll("Distance: *", "").replaceAll(" +", " ").split(" ")).map(Long::parseLong).toList());
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

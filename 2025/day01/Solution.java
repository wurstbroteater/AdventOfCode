import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    enum Rotation {LEFT, RIGHT}

    public record Spin(Rotation rotation, int distance) {
    }


    public static void main(final String[] args) {

        for (final String input : List.of("ex_0", "puzzle")) {
            List<Spin> spins = parseFile(input);
            int dial = 50;
            int part1 = 0;
            int part2 = 0;
            for (final Spin spin : spins) {
                final int old = dial;
                final int times = (old + spin.distance()) / 100;
                dial = nextDial(spin, dial);
                if (dial == 0) {
                    part1++;
                }
                if (times > 0) {

                    part2 += times;
                }
                //System.out.printf("[%s%d] dial from %d to %d times %d\n", spin.rotation.name().charAt(0), spin.distance, old, dial, times);
                if (dial < 0) {
                    throw new AssertionError("Dial can't be negative");
                }
            }
            //1034
            System.out.printf("[%s] Part 1: %d %n", input, part1);
            //4661 to low, 4971, 5101, 5920 inco
            System.out.printf("[%s] Part 2: %d %n", input, part2);
        }
    }


    private static int nextDial(final Spin spin, final int start) {
        final int s = spin.distance() % 100;
        final int out = spin.rotation == Rotation.LEFT ? start - s : start + s;
        return (out > 0 ? out : 100 + out) % 100;
    }

    private static List<Spin> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return fileStream.map(l -> new Spin(
                    l.charAt(0) == 'L' ? Rotation.LEFT : Rotation.RIGHT,
                    Integer.parseInt(l.substring(1))
            )).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }

}
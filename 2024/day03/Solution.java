import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class Solution {
    private static final String part1 = "mul\\((-?\\d+\\.?\\d*),(-?\\d+\\.?\\d*)\\)";
    private static final String part2 = "mul\\((-?\\d+\\.?\\d*),(-?\\d+\\.?\\d*)\\)|do\\(\\)|don't\\(\\)";

    public static void main(final String[] args) {
        for (final String input : List.of("ex_0", "ex_1", "puzzle")) {
            final String prefix = "[" + input + "] ";
            System.out.println(prefix + "Solution part 1: " + solve(parseFile(input, part1)));
            System.out.println(prefix + "Solution part 2: " + solve(filterByDont(parseFile(input, part2))));
        }
    }

    private static List<String> parseFile(final String name, final String regEx) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final Pattern findMulPattern = Pattern.compile(regEx);

            return fileStream
                    .flatMap(line -> {
                        Matcher matcher = findMulPattern.matcher(line);
                        List<String> lineMatches = new ArrayList<>();
                        while (matcher.find()) {
                            lineMatches.add(matcher.group());
                        }
                        return lineMatches.stream();
                    })
                    .toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getMessage(), ex);
        }
    }

    private static Long solve(final List<String> tokens) {
        return tokens.stream()
                .map(Solution::parseMul)
                .reduce(0L, Long::sum);

    }

    private static Long parseMul(final String validMulString) {
        final String[] intermediate = validMulString.replace("mul(", "").replace(")", "").split(",");
        return Long.parseLong(intermediate[0]) * Long.parseLong(intermediate[1]);
    }

    private static List<String> filterByDont(final List<String> input) {
        String last = "do()";
        final List<String> out = new ArrayList<>();
        for (final String s : input) {
            if (s.startsWith("mul") && "do()".equals(last)) {
                out.add(s);
            }
            if ("do()".equals(s) || "don't()".equals(s)) {
                last = s;
            }

        }
        return out;
    }
}

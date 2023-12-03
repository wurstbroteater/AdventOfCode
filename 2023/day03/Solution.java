import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Solution {
    private static final Map<Integer, List<Number>> lineToNumbers = new HashMap<>();

    private record Number(int value, int start, int end) {

        boolean isInRange(int x) {
            return x >= start && x <= end;
        }
    }

    private static final char[][] engineSchematic = parseSchematic("puzzle");

    public static void main(String[] args) {
        for (Integer key : lineToNumbers.keySet()) {
            System.out.println(key + " : " + lineToNumbers.get(key));
        }
        final List<Integer> adjacentNumbers = new ArrayList<>();
        for (int i = 0; i < engineSchematic.length; i++) {
            for (int j = 0; j < engineSchematic[i].length; j++) {
                final char current = engineSchematic[i][j];
                if (isSymbol(current)) {
                    adjacentNumbers.addAll(getAdjacentNumbers(i, j).stream().map(Number::value).toList());
                }
            }
        }
        System.out.println(adjacentNumbers);
        System.out.println("Solution part 1: " + adjacentNumbers.stream().reduce(0, Integer::sum));
    }

    private static List<Number> getAdjacentNumbers(int i, int j) {
        //same line
        final List<Number> out = new ArrayList<>(lineToNumbers.getOrDefault(i, new ArrayList<>())
                .stream().filter(n -> (n.isInRange(j - 1) || n.isInRange(j + 1))).toList());
        //line above
        final int lineAbove = i - 1;
        if (lineAbove >= 0) {
            out.addAll(lineToNumbers.getOrDefault(lineAbove, new ArrayList<>())
                    .stream().filter(n -> (n.isInRange(j - 1) || n.isInRange(j) || n.isInRange(j + 1))).toList());
        }
        //line below
        final int lineBelow = i + 1;
        if (lineBelow < engineSchematic.length) {
            out.addAll(lineToNumbers.getOrDefault(lineBelow, new ArrayList<>())
                    .stream().filter(n -> (n.isInRange(j - 1) || n.isInRange(j) || n.isInRange(j + 1))).toList());
        }
        return out;
    }

    private static boolean isDigit(Character c) {
        return c != null && c >= '0' && c <= '9';
    }

    private static boolean isSymbol(Character c) {
        return c != null && "/=+-:*!@#$%^&*()\"{}_[]|\\?/<>,".contains(c.toString());
    }

    private static boolean isPeriod(Character c) {
        return c != null && c == '.';
    }

    private static char[][] parseSchematic(final String name) {
        final char[][] schematic;
        //TODO: Maybe switch from BufferedReader to to filestream in general
        try (final BufferedReader bf = new BufferedReader(new FileReader(name));
             final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            String line = bf.readLine();
            schematic = new char[(int) fileStream.count()][line.length()];

            int lineCounter = 0;
            while (line != null) {
                String currentNumberFragment = null;
                final List<Number> numbersInLine = new ArrayList<>();
                schematic[lineCounter] = line.toCharArray();
                for (int i = 0; i < line.length(); i++) {
                    final Character nextChar = i == line.length() - 1 ? null : line.charAt(i + 1);
                    final Character currentChar = line.charAt(i);
                    //parse number
                    if (isDigit(currentChar)) {
                        currentNumberFragment = currentNumberFragment == null ? currentChar.toString() : currentNumberFragment + currentChar;
                        if (!isDigit(nextChar)) {
                            numbersInLine.add(new Number(Integer.parseInt(currentNumberFragment), i - currentNumberFragment.length() + 1, i));
                            currentNumberFragment = null;
                        }
                    }
                }
                lineToNumbers.put(lineCounter, numbersInLine);
                line = bf.readLine();
                lineCounter++;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing puzzle.txt: " + ex.getCause());
        }
        return schematic;
    }

}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    private static final int WINDOW_SIZE = 5;
    private static final Map<String, String> stringMappings = new HashMap<>();

    static {
        stringMappings.put("one", "o1e");
        stringMappings.put("two", "t2o");
        stringMappings.put("three", "t3e");
        stringMappings.put("four", "f4r");
        stringMappings.put("five", "f5e");
        stringMappings.put("six", "s6x");
        stringMappings.put("seven", "s7n");
        stringMappings.put("eight", "e8t");
        stringMappings.put("nine", "n9e");

        //edge cases
        stringMappings.put("oneight", "1ight");
        stringMappings.put("twone", "2ne");
        stringMappings.put("fiveight", "5ight");
        stringMappings.put("sevenine", "7ine");
        stringMappings.put("eightwo", "8wo");
        stringMappings.put("eighthree", "8hree");
    }

    public static void main(String[] args) {
        //ex_1 : [12, 38, 15, 77]             -> 142
        //ex_2 : [29, 83, 13, 24, 42, 14, 76] -> 281
        final List<String> puzzle = readFile("puzzle");
        System.out.println("Solution is " + puzzle.stream().map(Solution::findNumber).reduce(0, Integer::sum));
    }

    private static List<String> readFile(final String name) {
        final List<String> out = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(name))) {
            String line = bf.readLine();
            while (line != null) {
                line = replaceNumberNameByNumber(line, 0);
                out.add(line);
                line = bf.readLine();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing puzzle.txt: " + ex.getCause());
        }
        return out;
    }

    private static String replaceNumberNameByNumber(String line, int lastIndex) {
        String window;
        if (lastIndex + WINDOW_SIZE >= line.length()) {
            window = line.toLowerCase();
        } else {
            window = line.substring(lastIndex, lastIndex + WINDOW_SIZE).toLowerCase();
        }

        for (final String key : stringMappings.keySet()) {
            if (window.contains(key)) {
                final String temp = window;
                window = window.replace(key, stringMappings.get(key));
                line = line.replace(temp, window);
                break;
            }
        }

        if (lastIndex >= line.length() - 1) {
            return line;
        } else {
            return replaceNumberNameByNumber(line, ++lastIndex);
        }
    }

    private static int findNumber(final String line) {
        String first = null;
        String last = null;
        for (int i = 0; i < line.length(); i++) {
            final char currentFirst = line.charAt(i);
            if (first == null && currentFirst >= '0' && currentFirst <= '9') {
                first = String.valueOf(currentFirst);
            }
            final char currentLast = line.charAt(line.length() - 1 - i);
            if (last == null && currentLast >= '0' && currentLast <= '9') {
                last = String.valueOf(currentLast);
            }
        }
        if (first == null || last == null) {
            throw new IllegalStateException("Could not find all numbers!");
        }
        return Integer.parseInt(first + last);
    }
}
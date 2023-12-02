import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solution {
    private static final int MAX_R = 12;
    private static final int MAX_G = 13;
    private static final int MAX_B = 14;

    private record Bag(String color, int currentSize) {
    }

    private record Game(int id, List<List<Bag>> rounds) {
        int getMaxOccurrenceOfColor(final String color) {
            int total = 0;
            for (List<Bag> round : rounds) {
                for (Bag bag : round) {
                    if (bag.color().equals(color) && bag.currentSize >= total) {
                        total = bag.currentSize;
                    }
                }
            }
            return total;
        }

        int getCubePower() {
            return getMaxOccurrenceOfColor("red") * getMaxOccurrenceOfColor("green") * getMaxOccurrenceOfColor("blue");
        }
    }

    public static void main(String[] args) {
        final List<Game> games = readFile("puzzle");
        final int solutionPartOne = games.stream().filter(Solution::possibleGame).map(g -> g.id).reduce(0, Integer::sum);
        System.out.println("1. Solution is " + solutionPartOne);
        final int solutionPartTwo = games.stream().map(Game::getCubePower).reduce(0, Integer::sum);
        System.out.println("2. Solution is " + solutionPartTwo);
    }

    private static boolean possibleGame(final Game game) {
        return game.getMaxOccurrenceOfColor("red") <= MAX_R && game.getMaxOccurrenceOfColor("green") <= MAX_G && game.getMaxOccurrenceOfColor("blue") <= MAX_B;
    }

    private static List<Game> readFile(final String name) {
        final List<Game> out = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(name))) {
            String line = bf.readLine();
            while (line != null) {
                final String[] intermediate = line.split(": ");
                final String[] unparsedRounds = intermediate[1].split("; ");
                final List<List<Bag>> rounds = new ArrayList<>();
                for (String round : unparsedRounds) {
                    final List<Bag> bags = new ArrayList<>();
                    for (String bag : round.split(", ")) {
                        String[] temp = bag.split(" ");
                        bags.add(new Bag(temp[1].toLowerCase(), Integer.parseInt(temp[0])));
                    }
                    rounds.add(bags);
                }
                out.add(new Game(Integer.parseInt(intermediate[0].split(" ")[1]), rounds));
                line = bf.readLine();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing puzzle.txt: " + ex.getCause());
        }
        return out;
    }
}

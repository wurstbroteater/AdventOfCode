import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Solution {
    private static class Instruction {
        private final String label;
        private final boolean isAdd;
        private Integer focalLength;

        Instruction(final String label) {
            isAdd = label.contains("=");
            if (isAdd) {
                final String[] intermediate = label.split("=");
                focalLength = Integer.parseInt(intermediate[1]);
                this.label = intermediate[0];
            } else {
                this.label = label.split("-")[0];
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Instruction that = (Instruction) o;
            return hashCode() == that.hashCode() && Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return hashCode(label);
        }

        static int hashCode(String toHash) {
            int out = 0;
            for (int i = 0; i < toHash.length(); i++) {
                out += toHash.charAt(i);
                out *= 17;
                out = out % 256;
            }
            return out;
        }

        @Override
        public String toString() {
            return "[" + label + (isAdd ? " " + focalLength : "") + "]";
        }

    }

    public static void main(String[] args) {
        final List<Instruction> inits = parseFile("puzzle");
        System.out.println("Solution part 1: " + inits.stream().map(i -> Solution.Instruction.hashCode(i.label + (i.isAdd ? "=" + i.focalLength : "-"))).reduce(0, Integer::sum));
        final Map<Integer, List<Instruction>> partTwo = new HashMap<>();
        inits.forEach(i -> {
            final int currentHash = i.hashCode();
            final List<Instruction> currentMap = partTwo.getOrDefault(currentHash, new ArrayList<>());
            if (i.isAdd) {
                final int temp = currentMap.indexOf(i);
                if (temp == -1) {
                    currentMap.add(i);
                } else {
                    currentMap.get(temp).focalLength = i.focalLength;
                }
            } else {
                currentMap.remove(i);
            }
            if (currentMap.isEmpty()) {
                partTwo.remove(currentHash);
            } else {
                partTwo.put(currentHash, currentMap);
            }
        });
        long totalFocusingPower = 0L;
        for (final Integer hash : partTwo.keySet().stream().sorted().toList()) {
            final List<Instruction> remaining = partTwo.get(hash);
            for (int i = 0; i < remaining.size(); i++) {
                totalFocusingPower += (long) (hash + 1) * (i + 1) * remaining.get(i).focalLength;
            }
        }
        System.out.println("Solution part 2: " + totalFocusingPower);
    }


    private static List<Instruction> parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            return Arrays.stream(fileStream.reduce("", String::concat).split(",")).map(Instruction::new).toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

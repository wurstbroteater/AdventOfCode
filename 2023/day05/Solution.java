import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    public static void main(String[] args) {
        final List<Seed> seeds = readFile("puzzle");
        System.out.println("Solution part 1: " + seeds.stream().min(Comparator.comparing(Seed::getLocation)).orElseThrow().getLocation());
    }

    private static List<Seed> readFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final List<String> fileContent = new ArrayList<>(fileStream.toList());
            fileContent.add("");
            final List<Seed> seeds = Arrays.stream(fileContent.get(0).split("seeds: ")[1].split(" ")).map(n -> Long.parseLong(n.trim())).map(Seed::new).toList();
            // remove seed line followed by empty line
            fileContent.remove(0);
            fileContent.remove(0);
            //final Almanac almanac = new Almanac(seeds);
            String currrentMapName = fileContent.get(0).replace(" map:", "").trim();
            // remove first map name
            fileContent.remove(0);
            for (int i = 0; i < fileContent.size(); i++) {
                final String line = fileContent.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (Character.isDigit(line.charAt(0))) {
                    final List<Long> parsedNumbers = Arrays.stream(line.trim().split(" ")).map(n -> Long.parseLong(n.trim())).toList();
                    final long source = parsedNumbers.get(1);
                    final long dest = parsedNumbers.get(0);
                    final long length = parsedNumbers.get(2);
                    final boolean isNextLineEmpty = i + 1 < fileContent.size() && fileContent.get(i + 1).isEmpty();
                    for (Seed s : seeds) {
                        switch (currrentMapName) {
                            case "seed-to-soil" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getId(), s.getSoil());
                            case "soil-to-fertilizer" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getSoil(), s.getFertilizer());
                            case "fertilizer-to-water" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getFertilizer(), s.getWater());
                            case "water-to-light" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getWater(), s.getLight());
                            case "light-to-temperature" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getLight(), s.getTemperature());
                            case "temperature-to-humidity" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getTemperature(), s.getHumidity());
                            case "humidity-to-location" -> setSeedValue(currrentMapName, isNextLineEmpty, s, source, dest, length, s.getHumidity(), s.getLocation());
                        }
                    }
                } else {
                    currrentMapName = line.replace(" map:", "").trim();
                }
            }
            return seeds;
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }

    private static void setSeedValue(final String currentMapName, final boolean nextIsEmpty, final Seed seed, final long source, final long dest, final long length, final long id, final Long current) {
        if (current == null) {
            if (id >= source && id <= source + length - 1) {
                final long diff = id - source;
                seed.setValueByMapName(currentMapName, dest + diff);
            } else if (nextIsEmpty) {
                //if attribute hasn't been set yet, then set default
                seed.setValueByMapName(currentMapName, id);
            }
        }
    }
}

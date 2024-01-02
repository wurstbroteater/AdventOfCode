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
    private static final String START_LABEL = "A";
    private static final String END_LABEL = "Z";

    record Network(String directions, List<Node> startNode, Map<String, Node> nodes) {
    }

    private record Reached(Node start, Node end, int steps) {
    }

    public static void main(String[] args) {
        final Network net = parseFile("puzzle");
        System.out.println("Solution part 1: Steps to " + END_LABEL + " were " + calcStepsToEndLabel(net.startNode.getFirst(), net.directions()));
        System.out.println("Solution part 2: " + calcGhostStepsToEndLabels(net));
    }

    private static long calcGhostStepsToEndLabels(Network network) {
        final List<Reached> reachedNodes = findPathsToEndNodes(network.startNode(), network.directions);
        long lowestCommonMultiplier = 1L;
        for (Reached reachedNode : reachedNodes) {
            lowestCommonMultiplier = lowestCommonMultiplier * reachedNode.steps() / greatestCommonDivisor(lowestCommonMultiplier, reachedNode.steps());
        }
        return lowestCommonMultiplier;
    }

    private static List<Reached> findPathsToEndNodes(final List<Node> nodes, final String directions) {
        final List<Reached> reachedNodes = new ArrayList<>();
        for (Node n : nodes) {
            Node current = n;
            int steps = 0;
            Node foundZ = null;
            while (true) {
                //find reachable Z node
                while (steps == 0 || !current.getName().endsWith(END_LABEL)) {
                    current = current.getSibling(String.valueOf(directions.charAt(steps % directions.length())));
                    steps++;

                }
                if (foundZ == null) {
                    foundZ = current;
                    steps = 0;
                } else if (current.equals(foundZ)) {
                    reachedNodes.add(new Reached(n, current, steps));
                    break;
                }
            }
        }
        return reachedNodes;
    }

    private static long greatestCommonDivisor(long a, long b) {
        // if b=0 then a is the GCD
        // otherwise, replace a with b and b with modulus(a,b) as long as b != 0
        if (b == 0) {
            return a;
        } else {
            return greatestCommonDivisor(b, a % b);
        }
    }

    private static int calcStepsToEndLabel(Node current, final String directions) {
        int steps = 0;
        while (!current.getName().endsWith(END_LABEL)) {
            final String dir = String.valueOf(directions.charAt(steps % directions.length()));
            current = current.getSibling(dir);
            steps++;
        }
        return steps;
    }

    private static Network parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            final Map<String, Node> nodes = new HashMap<>();
            final List<String> fileContent = new ArrayList<>(fileStream.toList());
            final String directions = fileContent.removeFirst();
            fileContent.removeFirst();
            //add all nodes
            fileContent.forEach(l -> nodes.put(l.split(" = ")[0], new Node(l.split(" = ")[0])));
            //connect edges
            fileContent.forEach(l -> {
                String[] intermediate = l.split(" = ");
                final Node current = nodes.get(intermediate[0]);
                intermediate = intermediate[1].replaceAll("\\(", "").replaceAll("\\)", "").split(", ");
                current.setL(nodes.get(intermediate[0]));
                current.setR(nodes.get(intermediate[1]));
            });
            return new Network(directions, nodes.values().stream().filter(n -> n.getName().endsWith(START_LABEL)).toList(), nodes);
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

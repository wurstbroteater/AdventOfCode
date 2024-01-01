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

    private static final String START_LABEL = "AAA";
    private static final String END_LABEL = "ZZZ";


    record Network(String directions, Node startNode, Map<String, Node> nodes) {
    }

    public static void main(String[] args) {
        final Network net = parseFile("puzzle");
        System.out.println(net);
        int i = 0;
        Node current = net.startNode();
        long steps = 0;
        while (true) {
            if (i >= net.directions().length()) {
                i = 0;
            }
            if (current.getName().equals(END_LABEL)) {
                break;
            }
            String dir = net.directions().charAt(i) + "";
            current = current.getSibling(dir);
            steps++;
            i++;
        }

        System.out.println("Solution part 1: Steps to " + END_LABEL + " were " + steps);
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
            return new Network(directions, nodes.get(START_LABEL), nodes);
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing schematic file: " + ex.getCause());
        }
    }
}

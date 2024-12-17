import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Solution {
    record Rule(int x, int y) {
    }

    record Update(List<Integer> pages) {

        boolean isValid(final Graph graph) {
            return graph.insertUpdate(this) == -1;
        }

        Integer middle() {
            return pages.get(pages.size() / 2);
        }
    }

    record Graph(Map<Integer, List<Integer>> graph) {
        public int insertUpdate(final Update update) {
            final Map<Integer, Integer> visitOrder = new HashMap<>();
            int order = 0;
            for (final int page : update.pages()) {
                visitOrder.put(page, order++);
            }

            for (final int page : update.pages()) {
                final List<Integer> dependencies = graph.getOrDefault(page, Collections.emptyList());
                for (final int dependent : dependencies) {
                    if (visitOrder.containsKey(dependent) && visitOrder.get(dependent) < visitOrder.get(page)) {
                        return page; // Page caused the failure
                    }
                }
            }

            return -1; // No failure
        }

        public Update fixUpdate(final Update update) {
            List<Integer> topologicalOrder = topologicalSort(graph);

            // Reorder pages in the update based on the topological order
            List<Integer> reorderedPages = new ArrayList<>();
            Set<Integer> updatePages = new HashSet<>(update.pages());

            for (final int page : topologicalOrder) {
                if (updatePages.contains(page)) {
                    reorderedPages.add(page);
                }
            }

            return new Update(reorderedPages);
        }


        private List<Integer> topologicalSort(final Map<Integer, List<Integer>> graph) {
            final List<Integer> result = new ArrayList<>();
            final Set<Integer> visited = new HashSet<>();
            final Set<Integer> stack = new HashSet<>();

            for (final int node : graph.keySet()) {
                if (!visited.contains(node)) {
                    topologicalSortUtil(node, graph, visited, stack, result);
                }
            }

            return result;
        }


        private void topologicalSortUtil(final int node, Map<Integer, List<Integer>> graph,
                                         final Set<Integer> visited, Set<Integer> stack,
                                         final List<Integer> result) {
            visited.add(node);
            stack.add(node);

            for (final int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    topologicalSortUtil(neighbor, graph, visited, stack, result);
                }
            }

            stack.remove(node);
            result.addFirst(node); // Add to the result in reverse order
        }
    }

    final static class PrintQueue {
        private final List<Rule> orderingRules;
        private final List<Update> updates;

        PrintQueue(final List<Rule> orderingRules, final List<Update> updates) {
            this.orderingRules = orderingRules;
            this.updates = updates;
        }

        public Graph buildGraph() {
            final Map<Integer, List<Integer>> graph = new HashMap<>();

            for (final Rule rule : orderingRules) {
                graph.putIfAbsent(rule.x(), new ArrayList<>());
                graph.get(rule.x()).add(rule.y());
            }
            return new Graph(graph);
        }


        @Override
        public String toString() {
            return "PrintQueue{" +
                    "orderingRules=" + orderingRules +
                    ", updates=" + updates +
                    '}';
        }
    }

    public static void main(String[] args) {
        for (final String input : List.of("ex_0", "puzzle")) {
            final String prefix = "[" + input + "] ";
            final PrintQueue queue = parseFile(input);
            final var graph = queue.buildGraph();

            System.out.println(prefix + "Solution part 1: " + queue.updates.stream()
                    .filter(u -> u.isValid(graph))
                    .map(Update::middle)
                    .reduce(0, Integer::sum));

            System.out.println(prefix + "Solution part 2: " + queue.updates.stream()
                    .filter(u -> !u.isValid(graph))
                    .map(u -> graph.fixUpdate(u).middle())
                    .reduce(0, Integer::sum));
        }
    }


    private static PrintQueue parseFile(final String name) {
        try (final Stream<String> fileStream = Files.lines(Path.of(name), StandardCharsets.UTF_8)) {
            boolean fillRules = true;
            final List<Rule> rules = new ArrayList<>();
            final List<Update> updates = new ArrayList<>();
            for (final String line : fileStream.toList()) {
                if (line.isEmpty()) {
                    fillRules = false;
                } else if (fillRules) {
                    final List<Integer> intermediate = Arrays.stream(line.split("\\|")).map(Integer::parseInt).toList();
                    rules.add(new Rule(intermediate.getFirst(), intermediate.getLast()));
                } else {
                    updates.add(new Update(Arrays.stream(line.split(",")).map(Integer::parseInt).toList()));
                }
            }
            return new PrintQueue(rules, updates);
        } catch (IOException ex) {
            throw new IllegalStateException("Error while parsing file: " + ex.getMessage(), ex);
        }
    }
}

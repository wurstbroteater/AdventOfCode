import java.util.*;

public class Hand implements Comparable<Hand> {
    enum HAND_HIERARCHY {HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_KIND, FULL_HOUSE, FOUR_KIND, FIVE_KIND}

    static final String LETTER_HIERARCHY = "23456789TJQKA";

    private record Tuple(char letter, int occurrence) implements Comparable<Tuple> {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Tuple t) {
                return this.letter == t.letter;
            }
            return false;
        }

        @Override
        public int compareTo(Tuple o) {
            return LETTER_HIERARCHY.indexOf(this.letter) - LETTER_HIERARCHY.indexOf(o.letter);
        }
    }

    private final HAND_HIERARCHY kind;
    private final String l;
    private final int bid;
    private Integer rank;
    private final List<Tuple> zippedOccurrences;


    public Hand(String l, int bid) {
        this.l = l;
        this.bid = bid;
        final Map<Character, Integer> occurrences = new HashMap<>();
        final List<Character> all_letters = l.toUpperCase().chars().mapToObj(c -> (char) c).toList();
        all_letters.stream().distinct().forEach(letter -> occurrences.put(letter, Collections.frequency(all_letters, letter)));
        zippedOccurrences = zipMap(occurrences);
        //System.out.println(zippedOccurrences);
        if (zippedOccurrences.getFirst().occurrence() == 5) {
            kind = HAND_HIERARCHY.FIVE_KIND;
        } else if (zippedOccurrences.getFirst().occurrence() == 4) {
            kind = HAND_HIERARCHY.FOUR_KIND;
        } else if (zippedOccurrences.getFirst().occurrence() == 3 && zippedOccurrences.get(1).occurrence() == 2) {
            kind = HAND_HIERARCHY.FULL_HOUSE;
        } else if (zippedOccurrences.getFirst().occurrence() == 3) {
            kind = HAND_HIERARCHY.THREE_KIND;
        } else if (zippedOccurrences.getFirst().occurrence() == 2 && zippedOccurrences.get(1).occurrence() == 2) {
            kind = HAND_HIERARCHY.TWO_PAIR;
        } else if (zippedOccurrences.getFirst().occurrence() == 1 && zippedOccurrences.stream().filter(t -> Character.isAlphabetic(t.letter())).toList().isEmpty()) {
            kind = HAND_HIERARCHY.HIGH_CARD;
        } else {
            kind = HAND_HIERARCHY.ONE_PAIR;
        }
    }

    private List<Tuple> zipMap(final Map<Character, Integer> toSort) {
        final List<Map.Entry<Character, Integer>> entryList = new ArrayList<>(toSort.entrySet());
        // sort descending based on entry values
        entryList.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        // create a new list to preserve the order of sorted entries
        final List<Tuple> out = new ArrayList<>();
        for (final Map.Entry<Character, Integer> entry : entryList) {
            out.add(new Tuple(entry.getKey(), entry.getValue()));
        }
        return out;
    }

    HAND_HIERARCHY getKind() {
        return kind;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public int getBid() {
        return bid;
    }

    @Override
    public int compareTo(Hand o) {
        if (getKind().equals(o.getKind())) {
            for (int i = 0; i < l.length(); i++) {
                if (l.charAt(i) != o.l.charAt(i)) {
                    return LETTER_HIERARCHY.indexOf(l.charAt(i)) - LETTER_HIERARCHY.indexOf(o.l.charAt(i));
                }
            }
        }

        return getKind().ordinal() - o.getKind().ordinal();
    }

    @Override
    public String toString() {
        return "Hand[" +
                "l='" + l + '\'' +
                ", bid=" + bid +
                ", kind=" + kind +
                ", rank=" + rank +
                ']';
    }
}


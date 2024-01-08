import java.util.*;

//JAVA 21 PREVIEW LEVEL
public class Hand implements Comparable<Hand> {
    enum HAND_HIERARCHY {HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_KIND, FULL_HOUSE, FOUR_KIND, FIVE_KIND}

    static final String CARD_HIERARCHY = "23456789TJQKA";

    private record Tuple(char card, int occurrence) implements Comparable<Tuple> {
        @Override
        public boolean equals(final Object t) {
            if (t instanceof Tuple(char tCard, _)) {
                return this.card == tCard;
            }
            return false;
        }

        @Override
        public int compareTo(final Tuple t) {
            //max occurrence first
            return -1 * (this.occurrence - t.occurrence());
        }
    }

    private final HAND_HIERARCHY kind;
    private final String cards;
    private final int bid;
    private Integer rank;


    public Hand(final String cards, final int bid) {
        this.cards = cards;
        this.bid = bid;
        final List<Character> allCards = cards.toUpperCase().chars().mapToObj(c -> (char) c).toList();
        final List<Tuple> zippedOccurrences = allCards.stream().distinct().map(card -> new Tuple(card, Collections.frequency(allCards, card))).sorted().toList();
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
        } else if (zippedOccurrences.getFirst().occurrence() == 1 && zippedOccurrences.stream().filter(t -> Character.isAlphabetic(t.card())).toList().isEmpty()) {
            kind = HAND_HIERARCHY.HIGH_CARD;
        } else {
            kind = HAND_HIERARCHY.ONE_PAIR;
        }
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
    public int compareTo(final Hand h) {
        if (getKind().equals(h.getKind())) {
            for (int i = 0; i < cards.length(); i++) {
                if (cards.charAt(i) != h.cards.charAt(i)) {
                    return CARD_HIERARCHY.indexOf(cards.charAt(i)) - CARD_HIERARCHY.indexOf(h.cards.charAt(i));
                }
            }
        }
        return getKind().ordinal() - h.getKind().ordinal();
    }

    @Override
    public String toString() {
        return "Hand[" +
                "l='" + cards + '\'' +
                ", bid=" + bid +
                ", kind=" + kind +
                ", rank=" + rank +
                ']';
    }
}


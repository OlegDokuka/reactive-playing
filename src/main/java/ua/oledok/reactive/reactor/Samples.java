package ua.oledok.reactive.reactor;


import reactor.core.publisher.Flux;

public class Samples {
    private static final String[] STRINGS = {
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
    };

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        oneJust();
        twoJust();
        justList();
        correctFromList();
        fromRange();
        zipWordsWithRange();
        zipLettersWithRange();
        zipDistinctLettersWithRange();
        zipDistinctSortedLettersWithRange();
        zipReplacedDistinctSortedLettersWithRange();
    }

    private static void zipDistinctSortedLettersWithRange() {

    }

    private static void zipDistinctLettersWithRange() {

    }

    private static void zipLettersWithRange() {

    }

    private static void zipWordsWithRange() {

    }

    private static void fromRange() {

    }

    private static void correctFromList() {

    }

    private static void justList() {

    }

    private static void twoJust() {
        Flux
                .just("Hello", "World")
                .log()
                .subscribe();
    }

    private static void zipReplacedDistinctSortedLettersWithRange() {
    }

    private static void oneJust() {
        Flux
                .just("Howdy!")
                .log()
                .subscribe();
    }
}

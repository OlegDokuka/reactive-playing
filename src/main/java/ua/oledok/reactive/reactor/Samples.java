package ua.oledok.reactive.reactor;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

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
        printOn(Flux
                .fromIterable(Arrays.asList(STRINGS))
                .zipWith(Flux.range(0, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void fromRange() {
        printOn(Flux.range(0, 5));
    }

    private static void correctFromList() {
        printOn(Flux.fromIterable(Arrays.asList(STRINGS)));
    }

    private static void justList() {
        printOn(Flux.just(Arrays.asList(STRINGS)));
    }

    private static void twoJust() {
        printOn(Flux.just("Hello", "World"));
    }

    private static void zipReplacedDistinctSortedLettersWithRange() {
    }

    private static void oneJust() {
        printOn(Flux.just("Howdy!"));
    }

    private static void printOn(Flux<?> flux) {
        flux.subscribe(System.out::println);
    }

    private static void printOn(Mono<?> mono) {
        mono.subscribe(System.out::println);
    }

    private static String formatZipped(String w, Integer i) {
        return String.format("%2d. %s", i, w);
    }
}

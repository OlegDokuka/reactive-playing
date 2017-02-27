package ua.oledok.reactive.rxjava;


import io.reactivex.Observable;

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

    private static void zipReplacedDistinctSortedLettersWithRange() {
        printOn(Observable
                .fromIterable(Arrays.asList(STRINGS))
                .map(w -> w.equals("jumped") ? "jumps" : w)
                .flatMap(w -> Observable.fromArray(w.split("")))
                .distinct()
                .sorted()
                .zipWith(Observable.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void zipDistinctSortedLettersWithRange() {
        printOn(Observable
                .fromIterable(Arrays.asList(STRINGS))
                .flatMap(w -> Observable.fromArray(w.split("")))
                .distinct()
                .sorted()
                .zipWith(Observable.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void zipDistinctLettersWithRange() {
        printOn(Observable
                .fromIterable(Arrays.asList(STRINGS))
                .flatMap(w -> Observable.fromArray(w.split("")))
                .distinct()
                .zipWith(Observable.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void zipWordsWithRange() {
        printOn(Observable
                .fromIterable(Arrays.asList(STRINGS))
                .zipWith(Observable.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void zipLettersWithRange() {
        printOn(Observable
                .fromIterable(Arrays.asList(STRINGS))
                .flatMap(w -> Observable.fromArray(w.split("")))
                .zipWith(Observable.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static String formatZipped(String w, Integer i) {
        return String.format("%2d. %s", i, w);
    }

    private static void fromRange() {
        printOn(Observable.range(1, 5));
    }

    private static void correctFromList() {
        printOn(Observable.fromIterable(Arrays.asList(STRINGS)));
    }

    private static void justList() {
        printOn(Observable.just(Arrays.asList(STRINGS)));
    }

    private static void twoJust() {
        printOn(Observable.just("Hello", "World"));
    }

    private static void oneJust() {
        printOn(Observable.just("Howdy!"));
    }

    private static void printOn(Observable<?> observable) {
        observable.subscribe(System.out::println);
    }
}

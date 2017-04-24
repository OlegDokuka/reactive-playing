package ua.oledok.reactive.reactor;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

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
        shortCircuit();
        shortCircuitBlocking();
        firstEmitting();
    }

    private static void zipDistinctSortedLettersWithRange() {
        printOn(Flux
                .fromArray(STRINGS)
                .flatMap(w -> Flux.fromArray(w.split("")))
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void zipDistinctLettersWithRange() {
        printOn(Flux
                .fromArray(STRINGS)
                .flatMap(w -> Flux.fromArray(w.split("")))
                .distinct()
                .zipWith(Flux.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void zipLettersWithRange() {
        printOn(Flux
                .fromArray(STRINGS)
                .flatMap(w -> Flux.fromArray(w.split("")))
                .zipWith(Flux.range(1, Integer.MAX_VALUE), Samples::formatZipped));
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
        printOn(Flux
                .fromArray(STRINGS)
                .flatMap(w -> Flux.fromArray(w.split("")))
                .concatWith(Mono.just("s"))
                .distinct()
                .sort()
                .zipWith(Flux.range(1, Integer.MAX_VALUE), Samples::formatZipped));
    }

    private static void shortCircuit() {
        printOn(Mono
                .just("Hello")
                .concatWith(Mono.just("World!... with delay").delaySubscription(Duration.ofMillis(500))));
    }

    private static void shortCircuitBlocking() {
        printOn(Mono
                .just("Hello")
                .concatWith(Mono.just("World!... with delay").delaySubscription(Duration.ofMillis(500)))
                .toStream());
    }

    public static void firstEmitting() {
        Mono<String> a = Mono.just("oops I’m late")
                .delaySubscription(Duration.ofMillis(450));
        Flux<String> b = Flux.just("let’s get", "the party", "started")
                .delaySubscription(Duration.ofMillis(400));

        printOn(Flux.firstEmitting(a, b)
                .toStream());
    }

    private static void oneJust() {
        printOn(Flux.just("Howdy!"));
    }

    private static void printOn(Flux<?> flux) {
        flux.subscribe(System.out::println);
    }

    private static void printOn(Stream<?> stream) {
        stream.forEach(System.out::println);
    }

    private static String formatZipped(String w, Integer i) {
        return String.format("%2d. %s", i, w);
    }
}

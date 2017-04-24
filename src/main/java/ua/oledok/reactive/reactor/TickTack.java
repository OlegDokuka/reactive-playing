package ua.oledok.reactive.reactor;


import reactor.core.publisher.Flux;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;

public class TickTack {
    private static long start = System.currentTimeMillis();

    public static void main(String[] args) {
        clock().subscribe(tick -> System.out.println(new Date()));
        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Flux<Long> clock() {
        Flux<Long> fast = Flux.interval(Duration.ofMillis(1000));
        Flux<Long> slow = Flux.interval(Duration.ofMillis(3000));

        return Flux.merge(
                slow.filter(t -> isSlowTickTime()).doOnEach(w -> System.out.println("<SLOW>")),
                fast.filter(t -> !isSlowTickTime()).doOnEach(w -> System.out.println("<FAST>"))
        );
    }

    private static Boolean isSlowTickTime() {
        return (System.currentTimeMillis() - start) % 30_000 >= 15_000;
    }

    private static boolean isSlowTickDate() {
        return LocalDate.now().getDayOfWeek() == DayOfWeek.SATURDAY
                || LocalDate.now().getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}

package ua.oledok.reactive.rxjava;


import io.reactivex.Observable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    private static Observable<Long> clock() {
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);

        return Observable.merge(
                slow.filter(t -> isSlowTickTime()).doOnEach(w->System.out.println("<SLOW>")),
                fast.filter(t -> !isSlowTickTime()).doOnEach(w->System.out.println("<FAST>"))
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

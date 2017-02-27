package ua.oledok.reactive.rxjava;


import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class WordServiceTest {
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
    private WordService wordService = new WordServiceSupport();

    @Test
    public void shouldProcessWords() {
        TestObserver<String> testObserver = TestObserver.create();

        wordService
                .enumerate(Observable.fromArray(STRINGS))
                .subscribe(testObserver);

        testObserver
                .assertSubscribed()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(9)
                .assertValues(
                        " 1. the",
                        " 2. quick",
                        " 3. brown",
                        " 4. fox",
                        " 5. jumped",
                        " 6. over",
                        " 7. the",
                        " 8. lazy",
                        " 9. dog"
                );
    }

    @Test
    public void shouldExpectSimulatedError() {
        TestObserver<String> testObserver = TestObserver.create();

        wordService
                .enumerate(Observable.fromArray(STRINGS))
                .concatWith(Observable.error(new RuntimeException()))
                .subscribe(testObserver);

        testObserver
                .assertSubscribed()
                .assertError(RuntimeException.class)
                .assertNotComplete();
    }

    @Test
    public void shouldCompleteAfterAwaiting() {
        TestObserver<String> testObserver = TestObserver.create();

        wordService
                .enumerate(Observable.fromArray(STRINGS))
                .subscribeOn(Schedulers.computation())
                .subscribe(testObserver);

        testObserver
                .assertSubscribed()
                .awaitDone(2, TimeUnit.SECONDS)
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(9)
                .assertValues(
                        " 1. the",
                        " 2. quick",
                        " 3. brown",
                        " 4. fox",
                        " 5. jumped",
                        " 6. over",
                        " 7. the",
                        " 8. lazy",
                        " 9. dog"
                );
    }

    @Test
    public void shouldCompleteAfterAwaitingWithAwaitility() {
        TestObserver<String> testObserver = TestObserver.create();

        wordService
                .enumerate(Observable.fromArray(STRINGS))
                .subscribeOn(Schedulers.computation())
                .subscribe(testObserver);

        await()
                .timeout(2, TimeUnit.SECONDS)
                .until(testObserver::isTerminated);

        testObserver
                .assertSubscribed()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(9)
                .assertValues(
                        " 1. the",
                        " 2. quick",
                        " 3. brown",
                        " 4. fox",
                        " 5. jumped",
                        " 6. over",
                        " 7. the",
                        " 8. lazy",
                        " 9. dog"
                );
    }

    @Test
    public void shouldCompleteBlocking() {
        TestObserver<String> testObserver = TestObserver.create();

        wordService
                .enumerate(Observable.fromArray(STRINGS))
                .subscribeOn(Schedulers.computation())
                .blockingSubscribe(testObserver);

        testObserver
                .assertSubscribed()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(9)
                .assertValues(
                        " 1. the",
                        " 2. quick",
                        " 3. brown",
                        " 4. fox",
                        " 5. jumped",
                        " 6. over",
                        " 7. the",
                        " 8. lazy",
                        " 9. dog"
                );
    }

    @Test
    public void shouldCompleteAndReplaceComputationOnTrampoline() {
        TestObserver<String> testObserver = TestObserver.create();
        RxJavaPlugins.setComputationSchedulerHandler(old -> Schedulers.trampoline());

        try {
            wordService
                    .enumerate(Observable.fromArray(STRINGS))
                    .subscribeOn(Schedulers.computation())
                    .subscribe(testObserver);

            testObserver
                    .assertSubscribed()
                    .assertComplete()
                    .assertNoErrors()
                    .assertValueCount(9)
                    .assertValues(
                            " 1. the",
                            " 2. quick",
                            " 3. brown",
                            " 4. fox",
                            " 5. jumped",
                            " 6. over",
                            " 7. the",
                            " 8. lazy",
                            " 9. dog"
                    );
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void shouldCompleteWithSchedulerDebugging() {
        TestObserver<String> testObserver = TestObserver.create();
        TestScheduler testScheduler = new TestScheduler();

        wordService
                .enumerate(Observable.fromArray(STRINGS))
                .zipWith(Observable.interval(1, TimeUnit.SECONDS, testScheduler), (w, i) -> w)
                .subscribeOn(testScheduler)
                .subscribe(testObserver);

        testObserver
                .assertSubscribed()
                .assertNotComplete();

        testScheduler
                .advanceTimeBy(1, TimeUnit.SECONDS);

        testObserver
                .assertNotComplete()
                .assertValueCount(1)
                .assertValues(" 1. the");

        testScheduler
                .advanceTimeTo(9, TimeUnit.SECONDS);

        testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(9)
                .assertValues(
                        " 1. the",
                        " 2. quick",
                        " 3. brown",
                        " 4. fox",
                        " 5. jumped",
                        " 6. over",
                        " 7. the",
                        " 8. lazy",
                        " 9. dog"
                );
    }
}

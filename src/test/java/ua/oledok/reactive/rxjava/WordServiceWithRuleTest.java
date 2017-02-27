package ua.oledok.reactive.rxjava;


import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class WordServiceWithRuleTest {
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

    @Rule
    public final ImmediateSchedulersRule schedulers = new ImmediateSchedulersRule();

    private final WordService wordService = new WordServiceSupport();

    @Test
    public void shouldCompleteAndReplaceComputationOnTrampoline() {
        TestObserver<String> testObserver = TestObserver.create();

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
    }

    private static class ImmediateSchedulersRule implements TestRule {
        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    RxJavaPlugins.setIoSchedulerHandler(scheduler ->
                            Schedulers.trampoline());
                    RxJavaPlugins.setComputationSchedulerHandler(scheduler ->
                            Schedulers.trampoline());
                    RxJavaPlugins.setNewThreadSchedulerHandler(scheduler ->
                            Schedulers.trampoline());
                    try {
                        base.evaluate();
                    } finally {
                        RxJavaPlugins.reset();
                    }
                }
            };
        }
    }
}


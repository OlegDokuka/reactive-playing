package ua.oledok.reactive.rxjava;


import io.reactivex.Observable;

public class WordServiceSupport implements WordService {
    @Override
    public Observable<String> enumerate(Observable<String> words) {
        return words.zipWith(
                Observable.range(1, Integer.MAX_VALUE),
                (string, index) -> String.format("%2d. %s", index, string)
        );
    }
}

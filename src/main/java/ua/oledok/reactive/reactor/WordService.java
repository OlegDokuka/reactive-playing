package ua.oledok.reactive.reactor;


import io.reactivex.Observable;

public interface WordService {
    Observable<String> enumerate(Observable<String> words);
}

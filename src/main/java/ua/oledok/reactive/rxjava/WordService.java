package ua.oledok.reactive.rxjava;


import io.reactivex.Observable;

public interface WordService {
    Observable<String> enumerate(Observable<String> words);
}

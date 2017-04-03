package ua.oledok.reactive.gitter;


import org.reactivestreams.Publisher;
import ua.oledok.reactive.gitter.dto.Message;

public interface GitterClient {

    Publisher<Message> stream();
}

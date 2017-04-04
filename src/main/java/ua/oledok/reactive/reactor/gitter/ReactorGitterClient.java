package ua.oledok.reactive.reactor.gitter;

import io.netty.buffer.ByteBufHolder;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientResponse;
import ua.oledok.reactive.gitter.GitterClient;
import ua.oledok.reactive.gitter.dto.Message;
import ua.oledok.reactive.gitter.utils.MessageEncoder;


public class ReactorGitterClient implements GitterClient {
    private final ConnectableFlux<Message> connectableFlux;
    private final Disposable connector;

    public ReactorGitterClient(String roomId) {
        connectableFlux = Flux
                .<Message>create(e -> emmit(e, roomId), FluxSink.OverflowStrategy.BUFFER)
                .publish();
        connector = connectableFlux.connect();
    }

    @Override
    public Publisher<Message> stream() {
        return connectableFlux;
    }

    private void emmit(FluxSink<Message> emitter, String roomId) {
        HttpClient
                .create()
                .get("https://stream.gitter.im/v1/rooms/" + roomId + "/chatMessages",
                        (r) -> r.addHeader("Authorization", "Bearer 3cd4820adf59b6a7116f99d92f68a1b786895ce7"))
                .flatMap(HttpClientResponse::receiveContent)
                .map(ByteBufHolder::content)
                .filter(bb -> bb.capacity() > 2)
                .map(MessageEncoder::mapToMessage)
                .doOnNext(m -> System.out.println("Log Emit: " + m))
                .subscribe(emitter::next, emitter::error, emitter::complete);
    }

    @SneakyThrows
    public static void main(String[] args) {
        GitterClient gitterClient = new ReactorGitterClient("55e55b9f0fc9f982beaf4213");

        new Thread(() -> {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            gitterClient.stream()
                    .subscribe(new Subscriber<Message>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            System.out.println("Subscriber: Subscribed");
                            s.request(Long.MAX_VALUE);
                        }

                        @Override
                        public void onNext(Message message) {
                            System.out.println("Subscriber: [\n\r" + message + "\n\r");
                        }

                        @Override
                        public void onError(Throwable t) {
                            System.out.println("Subscriber: [\n\r" + t + "\n\r");
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("Subscriber: Complete");
                        }
                    });
        }).start();

        Thread.sleep(100000);
        System.exit(1);
    }
}

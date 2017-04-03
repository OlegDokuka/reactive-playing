package ua.oledok.reactive.rxjava.gitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import lombok.SneakyThrows;
import lombok.val;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import ua.oledok.reactive.gitter.GitterClient;
import ua.oledok.reactive.gitter.dto.Message;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.io.InputStream;

public final class RxGitterClient implements GitterClient {
    private final ConnectableFlowable<Message> connectableFlowable;
    private final Disposable connector;

    public RxGitterClient(String roomId) {
        connectableFlowable = Flowable
                .<Message>create(e -> emmit(e, roomId), BackpressureStrategy.BUFFER)
                .publish();
        connector = connectableFlowable.connect();
    }

    @SneakyThrows
    public static void main(String[] args) {
        GitterClient gitterClient = new RxGitterClient("55e55b9f0fc9f982beaf4213");

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

    private void emmit(FlowableEmitter<Message> emitter, String roomId) throws Exception {
        SSLContext sslCtx = SSLContext.getDefault();
        SSLEngine sslEngine = sslCtx.createSSLEngine("stream.gitter.im", 443);
        sslEngine.setUseClientMode(true);

        HttpClient
                .newClient("stream.gitter.im", 443)
                .secure(sslEngine)
                .createGet("/v1/rooms/" + roomId + "/chatMessages")
                .addHeader("Authorization", "Bearer 3cd4820adf59b6a7116f99d92f68a1b786895ce7")
                .flatMap(HttpClientResponse::getContent)
                .filter(bb -> bb.capacity() > 2)
                .map(this::mapToMessage)
                .doOnNext(m -> System.out.println("Log Emit: " + m))
                .subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
    }

    @SneakyThrows
    private Message mapToMessage(ByteBuf bb) {
        val buf = bb.nioBuffer();

        return new ObjectMapper().readValue(new InputStream() {

            public int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get() & 0xFF;
            }

            public int read(byte[] bytes, int off, int len)
                    throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }

                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        }, Message.class);
    }

    @Override
    public Publisher<Message> stream() {
        return connectableFlowable;
    }
}

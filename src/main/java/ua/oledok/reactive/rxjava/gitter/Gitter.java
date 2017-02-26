package ua.oledok.reactive.rxjava.gitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import lombok.SneakyThrows;
import lombok.val;
import ua.oledok.reactive.rxjava.gitter.domain.Message;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.IOException;
import java.io.InputStream;

public final class Gitter {
    private Gitter() {
    }

    @SneakyThrows
    public static void main(String[] args) {
        listen("55e55b9f0fc9f982beaf4213")
                .autoConnect()
                .subscribe(System.out::println);
        Thread.sleep(100000);
    }

    public static ConnectableFlowable<Message> listen(String roomId) {
        return Flowable
                .<Message>create(e -> emmit(e, roomId), BackpressureStrategy.BUFFER)
                .publish();
    }

    private static void emmit(FlowableEmitter<Message> emitter, String roomId) throws Exception {
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
                .map(Gitter::mapToMessage)
                .subscribe(emitter::onNext, emitter::onError, emitter::onComplete);
    }

    @SneakyThrows
    private static Message mapToMessage(ByteBuf bb) {
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
}

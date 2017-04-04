package ua.oledok.reactive.gitter.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import lombok.val;
import ua.oledok.reactive.gitter.dto.Message;

import java.io.IOException;
import java.io.InputStream;

public class MessageEncoder {

    @SneakyThrows
    public static Message mapToMessage(ByteBuf bb) {
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

package org.example.desktop.nio;

import unrefined.Lifecycle;
import unrefined.app.Log;
import unrefined.nio.channels.Pipe;
import unrefined.util.RunnableCallable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PipedMessage {

    public static void main(String[] args) throws IOException {
        Lifecycle.onMain(args);

        Pipe pipe = Pipe.open(); // Creates the pipe

        new Thread((RunnableCallable) () -> {
            Pipe.SinkChannel sink = pipe.sink(); // Gets the sink channel

            ByteBuffer buffer = ByteBuffer.allocate(512);
            buffer.clear();
            buffer.put("Hello World".getBytes());
            buffer.flip();

            // Write the data into the sink channel
            while (buffer.hasRemaining()) {
                sink.write(buffer);
            }
            sink.close();
        }).start();

        new Thread((RunnableCallable) () -> {
            Pipe.SourceChannel source = pipe.source(); // Gets the source channel

            ByteBuffer buffer1 = ByteBuffer.allocate(512);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // Retrieve the he data and write to the log
            while (source.read(buffer1) > 0) {
                buffer1.flip();

                while (buffer1.hasRemaining()) {
                    stream.write(buffer1.get());
                }
                buffer1.clear();
            }
            source.close();
            Log.defaultInstance().info("Unrefined NIO", "Message from pipe: " + stream);
        }).start();
    }

}

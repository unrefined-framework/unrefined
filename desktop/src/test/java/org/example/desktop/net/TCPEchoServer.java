package org.example.desktop.net;

import unrefined.Lifecycle;
import unrefined.Unrefined;
import unrefined.io.IOStreams;
import unrefined.net.ClientSocket;
import unrefined.net.InetSocketAddress;
import unrefined.net.Net;
import unrefined.net.ServerSocket;
import unrefined.nio.charset.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public class TCPEchoServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Lifecycle.onMain(args);
        Net net = Unrefined.net.net;
        InetSocketAddress remote = net.createInetSocketAddress("localhost", 23333);
        ServerSocket server = net.createServerSocket(remote);
        new Thread(() -> {
            try {
                ClientSocket client = server.accept();
                IOStreams.transfer(client.getInputStream(), client.getOutputStream());
                server.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).start();
        Thread.sleep(1000);
        ClientSocket client = net.createClientSocket(remote);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), Charsets.UTF_8));
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        out.println("Hello Unrefined Net");
        new Thread(() -> {
            try {
                Unrefined.app.log.info(client.getLocalInetAddress().getCanonicalHostName(), "Message from server: " + in.readLine());
                client.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).start();
    }

}

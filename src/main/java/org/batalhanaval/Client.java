package org.batalhanaval;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    private static final int serverPort = 12345;
    private static final String SERVER_ADDRESS = "192.168.10.26";

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS,serverPort);

            Thread readerThread = new Thread(new ClientReader(socket));
            Thread writerThread = new Thread(new ClientWriter(socket));

            readerThread.start();
            writerThread.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
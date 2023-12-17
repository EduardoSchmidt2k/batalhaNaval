package org.batalhanaval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientWriter implements Runnable {
    private Socket socket;

    public ClientWriter(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);

            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                serverOut.println(userMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

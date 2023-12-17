package org.batalhanaval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader implements Runnable {
    private final Socket socket;

    public ClientReader(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String serverResponse;
            while ((serverResponse = serverIn.readLine()) != null) {
                System.out.println(serverResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

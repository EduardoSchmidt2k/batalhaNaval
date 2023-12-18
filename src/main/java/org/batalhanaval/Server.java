package org.batalhanaval;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int port = 12345;
    private static final Map<String, BattleRoom> battleRooms = new HashMap<>();
    private static int playerCount = 0;
    private static final String ADDRESS = "192.xxx.xxx.xx";

    public static void main(String[] args) {
        try {
//            ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(ADDRESS));
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server is online");

            while(true){
                Socket client = server.accept();
                playerCount++;
                System.out.println("A player has been connected");

                PlayerHandler playerHandler = new PlayerHandler(client,battleRooms,playerCount);
                new Thread(playerHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

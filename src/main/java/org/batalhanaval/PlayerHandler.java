package org.batalhanaval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class PlayerHandler implements Runnable {

    private final Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    String roomId;
    private final Map<String, BattleRoom> battleRooms;
    private final String NO_ROOM = "no_room";
    String commands =
            "/join: Join a room (if the game is not started)\n" +
                    "/leave: Leave the current room (if the game is not started)\n" +
                    "/list: List available rooms (if the game is not started)\n" +
                    "/create: Create a new room (if the game is not started)\n" +
                    "/delete: Delete an existing room (if the game is not started)\n" +
                    "/placeShips: Place ships on the board (if the game is not started)\n" +
                    "/checkReady: Check if the player is ready (if the game is not started)\n" +
                    "/uncheckReady: Uncheck the player's readiness (if the game is not started)\n" +
                    "/startGame: Start the game (if the game is not started)\n" +
                    "/showBoard: Display the game board\n" +
                    "/fire: Perform a firing action\n" +
                    "/checkEnemyBoard: Check the enemy's game board\n" +
                    "/checkGameStatus: Check the status of the game";

    String playerCode;
    boolean isGameStarted = false;

    public PlayerHandler(Socket clientSocket, Map<String, BattleRoom> battleRooms, int playerCode) {
        this.clientSocket = clientSocket;
        this.roomId = NO_ROOM;
        this.battleRooms =  battleRooms;
        this.playerCode = "Player-"+playerCode;
    }



    @Override
    public void run(){
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            out.println("Welcome to the navel battle server");
            out.println("Here is the list of available commands: \n"+commands);
            out.println("______________________________________________________");

            String inputLine;
            while(true){
                inputLine = in.readLine();
                if(inputLine.equals("/exit") && !isGameStarted){
                    break;
                } else{
                    processInput(inputLine);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                leaveRoom();
                out.println("You're disconnected!");
                if(in != null) in.close();
                if(out != null) out.close();
                clientSocket.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void processInput(String input){
        if (input.startsWith("/join") && !isGameStarted){
            joinRoom();
        }
        else if(input.equals("/leave") && !isGameStarted){
            leaveRoom();
        }
        else if(input.equals("/list") && !isGameStarted){
            listRoom();
        }
        else if(input.startsWith("/create") && !isGameStarted){
            createRoom();
        }
        else if(input.startsWith("/delete") && !isGameStarted){
            deleteRoom();
        }
        else if(input.equals("/placeShips") && !isGameStarted){
            placeShips();
        }
        else if(input.equals("/checkReady") && !isGameStarted){
            checkReady();
        }
        else if(input.equals("/uncheckReady") && !isGameStarted){
            uncheckReady();
        }
        else if(input.equals("/startGame") && !isGameStarted){
            startGame();
        }
        else if(input.equals("/showBoard") && isGameStarted){
            showBoard();
        }
        else if (input.equals("/fire") && isGameStarted) {
            fire();
        }
        else if (input.equals("/checkEnemyBoard") && isGameStarted) {
            checkEnemyBoard();
        }
        else if (input.equals("/checkGameStatus") && isGameStarted) {
            checkGameStatus();
        }
        else if(input.startsWith("/")){
            out.println("command invalid, list of commands: \n"+commands);
        }
        else{
            sendMessage(input);
        }
    }

    private void checkGameStatus() {
        battleRooms.get(roomId).checkGameStatus(playerCode);
    }

    private void checkEnemyBoard() {
        out.println(battleRooms.get(roomId).checkEnemyBoard(playerCode));
    }

    private void fire(){
        out.println("Example of writing a position: 4,3");
        out.println("The first number is the row and the second the column");
        out.println("Where do you want to put your ship?");
        try {
            String position = in.readLine();
            while (!position.contains(",")){
                out.println("Command incorrect try again!");
                position = in.readLine();
            }
            String[] parts = position.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            battleRooms.get(roomId).fire(playerCode,row,col);
            if(battleRooms.get(roomId).checkWin(playerCode)){
                sendMessage("The player "+playerCode+" has won the game");
                sendMessage("The players are gonna be moving to the lobby!");
                battleRooms.get(roomId).resetGame(playerCode);
                battleRooms.remove(roomId);
                roomId = NO_ROOM;
                isGameStarted = false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startGame(){
        if (battleRooms.get(roomId).canStartGame()) {
            isGameStarted = true;
            sendMessage("The game has begun, get ready!");
        } else {
            out.println("The two players aren't ready yet.");
        }
    }

    private void placeShips(){
        int shipsLeft = 1;
        showBoard();
        out.println("Example of writing a position: 4,3 - the available numbers is 0~4,0~9");
        out.println("The first number is the row and the second the column");
        while (shipsLeft > 0){
            out.println("Where do you want to put your ship?");
            try {
                String position = in.readLine();
                while (!position.contains(",")){
                    out.println("Command incorrect try again!");
                    position = in.readLine();
                }
                String[] parts = position.split(",");
                if(battleRooms.get(roomId).placeShip(parts[0],parts[1],playerCode)){
                    out.println(battleRooms.get(roomId).showBoard(playerCode));
                    out.println("-----------------------------------------------------------");
                    shipsLeft--;
                }
                else{
                    out.println("Position unavailable!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        out.println("Your board is ready to go! write /checkReady and wait for your opponent!");
    }

    private void showBoard(){
        if(!roomId.equals(NO_ROOM)){
           out.println(battleRooms.get(roomId).showBoard(playerCode));
        }
    }

    private void checkReady(){
        boolean canStart = battleRooms.get(roomId).checkReady(playerCode);
        if(canStart){
            out.println("You're checked now, everyone is ready, write /startGame! to start the game!");
        }
        else{
            out.println("You're checked now. Waiting for the opponent get ready!");
        }
    }

    private void uncheckReady(){
        battleRooms.get(roomId).uncheckReady(playerCode);
        out.println("You're not checked anymore!");
    }

    private void createRoom(){
        out.println("Write the name of the room you want to create:");
        try {
            String input = in.readLine();
            battleRooms.put(input,new BattleRoom(input));
            out.println("The room has been created!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteRoom(){
        out.println("Write the name of the room you want to delete:");
        try {
            String input = in.readLine();
            if(battleRooms.containsKey(input)){
                battleRooms.remove(roomId);
                out.println("The room has been deleted!");
            }
            else{
                out.println("This room does not exist!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void listRoom(){
        out.println("List of rooms");
        battleRooms.forEach((roomId, battleRoom) -> out.println(roomId));
    }

    private void joinRoom(){
        out.println("Write the name of the room you want to join:");
        try {
            String input = in.readLine();
            if(battleRooms.containsKey(input)){
                leaveRoom();

                this.roomId = input;
                if(!battleRooms.get(input).isRoomFull()){
                    battleRooms.get(input).addPlayer(this,new Board(), playerCode);
                    out.println("You have entered in room: "+input);
                    out.println("Write /placeShips to start putting your ships in the board!");
                }
                else {
                    out.println("The room is full try another one!");
                }

            } else {
                out.println("The room "+input+ " doesn't exist.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void leaveRoom(){
        if(!roomId.equals(NO_ROOM)){
            battleRooms.get(roomId).removePlayer(this,playerCode);
            out.println("You have quit the room "+roomId);
            roomId = NO_ROOM;
        }
    }

    private void sendMessage(String message){
        if(!roomId.equals(NO_ROOM)){
            battleRooms.get(roomId).broadcastMessage(roomId+" - "+playerCode+": "+ message);
        }
        else {
            out.println("You're not in any room, use /join roomId to join in a room.");
        }
    }

}

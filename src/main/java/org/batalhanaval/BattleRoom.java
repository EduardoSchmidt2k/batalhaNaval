package org.batalhanaval;

import java.util.HashMap;
import java.util.Map;

public class BattleRoom {
    private String roomId;
    private final Map<String, PlayerHandler> players = new HashMap<>();
    private final Map<String,Board> boards = new HashMap<>();
    private final Map<String, PlayerStatus> playersStatus = new HashMap<>();
    private final String NO_ROOM = "no_room";

    public BattleRoom(String roomId){
        this.roomId = roomId;
    }

    public void addPlayer(PlayerHandler playerHandler,Board board,String playerCode){
        players.put(playerCode,playerHandler);
        boards.put(playerCode,board);
        playersStatus.put(playerCode,new PlayerStatus(true,false));
        boards.get(playerCode).initializeBoard();
    }

    public void removePlayer(PlayerHandler playerHandler,String playerCode){
        players.remove(playerHandler);
        playersStatus.remove(playerCode);
        boards.remove(playerCode);
    }

    public void broadcastMessage(String message){
        for(PlayerHandler playerHandler : players.values()){
            playerHandler.out.println(message);
        }
    }

    public String showBoard(String playerCode){
        return boards.get(playerCode).boardToString();
    }

    public boolean placeShip(String row, String col, String playerCode){
        return boards.get(playerCode).placeShip(Integer.parseInt(row),Integer.parseInt(col));
    }

    public void clearBoard(String playerCode){
        boards.get(playerCode).initializeBoard();
    }

    public boolean isRoomFull(){
        return players.size() == 2;
    }

    public void fire(String playerCode,int row, int col){
        if (playersStatus.get(playerCode).isMyTurn) {
            String result = boards.get(getEnemyCode(playerCode)).fire(row,col);
           if(result.equals("Hit")){
               playersStatus.get(getEnemyCode(playerCode)).shipsCount--;
               playersStatus.get(getEnemyCode(playerCode)).isMyTurn = true;
               playersStatus.get(playerCode).isMyTurn = false;
               players.get(playerCode).out.println(checkEnemyBoard(playerCode));
               players.get(getEnemyCode(playerCode)).out.println(showBoard(getEnemyCode(playerCode)));
               broadcastMessage("Player "+playerCode+" hit a ship!");
               players.get(getEnemyCode(playerCode)).out.println("It's your turn, type /fire to start shooting!");
           } else if (result.equals("Miss")) {
               playersStatus.get(getEnemyCode(playerCode)).isMyTurn = true;
               playersStatus.get(playerCode).isMyTurn = false;
               players.get(playerCode).out.println(checkEnemyBoard(playerCode));
               players.get(getEnemyCode(playerCode)).out.println(showBoard(getEnemyCode(playerCode)));
               broadcastMessage("Player "+playerCode+" missed a ship!");
               players.get(getEnemyCode(playerCode)).out.println("It's your turn, type /fire to start shooting!");
           } else{
                players.get(playerCode).out.println("Something is gone wrong, write again /fire!");
           }
        }
        else{
            players.get(playerCode).out.println("You have to wait the opponent fire!");
        }
    }

    public void resetGame(String playerCode){
        players.get(getEnemyCode(playerCode)).roomId = NO_ROOM;
        players.get(getEnemyCode(playerCode)).isGameStarted = false;
        players.clear();
        boards.clear();
        playersStatus.clear();
    }

    public boolean checkWin(String playerCode){
        if(playersStatus.get(getEnemyCode(playerCode)).shipsCount == 0){
            return true;
        }
        return false;
    }

    public boolean checkReady(String playerCode){
        playersStatus.get(playerCode).isReady = true;
        return canStartGame();
    }

    public void uncheckReady(String playerCode){
        playersStatus.get(playerCode).isReady = false;
    }

    public boolean canStartGame(){
        boolean everyoneIsReady = true;
        if(isRoomFull()){
            for (Map.Entry<String, PlayerStatus> entry : playersStatus.entrySet()) {
                PlayerStatus playerStatus = entry.getValue();
                if (!playerStatus.isReady) {
                    everyoneIsReady = false;
                    break;
                }
            }
        }
        else{
            everyoneIsReady = false;
        }
        return everyoneIsReady;
    }

    public void startGame(String playerCode){
        players.get(playerCode).isGameStarted = true;
        players.get(getEnemyCode(playerCode)).isGameStarted = true;
        playersStatus.get(getEnemyCode(playerCode)).isMyTurn = false;
        players.get(playerCode).out.println("It's your time, write /fire to take the enemy ship down!");
        players.get(getEnemyCode(playerCode)).out.println("It's the opponent turn, when he shot, you will see the message and the board, then its your turn!");
    }

    public String getEnemyCode(String playerCode){
        String enemyCode = "";

        for (Map.Entry<String, Board> entry : boards.entrySet()) {
            String code = entry.getKey();
            if (!code.equals(playerCode)) {
                enemyCode = code;
            }
        }

        return enemyCode;
    }

    public String checkEnemyBoard(String playerCode){
        return boards.get(getEnemyCode(playerCode)).checkEnemyBoard();
    }

    public String checkGameStatus(String playerCode){
        return "My ships left: "+playersStatus.get(playerCode).shipsCount+"\n" +
                "Enemy ships left: "+playersStatus.get(getEnemyCode(playerCode)).shipsCount;
    }

}

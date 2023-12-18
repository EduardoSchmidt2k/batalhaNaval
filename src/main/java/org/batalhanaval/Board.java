package org.batalhanaval;

import java.util.Arrays;

public class Board {
    char[][] board = new char[4][8];

    public void initializeBoard() {
        for (char[] chars : board) {
            Arrays.fill(chars, '.');
        }
    }

    public boolean placeShip(int row, int col){
        if(isPositionValid(row,col)){
            if(board[row][col] == '.'){
                board[row][col] = 'S';
                return true;
            }
        }
        return false;
    }

    public String fire(int row, int col){
        if(isPositionValid(row,col)){
            if(board[row][col] == 'S'){
                board[row][col] = 'X';
                return "Hit";
            }
            else if(board[row][col] == '.'){
                board[row][col] = 'O';
                return "Miss";
            }
        }
        return "Error";
    }

    public String boardToString() {
        StringBuilder sb = new StringBuilder();

        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell).append(' ');
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    public String checkEnemyBoard() {
        StringBuilder sb = new StringBuilder();

        for (char[] row : board) {
            for (char cell : row) {
                if(cell == 'S'){
                    sb.append('.').append(' ');
                }
                else{
                    sb.append(cell).append(' ');
                }
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    private boolean isPositionValid(int row, int column){
        return (row < 4 && row >= 0) && (column < 8 && column >= 0);
    }


}

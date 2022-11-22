package application;

import java.util.Arrays;

public class Game {

    int gameId;

    int player1;

    int player2;

    int turn; // 1 or 2

    int winner; // 0 (in progress), 1, 2 or 3 (for a tie)

    final int[][] chessBoard = new int[3][3];

    public Game(int gameId, int player1, int player2) {
        this.gameId = gameId;
        this.player1 = player1;
        this.player2 = player2;
        turn = 1;
        winner = 0;
    }

    // The player in turn set a chess at (x, y)
    public boolean setChess(int x, int y) {
        if (chessBoard[x][y] == 0) {
            chessBoard[x][y] = turn;
            turn = (turn + 1) % 2;
            return true;
        }

        return false;
    }

    // Determine whether the game can finish
    // after the previous player set chess at (x, y)
    public boolean canFinish(int x, int y) {
        int prev = (turn + 1) % 2;
        if (winner == 0) {
            // horizon
            if (chessBoard[x][y] == chessBoard[x][(y + 1) % 2] &&
                    chessBoard[x][y] == chessBoard[x][(y + 2) % 2]) {
                winner = prev;
                return true;
            }

            // vertical
            if (chessBoard[x][y] == chessBoard[(x + 1) % 2][y] &&
                    chessBoard[x][y] == chessBoard[(x + 2) % 2][y]) {
                winner = prev;
                return true;
            }

            // diagonal
            if (x == y || x == (y + 2) % 2) {
                if (chessBoard[x][y] == chessBoard[(x + 1) % 2][(y + 1) % 2] &&
                        chessBoard[x][y] == chessBoard[(x + 2) % 2][(y + 2) % 2]) {
                    winner = prev;
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public void printBoard() {
        System.out.println(Arrays.deepToString(chessBoard)
                .replace("], ", "\n")
                .replace("[", "")
                .replace("[[", "")
                .replace("]]", ""));
    }
}

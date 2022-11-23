package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    static int currentUser;

    static int playerIndex;

    static Game currentGame;

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        final int port = 8888;
        Socket s1 = null;
        BufferedReader br = null;
        Scanner in = null;
        BufferedReader is = null;
        PrintWriter os = null;

        try {
            s1 = new Socket(address, port);
            br = new BufferedReader(new InputStreamReader(System.in));
            in = new Scanner(System.in);
            is = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            os = new PrintWriter(s1.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("IO Exception");
        }

        assert s1 != null;
        assert is != null;
        assert os != null;

        System.out.println("Client Address : " + address);
        System.out.println("***Start Game***");

        String request = null;
        String response = null;
        try {
            // register and login
            while (true) {
                System.out.println("Are you a new user (1: yes, 2: no)");
                int num = in.nextInt();
                if (num == 1) {
                    System.out.println("Please register!");
                    System.out.println("Please input password: ");
                    String password = in.next();
                    request = String.format("register/%s", password);
                    response = sendRequest(request, os, is);
                    System.out.println("Registered userId : " + response);
                }

                System.out.println("Please log in");
                System.out.println("Please input userId: ");
                int userId = in.nextInt();
                System.out.println("Please input password: ");
                String password = in.next();
                request = String.format("logIn/%d/%s", userId, password);
                response = sendRequest(request, os, is);
                System.out.println(response);
                if (response.equals("true")) {
                    currentUser = userId;
                    System.out.println("Login successfully");
                    break;
                } else {
                    System.out.println("User dose not exist or wrong password");
                    System.out.println("Please try again");
                }
            }

            while (true) {
                System.out.println("To start game, you can:");
                System.out.println("1: Create new game, 2: Search joinable games");
                int num = in.nextInt();
                if (num == 1) {
                    request = String.format("createGame/%d", currentUser);
                    response = sendRequest(request, os, is);
                    System.out.println("You created game with id: " + response);
                    currentGame = new Game(Integer.parseInt(response), currentUser, 0);
                    playerIndex = 1;
                    break;
                } else {
                    request = String.format("getJoinableGames/%d", currentUser);
                    response = sendRequest(request, os, is);
                    System.out.println("All the joinable games");
                    System.out.println(response);
                    if (!response.equals("[]")) {
                        System.out.println("Choose the game you want to join");
                        int gameId = in.nextInt();
                        request = String.format("joinAndStartGame/%d/%d", currentUser, gameId);
                        response = sendRequest(request, os, is);
                        if (!response.equals("0")) {
                            System.out.println("You joined game with id: " + gameId);
                            currentGame = new Game(gameId, Integer.parseInt(response), currentUser);
                            currentGame.winner = 0;
                            playerIndex = 2;
                            break;
                        }
                        System.out.println("Join failed");
                    } else {
                        System.out.println("No joinable games");
                    }
                }
            }


            // Connect to a game
            System.out.println("Current User: " + currentUser);
            System.out.println("Current Game: " + currentGame);
            System.out.println("Game Start: ");
            currentGame.printBoard();

            while (currentGame.winner == 0) {
                int x;
                int y;
                if (playerIndex == currentGame.turn) {
                    System.out.println("Please input position to set cheese");
                    x = in.nextInt();
                    y = in.nextInt();
                    request = String.format("setChess/%d/%d/%d", currentGame.gameId, x, y);
                    response = sendRequest(request, os, is);
                } else {
                    request = String.format("getOpponentStep/%d", currentGame.gameId);
                    response = sendRequest(request, os, is);
                    x = response.charAt(1) - '0';
                    y = response.charAt(4) - '0';
                    System.out.println("Another player is considering");
                }

                currentGame.setChess(x, y);
                currentGame.printBoard();
                if (currentGame.canFinish(x, y)) {
                    currentGame.winner = (currentGame.turn + 1) % 2;
                    System.out.println("Game Finish, player" + currentGame.winner + "win");
                }
            }

            while (true) {
                Thread.sleep(10);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket read Error");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            request = String.format("logOut/%d", currentUser);
            response = sendRequest(request, os, is);
            currentUser = 0;
            System.out.println("Log out user");

            is.close();
            os.close();
            br.close();
            s1.close();
            System.out.println("Connection Closed");
        }

    }

    public static String sendRequest(String request,
                                     PrintWriter os,
                                     BufferedReader is) throws IOException {
        os.println(request);
        os.flush();
        return is.readLine();
    }
}

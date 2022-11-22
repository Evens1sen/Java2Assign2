package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;


public class Server {

    static Map<Integer, User> users = new HashMap<>();
    static List<Integer> onlineUsers = new ArrayList<>();
    static Map<Integer, Game> games = new HashMap<>();

    // register/password
    public static int register(String password) {
        int baseId = 0;
        int userId = baseId + users.size() + 1;
        users.put(userId, new User(userId, password));
        return userId;
    }

    // logIn/userId/password
    public static boolean logIn(int userId, String password) {
        User user = users.get(userId);
        if (user == null) {
            return false;
        }

        if (Objects.equals(password, user.password)) {
            onlineUsers.add(userId);
            return true;
        }

        return false;
    }

    // logOut/userId
    public static void logOut(int userId) {
        onlineUsers.remove((Integer) userId);
    }

    // createGame/userId
    public static int createGame(int userId) {
        int gameId = 100 + games.size() + 1;
        games.put(gameId, new Game(gameId, userId, 0));
        return gameId;
    }

    // getJoinableGames/userId
    public static List<Integer> getJoinableGames(int userId) {
        return games.values()
                .stream()
                .filter(game -> game.winner == -1 && game.player1 != userId && game.player2 != userId)
                .map(Game::getGameId)
                .collect(Collectors.toList());
    }

    // joinAndStartGame/userId/gameId
    public static int joinAndStartGame(int userId, int gameId) {
        Game toJoin = games.get(gameId);
        if (toJoin == null) {
            return 0;
        }

        toJoin.player2 = userId;
        toJoin.winner = 0;
        return toJoin.player1;
    }

    // startGame/user1/user2
    public static int startGame(int user1, int user2) {
        int gameId = 1000 + games.size() + 1;
        games.put(gameId, new Game(gameId, user1, user2));
        return gameId;
    }

    // makeMove/gameId/player/x/y
    public static boolean makeMove(int gameId, int player, int x, int y) {
        return false;
    }

    public static void main(String[] args) {

        Socket socket = null;
        ServerSocket serverSocket = null;
        final int PORT_NUM = 8888;
        System.out.println("Server Listening......");

        try {
            serverSocket = new ServerSocket(PORT_NUM);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server error");
        }

        while (true) {
            try {
                assert serverSocket != null;
                socket = serverSocket.accept();
                System.out.println("connection Established");
                ServerThread st = new ServerThread(socket);
                st.start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Connection Error");
            }
        }

    }

    static class ServerThread extends Thread {

        String request = null;
        BufferedReader is = null;
        PrintWriter os = null;
        Socket socket = null;

        public ServerThread(Socket s) {
            this.socket = s;
        }

        public String requestHandler(String request) {
            String response = null;
            String[] requestFields = request.split("/");
            String methodName = requestFields[0];

            if (Objects.equals(methodName, "register")) {
                String password = requestFields[1];
                int userId = register(password);
                response = String.valueOf(userId);
            } else if (Objects.equals(methodName, "logIn")) {
                int userId = Integer.parseInt(requestFields[1]);
                String password = requestFields[2];
                boolean result = logIn(userId, password);
                response = String.valueOf(result);
            } else if (Objects.equals(methodName, "logOut")) {
                int userId = Integer.parseInt(requestFields[1]);
                logOut(userId);
            } else if (Objects.equals(methodName, "createGame")) {
                int userId = Integer.parseInt(requestFields[1]);
                response = String.valueOf(createGame(userId));
            } else if (Objects.equals(methodName, "getJoinableGames")) {
                int userId = Integer.parseInt(requestFields[1]);
                response = getJoinableGames(userId).toString();
            } else if (Objects.equals(methodName, "joinAndStartGame")) {
                int userId = Integer.parseInt(requestFields[1]);
                int gameId = Integer.parseInt(requestFields[2]);
                response = String.valueOf(joinAndStartGame(userId, gameId));
            }

            return response;
        }

        public void run() {
            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("IO error in server thread");
            }

            try {
                request = is.readLine();
                while (true) {
                    String response = requestHandler(request);
                    os.println(response);
                    os.flush();
                    System.out.println("Response to Client  :  " + response);
                    request = is.readLine();
                }
            } catch (IOException e) {
                request = this.getName(); //reused String line for getting thread name
                System.out.println("IO Error/ Client " + request + " terminated abruptly");
            } catch (NullPointerException e) {
                request = this.getName(); //reused String line for getting thread name
                System.out.println("Client " + request + " Closed");
            } finally {
                try {
                    System.out.println("Connection Closing..");
                    if (is != null) {
                        is.close();
                        System.out.println(" Socket Input Stream Closed");
                    }

                    if (os != null) {
                        os.close();
                        System.out.println("Socket Out Closed");
                    }
                    if (socket != null) {
                        socket.close();
                        System.out.println("Socket Closed");
                    }

                } catch (IOException ie) {
                    System.out.println("Socket Close Error");
                }
            }
        }
    }

}



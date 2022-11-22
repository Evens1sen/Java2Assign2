package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server {

    static Map<Integer, User> users = new HashMap<>();
    static List<Integer> freeUsers = new ArrayList<>();
    static List<Integer> matchedUsers = new ArrayList<>();
    static Map<Integer, Game> games = new HashMap<>();

    // register/password
    public static int register(String password) {
        int baseId = 1000000;
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
            freeUsers.add(userId);
            return true;
        }

        return false;
    }

    // getFreeUsers/userId
    public static List<Integer> getFreeUsers(int userId) {
        List<Integer> res = new ArrayList<>(freeUsers);
        res.remove(userId);
        return res;
    }

    // matchGame/userId
    public static Game matchGame(int userId) {
//        if (freeUsers.size() < 2) {
//            return null;
//        }
//
//        if (freeUsers.size() == 2) {
//            return freeUsers.remove(0);
//        }
//
//        return freeUsers.remove(0);
        return null;
    }

    // matchPlayer/userId
    public static int matchPlayer(int userId) {
        if (freeUsers.size() < 2) {
            return 0;
        }

        if (freeUsers.size() == 2) {
            return freeUsers.remove(0);
        }

        return freeUsers.remove(0);
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
            } else if (Objects.equals(methodName, "getFreeUsers")) {
                int userId = Integer.parseInt(requestFields[1]);
                response = getFreeUsers(userId).toString();
            } else if (Objects.equals(methodName, "matchPlayer")) {
                int userId = Integer.parseInt(requestFields[1]);
                int result = matchPlayer(userId);
                response = String.valueOf(result);
            } else if (Objects.equals(methodName, "startGame")) {
                int player1 = Integer.parseInt(requestFields[1]);
                int player2 = Integer.parseInt(requestFields[2]);
                int gameId = startGame(player1, player2);
                response = String.valueOf(gameId);
            } else if (Objects.equals(methodName, "makeMove")) {
                return null;
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



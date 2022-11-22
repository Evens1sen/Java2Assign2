package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    static int currentUser;

    static Game currentGame;

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        final int PORT_NUM = 8888;
        Socket s1 = null;
        BufferedReader br = null;
        Scanner in = null;
        BufferedReader is = null;
        PrintWriter os = null;

        try {
            s1 = new Socket(address, PORT_NUM);
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
//        System.out.println("Enter Data to echo Server (Enter QUIT to end):");

        String request = null;
        String response = null;
        try {
            // register and login
            while (true) {
                System.out.println("Are you a new user (1: yes, 2: no)");
                int inputNum = in.nextInt();
                if (inputNum == 1) {
                    System.out.println("Please register!");
                    System.out.println("Please input password: ");
                    String password = in.next();
                    request = String.format("register/%s", password);
                    response = sendRequest(request, os, is);
                    System.out.println("Registered userId : " + response);
                    System.out.println("Please log in");
                }

                System.out.println("Please log in！");
                System.out.println("Please input userId: ");
                int userId = in.nextInt();
                System.out.println("Please input password: ");
                String password = in.next();
                request = String.format("logIn/%d/%s", userId, password);
                response = sendRequest(request, os, is);
                System.out.println(response);
                if (response.equals("true")) {
                    currentUser = userId;
                    System.out.println("Login successful");
                    break;
                } else {
                    System.out.println("User dose not exist or wrong password");
                    System.out.println("Please try again");
                }
            }

            request = String.format("getFreeUsers/%d", currentUser);
            response = sendRequest(request, os, is);
            if (response.equals("[]")) {
                System.out.println("Waiting for free player");
            } else {
                System.out.println("Choose the users you want to play with");
                System.out.println(response);
                int player2 = in.nextInt();
            }


            // Match player and start a game
//            request = String.format("matchPlayer/%d", currentUser);
//            response = sendRequest(request, os, is);
//            System.out.println("Waiting for another player");
//            while (Objects.equals(response, "0")) {
//                response = sendRequest(request, os, is);
//            }
//            int player2 = Integer.parseInt(response);
//            System.out.println("Matched successful");
//
//            request = String.format("startGame/%d/%d", currentUser, player2);
//            response = sendRequest(request, os, is);
//            while (Objects.equals(response, "0")) {
//                response = sendRequest(request, os, is);
//            }
//            currentGame = new Game(Integer.parseInt(response), currentUser, player2);
//            System.out.println("Current User: " + currentUser);
//            System.out.println("Current Game: " + currentGame.gameId);


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket read Error");
        } finally {
            is.close();
            os.close();
            br.close();
            s1.close();
            System.out.println("Connection Closed");
        }

    }

    public static String sendRequest(String request, PrintWriter os, BufferedReader is) throws IOException {
        os.println(request);
        os.flush();
        return is.readLine();
    }
}

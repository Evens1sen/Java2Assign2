package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
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

        String line = null;
        BufferedReader is = null;
        PrintWriter os = null;
        Socket socket = null;

        public ServerThread(Socket s) {
            this.socket = s;
        }

        public void run() {
            try {
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                os = new PrintWriter(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("IO error in server thread");
            }

            try {
                line = is.readLine();
                while (line.compareTo("QUIT") != 0) {
                    os.println(line);
                    os.flush();
                    System.out.println("Response to Client  :  " + line);
                    line = is.readLine();
                }
            } catch (IOException e) {
                line = this.getName(); //reused String line for getting thread name
                System.out.println("IO Error/ Client " + line + " terminated abruptly");
            } catch (NullPointerException e) {
                line = this.getName(); //reused String line for getting thread name
                System.out.println("Client " + line + " Closed");
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



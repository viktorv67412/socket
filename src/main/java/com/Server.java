package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Callable<Boolean> {

    private Socket socket;
    private Boolean isConnectionAvailable;

    public Server(Socket socket) {
        this.socket = socket;
    }

    public Server() {
    }

    public Boolean call() throws Exception {

        try (PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            isConnectionAvailable = socket.isConnected();

            writer.println("Hello !".toUpperCase());
            writer.flush();

            while (isConnectionAvailable) {

                String msgFromClient = reader.readLine();
                System.out.println(msgFromClient);

                String msg = consoleReader.readLine();
                writer.println(msg.toUpperCase());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnectionAvailable;
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080);
        ExecutorService service = Executors.newFixedThreadPool(3);

        while (true) {
            service.submit(new Server(serverSocket.accept()));
        }
    }
}

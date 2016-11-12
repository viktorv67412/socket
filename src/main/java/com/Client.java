package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client implements Callable<Boolean> {

    private Socket socket;
    private Boolean isConnectionAvailable;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Client() {
    }

    public Boolean call() {

        try (PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            isConnectionAvailable = socket.isConnected();

            System.out.println(reader.readLine());

            while (isConnectionAvailable) {

                String msg = consoleReader.readLine();

                if (!(msg.equals("exit"))) {
                    writer.println(msg);
                    writer.flush();
                } else {
                    writer.println("Good bye");
                    writer.flush();
                    isConnectionAvailable = false;
                }

                String msgFromServer = reader.readLine();
                System.out.println(msgFromServer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnectionAvailable;
    }

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(InetAddress.getLocalHost(), 8080);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Client(socket));

    }
}

package it.appmessaggi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ChatServer {
    private Scanner scanner = new Scanner(System.in);
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());
    private int PORT = 0;

    public ChatServer(){
        
        int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "5000"));

        System.out.println("Server avviato sulla porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                System.out.println(
                    "Nuovo utente connesso: " +
                    clientSocket.getRemoteSocketAddress()
                );

                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
    
    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                clientWriters.add(out);

                username = in.readLine();
                broadcast(username + " si è unito alla chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    }
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Connessione interrotta con " + username);
            } finally {
                if (out != null) clientWriters.remove(out);
                if (username != null) broadcast(username + " ha lasciato la chat.");
                try {
                    socket.close(); 
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            
        }
    }
}
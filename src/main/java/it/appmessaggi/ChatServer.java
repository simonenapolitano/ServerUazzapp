package it.appmessaggi;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class ChatServer extends WebSocketServer {

    private static Set<WebSocket> clients = new HashSet<>();

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("Client connesso");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        for (WebSocket client : clients) {
            client.send(message);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server WebSocket avviato");
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("PORT"));
        new ChatServer(port).start();
    }
}
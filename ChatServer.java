import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Map<String, PrintWriter> clientMap = new ConcurrentHashMap<>();
    private static final List<String> chatHistory = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Ask the client for their username
                out.println("Enter your username:");
                username = in.readLine();
                System.out.println(username + " has joined the chat.");

                // Add the client to the map of connected clients
                clientMap.put(username, out);

                // Send chat history to the new client
                for (String message : chatHistory) {
                    out.println(message);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equals("exit")) {
                        // Handle the user exit here
                        System.out.println(username + " has left the chat.");
                        broadcast(username + " has left the chat.");
                        break; // Exit the loop
                    } else {
                         chatHistory.add(username + ": " + message);
                         broadcast(username + ": " + message);
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clientMap.remove(username);
                System.out.println(username + " has left the chat.");
                broadcast(username + " has left the chat.");
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientMap.values()) {
                writer.println(message);
            }
        }
    }
}

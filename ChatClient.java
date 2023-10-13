import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));

            String username = in.readLine();
            System.out.println("Server: " + username);

            Thread messageReceiver = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    // Handle the socket closure gracefully
                    System.out.println("Server has closed the connection.");
                }
            });
            messageReceiver.start();

            String input;

            while (true) {
                input = consoleIn.readLine();

                if (input != null) {
                    if (input.equalsIgnoreCase("exit")) {
                        out.println("exit");
                        socket.close(); // Close the socket when exiting
                        break;
                    } else {
                        out.println(input);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

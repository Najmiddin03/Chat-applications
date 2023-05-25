import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private final String username;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void sendMessage() {
        try {
            out.write(username);
            out.newLine();
            out.flush();

            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = sc.nextLine();
                out.write(username + ": " + message);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGr;
                while (socket.isConnected()) {
                    try {
                        messageFromGr = in.readLine();
                        System.out.println(messageFromGr);
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                    }
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username for the group chat: ");
        String username = sc.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}

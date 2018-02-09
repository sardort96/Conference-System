import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private static int PORT = 9001;

    private static HashMap<String, PrintWriter> names;

    public static void main(String[] args) throws Exception {
        names = new HashMap<String, PrintWriter>();
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    System.out.println(name);
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (!names.containsKey(name)) {
                            System.out.println(name+" connected");
                            names.put(name, out);
                            break;
                        }
                    }
                }

                out.println("NAMEACCEPTED");
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    String[] tokens = input.split("@");

                    names.get(tokens[1]).println("MESSAGE " + input);

                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (name != null) {
                    names.remove(name);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}


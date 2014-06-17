package nl.avans.aalphoog.os3.server;

import nl.avans.aalphoog.os3.BasicByteStream;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * This class provides a main entry point to this application and an easy interface that can start a
 * multi-threaded server with support for multiple clients based on sockets and byte communication.
 */
public class App {

    /** Default port for this protocol. */
    public static final int DEFAULT_PORT = 3305;

    /** Port that is connected to. */
    private int port;

    /**
     * Creates a new App with the given port. The server can than be started with the run() method.
     *
     * @param port Port to use.
     */
    private App(int port) {
        this.port = port; // todo logic.
    }

    /**
     * Runs the server on the already provided port in an infinite loop. This can only be exited by
     * exiting on the command line. This will also start a listener that accepts new connections and
     * creates a new thread for every connected client.
     *
     * @throws IOException When the server couldn't be created.
     */
    private void run() throws IOException {
        ServerSocket socket = ServerSocketFactory.getDefault().createServerSocket(this.port);
        System.out.printf("Server running on port %d%n", this.port);

        while (true) {
            try {
                new Thread(new ClientConnection(new BasicByteStream(socket.accept()))).start();
            } catch (IOException e) {
                System.err.printf("Issue with accepting a client, reason: %s%n", e.getMessage());
            }
        }
    }

    /**
     * Main entry point for this application that can optionally receive a port number and assign it.
     * When no port number is provided, the default port will be used. When the server crashes the
     * server exits.
     *
     * @param args Arguments, only one possible: a port number.
     */
    public static void main(String[] args) {
        try {
            int port = App.DEFAULT_PORT;

            if (args != null && args.length > 0) {
                port = Integer.parseInt(args[0]);
            }

            new App(port).run();
        } catch (IOException e) {
            System.err.printf("The server has crashed, reason: %s%n", e.getMessage());
            System.exit(0);
        }
    }
}

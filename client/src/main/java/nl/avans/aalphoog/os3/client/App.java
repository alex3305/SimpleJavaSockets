package nl.avans.aalphoog.os3.client;

import nl.avans.aalphoog.os3.BasicByteStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * This class provides a main entry point to this application and an easy interface that can start a
 * command line based client that can communicate with a server based on the same protocol.
 */
public class App {

    /** Default host as a server. */
    public static final String DEFAULT_HOST = "localhost";

    /** Default port for this protocol. */
    public static final int DEFAULT_PORT = 3305;

    /** When debug is true, a localhost:3305 connection will be established. */
    private static final boolean DEBUG = true;

    /** Host that is connected to. */
    private String host;

    /** Port that is connected to. */
    private int port;

    /**
     * Creates a new App with the given host and the default port. The client can be ran with the
     * run() method.
     *
     * @param host Host to connect to.
     */
    private App(String host) {
        new App(host, App.DEFAULT_PORT);
    }

    /**
     * Creates a new App with the given host and the given port. The client can be ran with the run()
     * method.
     *
     * @param host Host to connect to.
     * @param port Port to connect to. Although a default port is encouraged.
     */
    private App(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Runs the client that connects to the already provided host and optionally a port. This
     * application can quit with the QUIT or EXIT input from the command line.
     *
     * @throws IOException When the input couldn't be read or when the connection is closed.
     */
    private void run() throws IOException {
        System.out.printf("Connecting to remote server %s:%s.%n", this.host, this.port);

        BufferedReader standardIn = new BufferedReader(new InputStreamReader(System.in));
        Socket server = new Socket(this.host, this.port);
        final BasicByteStream byteStream = new BasicByteStream(server);

        new Thread(new ServerConnection(byteStream)).start();

        String input = "";
        while ((input = standardIn.readLine()) != null) {
            if (input.equalsIgnoreCase("QUIT") || input.equalsIgnoreCase("EXIT")) {
                byteStream.close();
                System.exit(0);
            }

            byteStream.sendBytes(BasicByteStream.stringToBytes(input));
        }
    }

    /**
     * Main entry point for this application that must receive a host (except in debug mode) and
     * optionally a port number. When no or invalid arguments a provided, a usage screen will be
     * shown and the client will exit.
     *
     * @param args Arguments with a host and optionally a port.
     */
    public static void main(String[] args) {
        try {
            if (App.DEBUG) {
                new App(App.DEFAULT_HOST).run();
                return;
            }

            if (args != null && args.length > 0) {
                switch (args.length) {
                    case 1: new App(args[0]).run(); break;
                    case 2: new App(args[0], Integer.parseInt(args[1])).run();
                    default: break;
                }
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.err.println("Usage: java -jar Client.jar <host> (<port>)");
            System.exit(0);
        }
    }
}

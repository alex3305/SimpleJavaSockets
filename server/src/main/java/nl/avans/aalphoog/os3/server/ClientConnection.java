package nl.avans.aalphoog.os3.server;

import nl.avans.aalphoog.os3.BasicByteStream;

import java.io.IOException;

/**
 * This class provides a unified interface to connect to multiple clients. It is also constructed with
 * a provided BasicByteStream, which should be created for each connection and thus client that connects
 * to this server. This class also implements a Runnable, and should be ran inside a separate thread.
 */
public class ClientConnection implements Runnable {

    /** Byte stream for communication. */
    private final BasicByteStream byteStream;

    /**
     * Creates a new ClientConnection with the given BasicByteStream which already holds a Socket
     * and thus an open connection. The BasicByteStream class also provides a simple interface for
     * communication with a client.
     *
     * @param byteStream Connection with streams to use.
     */
    ClientConnection(BasicByteStream byteStream) {
        this.byteStream = byteStream;
    }

    @Override
    public void run() {
        byte[] input;

        try {
            while ((input = this.byteStream.read()) != null) {
                System.out.println(BasicByteStream.bytesToString(input));
                // TODO Stub. Handle response properly.
                this.byteStream.sendBytes("Got message!".getBytes("UTF-8"));
            }
        } catch (IOException e) {
            System.err.println("Error when communicating.");
        }
    }
}

package nl.avans.aalphoog.os3.client;

import nl.avans.aalphoog.os3.BasicByteStream;

import java.io.IOException;

/**
 * This class provides a connection a server. It is also constructed with a BasicByteStream which
 * holds both an input and output stream and thus can perform the communication that is needed. This
 * class also implements a Runnable, and should be ran inside a separate thread.
 */
public class ServerConnection implements Runnable {

    /** Byte stream for communication. */
    private final BasicByteStream byteStream;

    /**
     * Creates a new ServerConnection with the given BasicByteStream which already holds a Socket
     * and thus an open connection. The BasicByteStream class also provides a simple interface for
     * communication with the server.
     *
     * @param byteStream Connection with streams to use.
     */
    public ServerConnection(BasicByteStream byteStream) {
        this.byteStream = byteStream;
    }

    @Override
    public void run() {
        byte[] input;

        try {
            while ((input = this.byteStream.read()) != null) {
                System.out.println(BasicByteStream.bytesToString(input));
            }
        } catch (IOException e) {
            System.err.println("Error when communicating.");
        }
    }
}

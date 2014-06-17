package nl.avans.aalphoog.os3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

/**
 * This class provides an interface and uses both an input and output stream in which data is written
 * in bytes as a byte-array. The data can easily be converted from and to human readable text by
 * calling the static methods bytesToString() and stringToBytes().
 *
 * Both the streams and the socket can be closed using the generally available close() method. After
 * closing, methods in this class will throw an IOException.
 */
public class BasicByteStream {

    /** Input stream for receiving data. */
    private DataInputStream inputStream;

    /** Output stream for sending data. */
    private DataOutputStream outputStream;

    /** Generally used connection. */
    private Socket socket;

    /**
     * Creates a new BasicByteStream with a given Socket.
     *
     * @param socket Connection to use.
     */
    public BasicByteStream(Socket socket) {
        this.socket = socket;
    }

    /**
     * Closes the connection after which every method in this class will throw an IOException.
     *
     * @throws IOException When the socket already is closed.
     */
    public void close() throws IOException {
        if (!this.socket.isClosed()) {
            this.inputStream.close();
            this.outputStream.close();
            this.socket.close();
        }
    }

    /**
     * Reads the input from the Socket's input stream. This method will also check if the stream(s)
     * are available and assign them when this is necessary.
     *
     * @return Byte array with the read data.
     * @throws IOException When the stream couldn't be created or when the input stream couldn't be read.
     */
    public byte[] read() throws IOException {
        this.checkAndCreateStreams();

        int length = this.inputStream.readInt();
        byte[] buffer = new byte[length];
        int bytesRead = 0;

        while (length > bytesRead) {
            bytesRead += this.inputStream.read(buffer, bytesRead, length - bytesRead);
        }

        return buffer;
    }

    /**
     * Writes a complete byte array to the output stream, thus enabling communication. This method will
     * call sendBytes(bytes, 0, bytes.length) and has the same error check.
     *
     * @param bytes Bytes to be send.
     * @throws IOException When the stream couldn't be created or when it was impossible to write to
     *                     the stream.
     */
    public void sendBytes(byte[] bytes) throws IOException {
        this.sendBytes(bytes, 0, bytes.length);
    }

    /**
     * Writes the bytes to the output stream and sends them through the buffer, enabling communication.
     * This method will also check whether the stream(s) are available and assign them when necessary.
     *
     * This method will check whether the assigned variables are valid, but when they are not valid an
     * appropriate exception will be thrown.
     *
     * @param bytes  Bytes to be send.
     * @param start  Start of the array to send.
     * @param length Total number of bytes to send.
     * @throws IOException               When the stream couldn't be created or when it was impossible
     *                                   to write to the stream.
     * @throws IllegalArgumentException  When one of the arguments has an invalid value.
     * @throws IndexOutOfBoundsException When the start argument has a value smaller than zero or when
     *                                   it is larger than the length of bytes.
     */
    public void sendBytes(byte[] bytes, int start, final int length)
            throws IOException, IllegalArgumentException, IndexOutOfBoundsException {
        if (length <= 0) {
            throw new IllegalArgumentException("Negative length not allowed.");
        }

        if (bytes == null || bytes.length < 0) {
            throw new IllegalArgumentException("Bytes to be send cannot be null or empty.");
        }

        if (start < 0 || start >= bytes.length) {
            throw new IndexOutOfBoundsException("Start index out of bounds: " + start + ".");
        }

        this.checkAndCreateStreams();

        this.outputStream.writeInt(length);
        if (length > 0) {
            this.outputStream.write(bytes, start, length);
        }
    }

    /**
     * Checks and when necessary creates the input streams that are needed for socket communication
     * with basic byte arrays. This method will always be ran when a read or write operation is
     * performed. Although this impacts performance a little, we are always sure of an open socket and
     * stream.
     *
     * @throws IOException When the socket already has been closed.
     */
    private void checkAndCreateStreams() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new DataInputStream(socket.getInputStream());
        }

        if (this.outputStream == null) {
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        }
    }

    /**
     * Converts a given byte array into a Java String, while decoding with the UTF-8 character set. UTF-8
     * is used because it is supported and used by most platforms and thus maintaining compatibility.
     * Also it is by default available in Java.
     *
     * @param input Input to convert to a byte array.
     * @return Converted String.
     * @throws UnsupportedEncodingException When the UTF-8 encoding could not be found.
     */
    public static final String bytesToString(byte[] input) throws UnsupportedEncodingException {
        return new String(input, "UTF-8");
    }

    /**
     * Converts a given Java String into a byte array, while decoding with the UTF-8 character set. UTF-8
     * is used because it is supported and used by most platforms and thus maintaining compatibility. Also
     * it is by default available in Java.
     *
     * @param input Input to convert to a String.
     * @return Converted byte array.
     * @throws UnsupportedEncodingException When the UTF-8 encoding could not be found.
     */
    public static final byte[] stringToBytes(String input) throws UnsupportedEncodingException {
        return input.getBytes("UTF-8");
    }

}

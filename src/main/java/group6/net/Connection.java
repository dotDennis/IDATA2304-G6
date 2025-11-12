package group6.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * TCP Connection wrapper class.
 * Simple 4-byte length prefixed framing.
 * Thread-safe for concurrent send/receive from different threads.
 * 
 * Frame format:
 * [ length:int32_be ][ payload:byte[length] ]
 * 
 * Safety:
 * - Validates non-negative length
 * - Enforces a max frame size to prevent OOM
 * - Honors socket timeouts if configurred externally
 */
public class Connection implements Closeable {

    public static final int DEFAULT_MAX_FRAME_SIZE = 1 * 1024 * 1024; // 1 MB

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final int maxFrameSize;

    private final Object sendLock = new Object();
    private final Object recvLock = new Object();

    /**
     * Default constructor with default max frame size.
     * 
     * @param socket an already connected socket
     * @throws Exception on IO errors
     */
    public Connection(Socket socket) throws IOException {
        this(socket, DEFAULT_MAX_FRAME_SIZE);
    }

    /**
     * Constructor with custom max frame size.
     * 
     * @param socket       an already connected socket
     * @param maxFrameSize maximum allowed frame size in bytes
     * @throws Exception on IO errors
     */
    public Connection(Socket socket, int maxFrameSize) throws IOException {
        if (socket == null)
            throw new IllegalArgumentException("Socket cannot be null");
        if (!socket.isConnected())
            throw new IllegalArgumentException("Socket must be connected");
        if (maxFrameSize <= 0)
            throw new IllegalArgumentException("Max frame size must be larger than zero");

        this.socket = socket;
        this.maxFrameSize = maxFrameSize;
        // Buffered streams, data simplifies int/byte handling
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream())); // buffer for performance &
                                                                                         // efficiency
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream())); // avoids many small writes
                                                                                             // and syscalls
    }

    // -------- Framed I/O --------

    /**
     * Sends one frame (length + payload).
     * 
     * @param payload bytes to send (non-null), empty allowed (zero-length frame).
     * @throws IOException on IO errors
     */
    public void sendFrame(byte[] payload) throws IOException {
        Objects.requireNonNull(payload, "payload");
        if (payload.length > maxFrameSize) {
            throw new IOException("Payload size exceeds maximum frame size: " + payload.length + " > " + maxFrameSize);
        }
        synchronized (sendLock) { // to avoid interleaved messages.
            out.writeInt(payload.length); // length prefix, to know when message ends
            if (payload.length > 0) { // useful for pings or empty messages
                out.write(payload); // length=0 means no body
            }
            out.flush(); // ensure data is sent out and not stuck in memory buffer
        }
    }

    /**
     * Receives one frame (blocks until full frame is available or socket timeout
     * fires.
     * 
     * @return the payload bytes (never null, can be zero-length)
     * @throws EOFException if the stream is closed before a full frame is read
     * @throws IOException  on IO errors
     */
    public byte[] recvFrame() throws IOException {
        synchronized (recvLock) { // to avoid corruption if called at same time.
            int len;
            try {
                len = in.readInt(); // read length prefix
            } catch (EOFException eof) {
                throw eof; // propagate eof, caller should treat as connection closed
            }

            if (len < 0) {
                throw new IOException("Invalid negative frame length: " + len);
            }
            if (len > maxFrameSize) {
                throw new IOException("Frame length exceeds maximum frame size: " + len + " > " + maxFrameSize);
            }

            byte[] buf = new byte[len];
            in.readFully(buf); // Blocks until all bytes are read or EOF
            return buf;
        }
    }

    // -------- UTF Helpers --------

    /**
     * Sends a UTF-8 encoded string as a frame.
     * 
     * @throws IOException
     */
    public void sendUtf(String message) throws IOException {
        Objects.requireNonNull(message, "message");
        sendFrame(message.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Receives a UTF-8 encoded string as a frame.
     * 
     * @throws IOException
     */
    public String recvUtf() throws IOException {
        byte[] data = recvFrame();
        return new String(data, StandardCharsets.UTF_8);
    }

    // -------- Socket Controls --------

    /**
     * Sets the socket read timeout on the underlying socket. Affects
     * {@link #recvFrame()}.
     * 
     * @param timeoutMillis milliseconds, zero for infinite (blocking). >0 to bind a
     *                      wait
     *                      time.
     * @throws IOException on IO errors
     */
    public void setSoTimeout(int timeoutMillis) throws IOException {
        socket.setSoTimeout(timeoutMillis);
    }

    /**
     * Returns whether the connection is open locally.
     * <p>
     * Note: this does not guarantee that the remote peer is still connected —
     * only that our side has not closed or shut down the socket.
     * 
     * @return true if open, false if closed
     */
    public boolean isOpen() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed()
                && !socket.isInputShutdown()
                && !socket.isOutputShutdown();
    }

    // -------- Getters --------

    /**
     * Gets the remote address as string.
     * if not available, returns 'unknown'.
     * 
     * @return remote address string, 'unknown' if not available
     */
    public String getRemoteAddress() {
        return socket.getRemoteSocketAddress() != null ? socket.getRemoteSocketAddress().toString() : "unknown";
    }

    /**
     * Gets the local address as string.
     * if not available, returns 'unknown'.
     * 
     * @return local address string, 'unknown' if not available
     */
    public String getLocalAddress() {
        return socket.getLocalSocketAddress() != null ? socket.getLocalSocketAddress().toString() : "unknown";
    }

    /**
     * Gets the maximum frame size.
     * 
     * @return maximum frame size in bytes
     */
    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    @Override
    public void close() throws IOException {
        socket.close(); // closes underlying streams as well
    }

}

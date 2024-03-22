package chatapp.messageconsumer.id;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

public class IdGenerator {

    private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
    private static final int EPOCH_BITS = 41;
    private static final int NODE_ID_BITS = 7;
    private static final int SEQUENCE_BITS = 6;
    private static final int THREAD_BITS = 9;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;
    private static final long maxThread = (1L << THREAD_BITS) - 1;

    // 2024년 1월 1일 0시 0분 0초
    private static final long DEFAULT_CUSTOM_EPOCH = 1704034800000L;

    private final long nodeId;
    private final long thread;
    private final long customEpoch;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    // Let Snowflake generate a nodeId
    protected IdGenerator(long thread) {
        this.nodeId = createNodeId();
        this.customEpoch = DEFAULT_CUSTOM_EPOCH;
        this.thread = thread;
    }

    public long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        return currentTimestamp << (NODE_ID_BITS + THREAD_BITS + SEQUENCE_BITS)
            | (nodeId << (THREAD_BITS + SEQUENCE_BITS)) | (thread << SEQUENCE_BITS)
            | sequence;
    }

    public long[] parse(long id) {
        long maskNodeId = ((1L << NODE_ID_BITS) - 1) << (SEQUENCE_BITS + THREAD_BITS);
        long maskSequence = (1L << SEQUENCE_BITS) - 1;
        long maskThread = ((1L << THREAD_BITS) - 1) << SEQUENCE_BITS;

        long timestamp = (id >> (NODE_ID_BITS + SEQUENCE_BITS + THREAD_BITS)) + customEpoch;
        long nodeId = (id & maskNodeId) >> (SEQUENCE_BITS + THREAD_BITS);
        long sequence = id & maskSequence;
        long thread = (id & maskThread) >> SEQUENCE_BITS;

        return new long[]{timestamp, nodeId, thread, sequence};
    }


    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private long timestamp() {
        return Instant.now().toEpochMilli() - customEpoch;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private long createNodeId() {
        long nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for (byte macPort : mac) {
                        sb.append(String.format("%02X", macPort));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & maxNodeId;
        return nodeId;
    }

    @Override
    public String toString() {
        return "Snowflake Settings [EPOCH_BITS=" + EPOCH_BITS + ", NODE_ID_BITS=" + NODE_ID_BITS
            + ", SEQUENCE_BITS=" + SEQUENCE_BITS + ", CUSTOM_EPOCH=" + customEpoch
            + ", NodeId=" + nodeId + ", ThreadName=" + thread + "]";
    }
}

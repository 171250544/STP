import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * @author wzy
 */
public class Segment {
    /**
     * 报文格式
     * Header: 12 = 1+4+4+3（数据判断位+Seq+Ack+3个标志）
     * data: 数据
     */
    private static int LENGTH;
    private static int HEADER_LENGTH = 12;
    private static int DATA_LENGTH;
    private byte SYN = 0;
    private byte ACK = 0;
    private byte FIN = 0;
    private byte[] seq = {0, 0, 0, 0};
    private byte[] ack = {0, 0, 0, 0};
    private byte[] data;

    /**
     * @param data 传入数据部分
     * @param dataLength 数据部分长度
     * @param seq 序列号
     * @param ack 响应号
     */
    public Segment(byte[] data, int dataLength, int seq, int ack) {
        byte[] tmp = Arrays.copyOfRange(data, 0, dataLength);
        System.arraycopy(data, 0, tmp, 0, dataLength);
        if (tmp.length > DATA_LENGTH)
            throw new RuntimeException("STP packet data too long");
        this.data = tmp;
        setseq(seq);
        setack(ack);
    }

    public Segment(byte SYN, byte ACK, byte FIN, int seq, int ack) {
        this.SYN = SYN;
        this.ACK = ACK;
        this.FIN = FIN;
        setseq(seq);
        setack(ack);
    }

    public int size() {
        return (data == null ? HEADER_LENGTH : LENGTH);
    }

    public byte getSYN() {
        return SYN;
    }

    public void setSYN(byte SYN) {
        this.SYN = SYN;
    }

    public byte getACK() {
        return ACK;
    }

    public void setACK(byte ACK) {
        this.ACK = ACK;
    }

    public byte getFIN() {
        return FIN;
    }

    public void setFIN(byte FIN) {
        this.FIN = FIN;
    }

    public int getseq() {
        return seq[3] & 0xFF |
                (seq[2] & 0xFF) << 8 |
                (seq[1] & 0xFF) << 16 |
                (seq[0] & 0xFF) << 24;
    }

    public void setseq(int seq) {
        this.seq = new byte[]{
                (byte) ((seq >> 24) & 0xFF),
                (byte) ((seq >> 16) & 0xFF),
                (byte) ((seq >> 8) & 0xFF),
                (byte) (seq & 0xFF)
        };
    }

    public int getack() {
        return ack[3] & 0xFF |
                (ack[2] & 0xFF) << 8 |
                (ack[1] & 0xFF) << 16 |
                (ack[0] & 0xFF) << 24;
    }

    public void setack(int ack) {
        this.ack = new byte[]{
                (byte) ((ack >> 24) & 0xFF),
                (byte) ((ack >> 16) & 0xFF),
                (byte) ((ack >> 8) & 0xFF),
                (byte) (ack & 0xFF)
        };
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public static int getLength() {
        return LENGTH;
    }

    public static int getDataLength() {
        return DATA_LENGTH;
    }

    public static int getHeaderLength() {
        return HEADER_LENGTH;
    }

    public static void setLength(int length) {
        LENGTH = length;
    }

    public static void setDataLength(int dataLength) {
        DATA_LENGTH = dataLength;
    }


    @Override
    public String toString() {
        return data.toString();
    }


    public Segment(byte[] segment) {
        if (segment[0] == 1) {
            data = new byte[segment.length - HEADER_LENGTH];
            System.arraycopy(segment, HEADER_LENGTH, data, 0, data.length);
        }
        System.arraycopy(segment, 1, seq, 0, 4);
        System.arraycopy(segment, 5, ack, 0, 4);
        SYN = segment[9];
        ACK = segment[10];
        FIN = segment[11];
    }

    public byte[] toByteArray() {
        byte[] result;
        if (data == null) {
            result = new byte[HEADER_LENGTH];
        } else {
            result = new byte[LENGTH];
            System.arraycopy(data, 0, result, 12, data.length);
        }
        result[0] = (data == null) ? (byte) 0 : 1;
        for (int i = 1; i < 5; i++) {
            result[i] = seq[i - 1];
        }
        for (int i = 5; i < 9; i++) {
            result[i] = ack[i - 5];
        }
        result[9] = SYN;
        result[10] = ACK;
        result[11] = FIN;

        return result;
    }

    public DatagramPacket P2DP() {
        return new DatagramPacket(toByteArray(), toByteArray().length);
    }

    public DatagramPacket P2DP(InetAddress ip, int port) {
        return new DatagramPacket(toByteArray(), toByteArray().length, ip, port);
    }

    @Override
    /**
     比较segment是否相同
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment seg = (Segment) o;

        if (SYN != seg.SYN) return false;
        if (ACK != seg.ACK) return false;
        if (FIN != seg.FIN) return false;
        if (seq != seg.seq) return false;
        if (ack != seg.ack) return false;
        return Arrays.equals(data, seg.data);
    }

    public int hashCode() {
        return Arrays.hashCode(data);
    }

}


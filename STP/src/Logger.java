import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author hj
 */
public class Logger {
    private FileWriter sendWriter;
    private FileWriter receiveWriter;
    private String senderLogFileName = "Log/Sender_log.txt";
    private String receiverLogFileName = "Log/Receiver_log.txt";

    public Logger() throws IOException {
        sendWriter = new FileWriter(senderLogFileName);
        receiveWriter = new FileWriter(receiverLogFileName);
        sendWriter.write("SndOrRecOrDrop  time  type  seq  size  ack" + "\n");
        receiveWriter.write("SndOrRecOrDrop  time  type  seq  size  ack" + "\n");
    }

    /**
     * 将内容写入SenderLog
     */
    public void writeToSenderLog(Segment stpSegment, String sndOrRcvOrDrop, Date startDate) {
        String type = getType(stpSegment);//当前包的类型
        int seq = stpSegment.getseq();//序列号
        int size = 0;//数据报文大小
        if (stpSegment.getData() != null) {
            size = stpSegment.getData().length;
        }
        double time = (new Date().getTime() - startDate.getTime()) / 1000.0;
        int ack = stpSegment.getack();
        try {
            sendWriter.write(sndOrRcvOrDrop + "  " + time + "  " + type + "  " + seq + "  " + size + "  " + ack + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将内容写入ReceiverLog
     */
    public void writeToReceiverLog(Segment stpSegment, String sndOrRcvOrDrop, Date startDate) {
        String type = getType(stpSegment);//当前包的类型
        int seq = stpSegment.getseq();//序列号
        int size = 0;//数据报文大小
        if (stpSegment.getData() != null) {
            size = stpSegment.getData().length;
        }
        double time = (new Date().getTime() - startDate.getTime()) / 1000.0;
        int ack = stpSegment.getack();
        try {
            receiveWriter.write(sndOrRcvOrDrop + "  " + time + "  " + type + "  " + seq + "  " + size + "  " + ack + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成recLog文件写入
     */
    public void finishRecLog() throws IOException {
        receiveWriter.flush();
        receiveWriter.close();
    }

    /**
     * 完成sendLog文件写入
     */
    public void finishSendLog() throws IOException {
        sendWriter.flush();
        sendWriter.close();
    }


    /**
     * 得到包的格式
     */
    public String getType(Segment segment) {
        byte ack = segment.getACK();
        byte syn = segment.getSYN();
        byte fin = segment.getFIN();

        if (ack == 0) {
            if (syn == 1) {
                return "第一次握手请求包";
            }
            if (fin == 1) {
                return "第三次握手请求包";
            }
        } else {
            if (syn == 1) {
                return "第二次握手应答包";
            }
            if (fin == 1) {
                return "第四次握手应答包";
            }
        }
        if (segment.getack() != 0 && segment.getseq() == 0) {
            return "普通应答包";
        }
        return "数据包";
    }
}

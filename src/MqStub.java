import com.ibm.mq.MQDestination;
import com.ibm.mq.MQQueue;
import com.ibm.mq.jms.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.Marshaller;

public class MqStub {
    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            MQQueueSession mqQSession;

            MQQueue mqIn;
            MQQueue mgOut;

            MQQueueReceiver mqReceiver;
            MQQueueSender ueSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1410);

            mqCF.setQueueManager("MQtester");
            mqCF.setChannel("SYSTEM.DEF.SVRCONN");

            mqConn = (MQQueueConnection) mqCF.createQueueConnection();
            mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqQSession.createQueue("Mq.IN");
            mgOut = (MQQueue) mqQSession.createQueue("Mq.OUT");

            mqReceiver = (MQQueueReceiver) mqQSession.createReceiver (mqIn);
            ueSender =(MQQueueSender) mqQSession.createSender (mgOut);



            javax.jms.MessageListener Listener = new javax.jms.MessageListener() {
                @Override
                public void onMessage(Message msg) {
                    System.out.println("Got message !");
                    if (msg instanceof TextMessage) {
                        try {
                            TextMessage tMsg = (TextMessage) msg;
                            String msgText = tMsg.getText();

                            TextMessage message = (TextMessage) mqQSession.createTextMessage(msgText);
                            ueSender.send(message);
                            ueSender.close();
                            mqQSession.close();

                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                }

            };
            mqReceiver.setMessageListener(Listener);
            mqConn.start();
            System.out.println("Stub Started");
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

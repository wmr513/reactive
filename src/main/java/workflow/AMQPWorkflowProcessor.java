package workflow;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPWorkflowProcessor {

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("workflow.q", true, consumer);

		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			System.out.println("message received: " + msg);
			String newMsg = msg.substring(0, msg.indexOf(" shares"));
			byte[] bmsg = newMsg.getBytes();
			System.out.println("Trade fixed: " + newMsg);
			channel.basicPublish("", "trade.eq.q", null, bmsg);
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPWorkflowProcessor().execute();
	}
}






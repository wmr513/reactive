package workflow;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

/**
 * Continuously consume messages every 2 seconds
 */
public class AMQPWorkflowConsumer {

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.eq.q", true, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			String message = new String(msg.getBody());
			System.out.println("message received: " + message);
			String[] parts = message.split(",");
			long shares = new Long(parts[2]).longValue();
			Thread.sleep(1000);
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPWorkflowConsumer().execute();
	}
}

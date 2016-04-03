package workflow;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPWorkflowConsumer2 {

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.eq.q", true, consumer);

		QueueingConsumer.Delivery msg = null;
		
		while (true) {
			try {
				msg = consumer.nextDelivery();
				String message = new String(msg.getBody());
				System.out.println("message received: " + message);
				String[] parts = message.split(",");
				long shares = new Long(parts[2]).longValue();
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("error with trade: " + e.getMessage());
				System.out.println("sending to workflow");
				channel.basicPublish("", "workflow.q", null, msg.getBody());
			}
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPWorkflowConsumer2().execute();
	}
}

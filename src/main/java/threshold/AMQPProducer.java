package threshold;

import java.util.Random;

import workflow.AMQPWorkflowProcessor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPProducer {

	public void execute() throws Exception {
		long threshold = 2000;
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.response.q", true, consumer);
		for (int i=0;i<10;i++) {
//		while (true) {
			long start = System.currentTimeMillis();
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY,AAPL," + shares;
			byte[] message = text.getBytes();
			System.out.println("sending trade: " + text);
			channel.basicPublish("", "trade.request.q", null, message);

			consumer.nextDelivery();
			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("trade confirmation received in " + duration + " ms");
			if ((duration*2) > threshold) {
				threshold = duration*2;
				String msg = "" + threshold;
				byte[] configMsg = msg.getBytes();
				System.out.println("updating threshold: " + msg);
				channel.basicPublish("", "config.q", null, configMsg);
			}
			System.out.println("");
			Thread.sleep(1000);
		}
		channel.close();
		System.exit(0);
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPProducer().execute();
	}
}






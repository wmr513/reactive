package workflow;

import java.util.Random;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

/**
 * Continuously produce messages every 1 second
 */
public class AMQPWorkflowProducer {
	
	public static void main(String[] args) throws Exception {
		AMQPWorkflowProducer app = new AMQPWorkflowProducer();
		app.produceMessages(args);
	}
	
	private void produceMessages(String[] args) throws Exception {		
		Channel channel = AMQPCommon.connect();
		for (int i=0;i<10;i++) {
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY,AAPL," + shares;
			if (i == 4) text = "BUY,AAPL," + shares + " shares";
			byte[] message = text.getBytes();
			System.out.println("sending trade: " + text);
			channel.basicPublish("", "trade.eq.q", null, message);
		}
		AMQPCommon.close(channel);
		System.exit(0);
	}	
}










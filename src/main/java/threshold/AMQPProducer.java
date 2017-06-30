package threshold;

import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPProducer {

	public void execute() throws Exception {
		long threshold = 2000; //simulate read from config (starting point)
		long current = threshold/2;
		long tmin = 0;
		long tmax = 0;
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.response.q", true, consumer);
		for (int i=0;i<10;i++) {
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

			//check t-min
			if (duration < current && duration > tmin) {
				tmin = duration;
				String msg = "UPDATED: place_trade_tmin, " + tmin*2 + " (" + tmin + ")";
				byte[] configMsg = msg.getBytes();
				System.out.println("updating t-min, starting timer...");
				channel.basicPublish("", "config.q", null, configMsg);
			}

			//check t-max
			if (duration > current) {
				if (tmax == 0) {
					tmax = duration;
					String msg = "UPDATED: place_trade_tmax, " + tmax*2 + " (" + tmax + ")";
					byte[] configMsg = msg.getBytes();
					System.out.println("updating t-max, starting timer...");
					channel.basicPublish("", "config.q", null, configMsg);
				} else {
					if (duration > tmax) {
						tmin = current;
						current = tmax;
						tmax = duration; 
						String msg = "UPDATED: place_trade, " + current*2 + " (" + current + ")";
						byte[] configMsg = msg.getBytes();
						System.out.println("updating threshold...");
						channel.basicPublish("", "config.q", null, configMsg);

						msg = "UPDATED: place_trade_tmin, " + tmin*2 + " (" + tmin + ")";
						configMsg = msg.getBytes();
						System.out.println("updating t-min, starting timer...");
						channel.basicPublish("", "config.q", null, configMsg);

						msg = "UPDATED: place_trade_tmax, " + tmax*2 + " (" + tmax + ")";
						configMsg = msg.getBytes();
						System.out.println("updating t-max, starting timer...");
						channel.basicPublish("", "config.q", null, configMsg);
					}
				}
			}
			System.out.println("");
			System.in.read();
		}
		channel.close();
		System.exit(0);
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPProducer().execute();
	}
	
	/*
	public void execute() throws Exception {
		long threshold = 2000;
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.response.q", true, consumer);
		for (int i=0;i<10;i++) {
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
			System.in.read();
		}
		channel.close();
		System.exit(0);
	}
	*/	

	
}






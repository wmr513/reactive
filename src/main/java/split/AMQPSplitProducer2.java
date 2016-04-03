package split;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

public class AMQPSplitProducer2 {

	private List<String> queues = Arrays.asList("trade.1.q", "trade.2.q");

	public void run(int numMsgs) throws Exception {
		Channel channel = AMQPCommon.connect();
		int queueIndex = 0;
		for (int i=0; i<numMsgs; i++) {
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY AAPL " + shares + " SHARES";
			byte[] message = text.getBytes();
			queueIndex = (shares <= 2000) ? 0 : 1;
			System.out.println("sending trade: " + text);
			channel.basicPublish("", queues.get(queueIndex), null, message);
			Thread.sleep(1000);
		}
		
		AMQPCommon.close(channel);
	}
	
	public static void main(String[] args) throws Exception {
		int numMsgs = args.length > 0 ? new Integer(args[0]).intValue() : 1;
		new AMQPSplitProducer2().run(numMsgs);
	}
	
}










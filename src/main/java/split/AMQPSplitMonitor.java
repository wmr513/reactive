package split;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

public class AMQPSplitMonitor {

	public static void main(String[] args) {

		try {
			Channel channel = AMQPCommon.connect();			
			while (true) {
				long consumers = channel.consumerCount(args[0]);
				long queueDepth = channel.messageCount(args[0]);
				System.out.println("consumers: " + consumers + ", pending messages:" + queueDepth);
				Thread.sleep(1000);
			}

//			DeclareOk queue = channel.queueDeclare("trade.eq.q", true, false, false, null);
//			long consumers = queue.getConsumerCount();
//			long queueDepth = queue.getMessageCount();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}






package dispatcher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPThreadDispatcher {

	private Long numThreads = 0l;
	
	public void dispatchMessages() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicQos(1);
		channel.basicConsume("trade.eq.q", false, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
			new Thread(new POJOThreadProcessor(
				this, new String(msg.getBody()))).start();
			numThreads++;
			System.out.println("Threads: " + numThreads);
		}			
	}	
	
	public void tradeComplete() {
		synchronized(numThreads) {
			numThreads = numThreads-1;
		}
	}
	
	public static void main(String[] args) throws Exception {
		new AMQPThreadDispatcher().dispatchMessages();
	}
}

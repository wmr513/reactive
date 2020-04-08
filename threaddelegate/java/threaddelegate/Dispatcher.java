package threaddelegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class Dispatcher {

	//account, thread
	private Map<String, Long> allocationMap = new ConcurrentHashMap<String, Long>();
	
	//thread id, number of messages processing
	private Map<Long, Long> processingCountMap = new ConcurrentHashMap<Long, Long>();
	
	//Thread id, thread object
	private Map<Long, TradeProcessor> threadpool = new ConcurrentHashMap<Long, TradeProcessor>();
	
	private boolean display = false;
	
	public void dispatchMessages() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicQos(1);
		channel.basicConsume("trade.eq.q", false, consumer);

		java.util.Scanner input = new java.util.Scanner(System.in);
	    System.out.print("Display Allocation Map? (y/n): ");
	    display = input.next().equalsIgnoreCase("y");
		input.close();
		
		//start with 5 threads...
		for (long i=1;i<6;i++) {
			TradeProcessor processor = new TradeProcessor(this, i);
			threadpool.put(i, processor);
			processingCountMap.put(i, 0L);
			new Thread(()->processor.start()).start();
		}

		displayAllocationMap();
		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
			String trade = new String(msg.getBody());
			String context = getContext(trade);
			Long threadId = 0L;
						
			if (allocationMap.containsKey(context)) {
				threadId = allocationMap.get(context);
			} else {
				threadId = getNextAvailableThread();
				allocationMap.put(context, threadId);
			}
			processingCountMap.put(threadId, processingCountMap.get(threadId)+1);
			if (display) System.out.println("Dispatcher: Received " + trade);
			displayAllocationMap();
			threadpool.get(threadId).addMessage(new String(msg.getBody()));				
		}			
	}	
	
	public void tradeComplete(long threadId) {
		processingCountMap.put(threadId, processingCountMap.get(threadId)-1);
		if (processingCountMap.get(threadId) == 0) {
			String context = "";
			for (Map.Entry<String,Long> entry : allocationMap.entrySet()) {
				if (entry.getValue() == threadId) {
					context = entry.getKey();
					break;
				}
			}
			allocationMap.remove(context);
		}
		displayAllocationMap();
	}
	
	private String getContextKey(Long threadId) {
		String context = "";
		for (Map.Entry<String,Long> entry : allocationMap.entrySet()) {
			if (entry.getValue() == threadId) {
				context = entry.getKey();
				break;
			}
		}
		return context;
	}
	
	private String getContext(String trade) {
		return trade.substring(trade.lastIndexOf(")") +1, trade.indexOf(",")).trim();
	}
	
	private Long getNextAvailableThread() {
		Long count = Long.MAX_VALUE;
		Long threadId = 0L;
		for (Map.Entry<Long,Long> entry : processingCountMap.entrySet()) {
			if (entry.getValue() < count) {
				count = entry.getValue();
				threadId = entry.getKey();
			}
		}
		return threadId;
	}
	
	private void displayAllocationMap() {
		if (!display) return;
		List<Long> threads = new ArrayList<Long>(threadpool.keySet());
		Collections.sort(threads);		
		threads.forEach((id) -> 
			System.out.println("Thread-" + id + (getContextKey(id).length() > 0 ? "," + getContextKey(id) : "") + "," + processingCountMap.get(id)));
		System.out.println();		
	}
	
	public static void main(String[] args) throws Exception {
		new Dispatcher().dispatchMessages();
	}
}





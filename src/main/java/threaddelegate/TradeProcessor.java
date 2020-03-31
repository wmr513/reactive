package threaddelegate;

import java.util.LinkedList;
import java.util.Queue;


public class TradeProcessor {

	private Dispatcher dispatcher;
	private Long threadId;
	
	private Queue<String> queue = new LinkedList<String>();
	
	public TradeProcessor(Dispatcher dispatcher, Long threadId) {
		this.dispatcher = dispatcher;
		this.threadId = threadId;
	}
	
	public void addMessage(String message) {
		queue.add(message);
	}
	
	public void start() {
		while (true) {
			try {		
				if (queue.size() > 0) {
					Thread.sleep(2000);
					String message = queue.remove();
					System.out.println("Thread " + threadId + ": Placed " + message);
					dispatcher.tradeComplete(threadId);
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
}

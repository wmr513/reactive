package router;

import org.apache.log4j.Logger;

public class POJOMusicProcessor implements POJOOrderProcessor {

	private static Logger log = Logger.getLogger(POJOMusicProcessor.class);

	public void processOrder(String orderItem) {
		log.info("PROCESSING MUSIC ORDER: " + orderItem);
	}
}





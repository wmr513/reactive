package router;

import org.apache.log4j.Logger;

public class POJOBookProcessor implements POJOOrderProcessor {

	public static final Logger log = Logger.getLogger(POJOBookProcessor.class);

	public void processOrder(String orderItem) {
		log.info("PROCESSING BOOK ORDER: " + orderItem);
	}
}

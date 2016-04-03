package router;

import org.apache.log4j.Logger;

public class POJOMovieProcessor implements POJOOrderProcessor {

	private static Logger log = Logger.getLogger(POJOMovieProcessor.class);

	public void processOrder(String orderItem) {
		log.info("PROCESSING MOVIE ORDER: " + orderItem);
	}
}





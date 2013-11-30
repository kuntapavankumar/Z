package edu.concordia.dpis;

import edu.concordia.dpis.commons.Message;
import edu.concordia.dpis.fifo.RequestResolver;

public interface RequestHandler extends RequestResolver {

	Object doOperation(Message request);

}

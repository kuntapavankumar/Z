package edu.concordia.dpis;

import java.util.List;

public interface Command {

	Object execute(List<Object> params);
}

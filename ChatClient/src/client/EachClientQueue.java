package client;

import java.util.concurrent.BlockingDeque;

public class EachClientQueue {
	String ID;
	BlockingDeque<String> msgQueue;
	
	public EachClientQueue(String ID,BlockingDeque<String> q){
		this.ID=ID;
		msgQueue=q;
	}

}

package org.jugendhackt.tinnitus.util;

import java.util.ArrayList;
import java.util.Collections;

public class Cache {

	private Cache instance = null;
	
	private ArrayList<Tuple<String, Long>> cache;

	private Cache() {
		this.cache = new ArrayList<Tuple<String, Long>>();
	}
	
	public Cache getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new Cache();
		}
	}
	
	public Tuple<String, Long> getNextElement() {
		Tuple<String, Long> res = this.cache.get(0);
		this.cache.remove(0);
		
		Collections.rotate(cache, -1);
		
		return res;
	}
	
}

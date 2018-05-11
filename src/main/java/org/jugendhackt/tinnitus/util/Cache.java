package org.jugendhackt.tinnitus.util;

import java.util.ArrayList;
import java.util.Collections;

public class Cache {

	private static Cache instance = null;
	
	private ArrayList<Tuple<String, Integer>> cache;

	private Cache() {
		this.cache = new ArrayList<Tuple<String, Integer>>();
	}
	
	public static Cache getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new Cache();
		}
	}
	
	public Tuple<String, Integer> getNextElement() {
		Tuple<String, Integer> res = this.cache.get(0);
		this.cache.remove(0);
		
		Collections.rotate(cache, -1);
		
		return res;
	}
	
	public void addElement(Tuple<String, Integer> data) {
	    this.cache.add(data);
	}
	
}

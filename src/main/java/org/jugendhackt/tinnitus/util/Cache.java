package org.jugendhackt.tinnitus.util;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cache {

	private static Cache instance = null;
	
	private CopyOnWriteArrayList<DataSet<String, Integer>> cache;

	private Cache() {
		this.cache = new CopyOnWriteArrayList<DataSet<String, Integer>>();
	}
	
	public static Cache getInstance() {
		if(instance != null) {
			return instance;
		} else {
			return new Cache();
		}
	}
	
	public DataSet<String, Integer> getNextElement() {
	    DataSet<String, Integer> res = this.cache.get(0);
		this.cache.remove(0);
		
		Collections.rotate(cache, -1);
		
		return res;
	}
	
	public void addElement(DataSet<String, Integer> data) {
	    this.cache.add(data);
	}
	
}

package org.jugendhackt.tinnitus.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Cache {

	private static Cache instance = null;
	
	private List<DataSet<String, Integer>> cache = Collections.synchronizedList(new ArrayList<DataSet<String, Integer>>());

	private Cache() {

	}
	
	public static synchronized Cache getInstance() {
		if(instance != null) {
			return instance;
		} else {
			instance = new Cache();
			return instance;
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

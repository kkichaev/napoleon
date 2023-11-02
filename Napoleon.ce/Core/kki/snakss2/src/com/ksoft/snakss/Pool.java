package com.ksoft.snakss;

import java.util.ArrayList;
import java.util.List;

public class Pool<T> {
	private final PoolObjectFactory<T> factory;
	private final List<T> freeObjects;
	private final int maxSize;
	
	public Pool(PoolObjectFactory<T> factory, int maxSize) {
		this.factory = factory;
		this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>();
	}
	
	public T newObject() {
		T result = null;
		
		if(freeObjects.isEmpty())
			result = factory.createObject();
		else
			result = freeObjects.remove(freeObjects.size() - 1);
		
		return result;
	}
	
	public void free(T object) {
		if(freeObjects.size() < maxSize)
			freeObjects.add(object);
	}
}

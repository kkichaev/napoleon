package com.ksoft.snakss;

import java.util.ArrayList;
import java.util.List;

public class RabbitPool {
	private List<Rabbit> animals;
	
	public RabbitPool() {
		animals = new ArrayList<Rabbit>();
	}
	
	public void init(int level) {
		animals.clear();
		
		for (int i = 0; i < level; i ++)
			animals.add(new Rabbit());
	}
	
	public Rabbit[] getRabbits() {
		Rabbit[] result = new Rabbit[animals.size()];
		result = animals.toArray(result);
		return result;
	}
	
	public int getSize() {
		return animals.size();
	}
	
	public Cell[] getRabbitCells() {
		Cell[] result = new Cell[animals.size()];
		
		for(int i = 0; i < animals.size(); i++)
			result[i] = animals.get(i).getCell();
		
		return result;
	}

	public Rabbit getRabbit(int x, int y) {
		Rabbit result = null;
		
		for(Rabbit r : animals ) {
			if(r.getX() == x && r.getY() == y) {
				result = r;
				break;
			}
		}
		
		return result;
	}

	public Rabbit getRabbit(int idx) {
		return animals.get(idx);
	}

	public void remove(Rabbit r) {
		animals.remove(r);
	}

	public boolean isEmpty() {
		return animals.size() == 0;
	}
	
	public void clear() {
		animals.clear();
	}
}

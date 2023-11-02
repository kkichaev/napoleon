package com.ksoft.snakss;

import java.util.ArrayList;
import java.util.List;

public class Snake {
	public static enum Direction { LEFT, RIGHT, TOP, BOTTOM }
	private final int START_LEN = 1;
	private List<Cell> tail = new ArrayList<Cell>();
	private int len = START_LEN;
	private Direction dir = Direction.RIGHT;
	private boolean isGrown;
	
	public Snake() {
	}

	public void makeNewSnake() {
		tail.clear();
		
		for(int i = 0; i < len; i++)
			tail.add(new Cell());
		
		init();
	}
	
	public synchronized void step() {
		Cell head = tail.get(0);
		Cell last = null;
		
		if(isGrown) {
			last = new Cell();
			isGrown = false;
		}else
			last = tail.remove(tail.size() - 1);
		
		last.x = head.x;
		last.y = head.y;
		
		if (dir ==Direction.RIGHT)
			last.x++;
		else if (dir == Direction.LEFT)
			last.x--;
		else if(dir == Direction.TOP)
			last.y--;
		else if(dir == Direction.BOTTOM)
			last.y++;
		
		tail.add(0, last);
	}
	
	private synchronized List<Cell> getTail(){ return tail; }
	
	public synchronized void init() {
		isGrown = false;
		dir = Direction.RIGHT;
		
		int x = 0;
		int y = 0;
		
		for(Cell t : getTail()) {
			t.x = x--;
			t.y = y;
		}
	}

	public void changeDirection(int sx, int sy) {
		Cell head = tail.get(0);
		
		if((dir == Direction.RIGHT || dir == Direction.LEFT) && sy > head.y)
			dir = Direction.BOTTOM;
		else if((dir == Direction.RIGHT || dir == Direction.LEFT) && sy < head.y)
			dir = Direction.TOP;
		else if((dir == Direction.BOTTOM || dir == Direction.TOP) && sx > head.x)
			dir = Direction.RIGHT;
		else if((dir == Direction.BOTTOM || dir == Direction.TOP) && sx < head.x)
			dir = Direction.LEFT;
	}

	public void grow() {
		isGrown = true;
	}
	
	public Direction getDirection() {
		return dir;
	}
	
	public Cell getHead() { 
		return tail.get(0); 
	}
	
	public Cell[] getBody() {
		Cell[] result = new Cell[tail.size()];
		tail.toArray(result);
		
		return result;
		
	}

	public int getSize() { return tail.size(); }
}

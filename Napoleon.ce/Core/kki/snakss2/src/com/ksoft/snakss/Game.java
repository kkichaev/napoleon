package com.ksoft.snakss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.ksoft.snakss.Snake.Direction;

public class Game {
	public interface GameEventListener{
		void onEat();
		void onGameOver();
	}

	private Snake snake;
	private RabbitPool rabbits;
	private int width;
	private int height;
	private boolean gameOver = false;
	private GameEventListener gameEventListener;
	private int level = 1;
	
	public void setGameEventListenet(GameEventListener listener) {
		this.gameEventListener = listener;
	}
	
	private void fireEatListener() {
		if (gameEventListener != null)
			gameEventListener.onEat();
	}
	
	private void fireGameOver() {
		if (gameEventListener != null)
			gameEventListener.onGameOver();
	}

	public Game() {
		final int DEF_WIDTH = 20;
		final int DEF_HEIGHT = 14;
		
		this.width = DEF_WIDTH;
		this.height = DEF_HEIGHT;
		
		this.snake = new Snake();
		this.rabbits = new RabbitPool();
		
		restart();
	}

	public void restart() {
		level = 1;
		gameOver = false;
		snake.makeNewSnake();
		makeNewRabbits();
	}
	
	public Snake getSnake() { return snake; }
	public RabbitPool getRabbits() { return rabbits; }
	
	private void initRabbits(Cell[] snake) {
		List<Cell> inited = new ArrayList<Cell>();
		Rabbit[] rbs = rabbits.getRabbits();
		for(int i = 0; i < rbs.length; i++) {
			Rabbit r = rbs[i];
			
			getRabbitFreeCell(snake, inited, r);
		}
	}

	private void getRabbitFreeCell(Cell[] snake, List<Cell> inited, Rabbit r) {
		Cell[] c = new Cell[snake.length + inited.size()];
		System.arraycopy(snake, 0, c, 0, snake.length);
		Cell[] b = new Cell[inited.size()];
		b = inited.toArray(b);
		System.arraycopy(b, 0, c, snake.length, b.length);
		Cell f = getFreeCell(hashSnakeBody(c));
		
		if(f != null) {
			r.setCell(f);
			inited.add(f);
		}
	}
	
	private Cell getFreeCell(Map<Integer, Set<Integer>> xy) {
		final int MAX_ITER = 1000000;
		int counter = 0;
		Cell result = new Cell();
		
		Random r = new Random();
		
		do {
			result.x = r.nextInt(width);
			result.y = r.nextInt(height);
		}while(xy.containsKey(result.x) && xy.get(result.x).contains(result.y) && counter < MAX_ITER);
		
		if (counter > MAX_ITER)
			result = null;
		
		return result;
	}

	private Map<Integer, Set<Integer>> hashSnakeBody(Cell[] snake) {
		Map<Integer, Set<Integer>> xy = new HashMap<Integer, Set<Integer>>();
		
		for(Cell p : snake) {
			if (!xy.containsKey(p.x))
				xy.put(p.x, new HashSet<Integer>());
			
			Set<Integer> y = xy.get(p.x);
			y.add(p.y);
		}
		
		return xy;
	}

	public int getWidth() { return width; }
	
	public int getHeight() { return height; }

	public void pressed(int sx, int sy) {
		snake.changeDirection(sx, sy);
	}

	public void step() {
		if (!gameOver) {
			Cell head = snake.getHead();
			Direction dir = snake.getDirection();
			gameOver = !canStep(head, dir);
			
			if (!gameOver) {
				snake.step();
				
				gameOver = isSnakeEatHimself();
				
				if(!gameOver)
					gameOver = isSnakeEatHimself();
				
				if (!gameOver) {
					head = snake.getHead();
					
					Rabbit r = rabbits.getRabbit(head.x, head.y);
					
					if(r != null) {
						fireEatListener();
						snake.grow();
						rabbits.remove(r);
						
						if(rabbits.isEmpty()) {
							level++;
							makeNewRabbits();
						}
					}
				}
			}
			
			if(gameOver)
				fireGameOver();
		}
	}

	private void makeNewRabbits() {
		this.rabbits.init(level);
		initRabbits(snake.getBody());
	}
	
	private boolean isSnakeEatHimself() {
		boolean result = false;
		Cell head = snake.getHead();
		Cell[] body = snake.getBody();
		
		if (body.length > 0) {
			Cell[] tail = new Cell[body.length - 1];
			System.arraycopy(body, 1, tail, 0, tail.length);
			
			Map<Integer, Set<Integer>> hash = hashSnakeBody(tail);
			if (hash.containsKey(head.x) && hash.get(head.x).contains(head.y))
				result = true;
		}
		
		return result;
	}

	private boolean canStep(Cell head, Direction dir) {
		return 
		(dir == Direction.RIGHT && head.x < getWidth() - 1) ||
		(dir == Direction.BOTTOM && head.y < getHeight() - 1) ||
		(dir == Direction.TOP && head.y > 0) ||
		(dir == Direction.LEFT && head.x > 0);
	}
	
	public int getScrore() {
		return snake.getSize() - 1;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}

	public int getLevel() {
		return level;
	}
}

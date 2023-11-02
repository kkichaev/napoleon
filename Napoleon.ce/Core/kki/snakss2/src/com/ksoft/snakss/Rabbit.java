package com.ksoft.snakss;

class Rabbit
{
	private Cell cell;
	
	public Rabbit() {
		cell = new Cell(0, 0);
	}
	
	public int getX() {
		return cell.x;
	}
	
	public int getY() {
		return cell.y;
	}
	
	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell c) {
		this.cell.x = c.x;
		this.cell.y = c.y;
	}
}
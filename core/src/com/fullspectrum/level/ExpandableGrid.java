package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class ExpandableGrid<T> {

	// Represent min and max row/col positions inclusive
	private int minRow;
	private int minCol;
	private int maxRow;
	private int maxCol;
	
	private Array<Array<T>> grid;
	private String nullCharacter;
	
	public ExpandableGrid() {
		this(1, 1);
	}
	
	public ExpandableGrid(int width, int height) {
		if(width < 1 || height < 1) throw new IllegalArgumentException("Width and height of grid must be >= 1.");
		nullCharacter = "-";
		grid = new Array<Array<T>>();
		init(height, width);
	}
	
	private void init(int rows, int cols) {
		for(int row = 0; row < rows; row++) {
			Array<T> gridRow = new Array<T>();
			for(int col = 0; col < cols; col++) {
				gridRow.add(null);
			}
			grid.add(gridRow);
		}
		maxRow = rows - 1;
		maxCol = cols - 1;
	}
	
	public void add(int row, int col, T value) {
		if(!contains(row, col)) {
			int growRows = 0;
			if(row < minRow) {
				growRows = row - minRow;
			} else if (row > maxRow) {
				growRows = row - maxRow;
			}
			
			int growCols = 0;
			if(col < minCol) {
				growCols = col - minCol;
			} else if (col > maxCol) {
				growCols = col - maxCol;
			}
			grow(growRows, growCols);
		}
		set(row, col, value);
	}

	public void addXY(int x, int y, T value) {
		add(y, x, value);
	}
	
	public void remove(int row, int col) {
		if(!contains(row, col)) {
			throw new ArrayIndexOutOfBoundsException("Grid does not contain a point at " + row + ", " + col);
		}
		set(row, col, null);
		shrink();
	}
	
	public void removeXY(int x, int y) {
		remove(y, x);
	}
	
	public boolean contains(int row, int col) {
		return row <= maxRow && row >= minRow && col <= maxCol && col >= minCol;
	}
	
	public boolean containsXY(int x, int y) {
		return contains(y, x);
	}
	
	public void set(int row, int col, T value) {
		grid.get(row - minRow).set(col - minCol, value);
	}
	
	public void setXY(int x, int y, T value) {
		set(y, x, value);
	}
	
	private void grow(int rows, int cols) {
		if(rows != 0){
			for(int i = 0; i < Math.abs(rows); i++) {
				Array<T> row = new Array<T>(getCols());
				for(int j = 0; j < getCols(); j++) {
					row.add(null);
				}
				grid.insert(rows < 0 ? 0 : grid.size, row);
			}
		} 
		if(cols != 0) {
			for(int i = 0; i < Math.abs(cols); i++) {
				for(int j = 0; j < getRows(); j++) {
					grid.get(j).insert(cols < 0 ? 0 : grid.get(j).size, null);
				}
			}
		}
		
		// Update the bounds
		if(rows < 0) {
			minRow += rows;
		} else if(rows > 0) {
			maxRow += rows;
		}
		if(cols < 0) {
			minCol += cols;
		} else if(cols > 0) {
			maxCol += cols;
		}
	} 
	
	private void shrink() {
		// First loop throw rows, remove if all null
		for(int row = minRow; getRows() != 1; row++) {
			boolean allEmpty = true;
			for(int col = minCol; col <= maxCol; col++) {
				if(get(row, col) != null) {
					allEmpty = false;
					break;
				}
			}
			if(allEmpty && getRows() > 1) {
				grid.removeIndex(row - minRow);
				minRow++;
			} else {
				break;
			}
		}
		
		for(int row = maxRow; getRows() != 1; row--) {
			boolean allEmpty = true;
			for(int col = minCol; col <= maxCol; col++) {
				if(get(row, col) != null) {
					allEmpty = false;
					break;
				}
			}
			if(allEmpty && getRows() > 1) {
				grid.removeIndex(row - minRow);
				maxRow--;
			} else {
				break;
			}
		}
		
		for(int col = minCol; getCols() != 1; col++) {
			boolean allEmpty = true;
			for(int row = minRow; row <= maxRow; row++) {
				if(get(row, col) != null) {
					allEmpty = false;
					break;
				}
			}
			if(allEmpty && getCols() > 1) {
				removeColumn(col - minCol);
				minCol++;
			} else {
				break;
			}
		}
		
		for(int col = maxCol; getCols() != 1; col--) {
			boolean allEmpty = true;
			for(int row = minRow; row <= maxRow; row++) {
				if(get(row, col) != null) {
					allEmpty = false;
					break;
				}
			}
			if(allEmpty && getCols() > 1) {
				removeColumn(col - minCol);
				maxCol--;
			} else {
				break;
			}
		}
	}
	
	private void removeColumn(int col) {
		for(int row = 0; row < getRows(); row++) {
			grid.get(row).removeIndex(col);
		}
	}
	
	public T get(int row, int col) {
		if(!contains(row, col)) {
			throw new ArrayIndexOutOfBoundsException("Grid does not contain a point at " + row + ", " + col);
		}
		return grid.get(row - minRow).get(col - minCol);
	}
	
	public T getXY(int x, int y) {
		return get(y, x);
	}
	
	public int getRows() {
		return maxRow - minRow + 1;
	}
	
	public int getCols() {
		return maxCol - minCol + 1;
	}
	
	public int getWidth() {
		return getCols();
	}
	
	public int getHeight() {
		return getRows();
	}
	
	public void setNullCharacter(String nullCharacter) {
		this.nullCharacter = nullCharacter;
	}
	
	public String getNullCharacter() {
		return nullCharacter;
	}
	
	public Array<Array<T>> getGrid() {
		return grid;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("minRow: " + minRow + ", minCol: " + minCol + ", maxRow: " + maxRow + ", maxCol: " + maxCol + "\n");
		for(int row = maxRow; row >= minRow; row--) {
			for(T cell : grid.get(row - minRow)) {
				builder.append((cell == null ? nullCharacter : cell) + " ");
			}
			builder.append("\n");
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grid == null) ? 0 : grid.hashCode());
		result = prime * result + maxCol;
		result = prime * result + maxRow;
		result = prime * result + minCol;
		result = prime * result + minRow;
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ExpandableGrid other = (ExpandableGrid) obj;
		if (grid == null) {
			if (other.grid != null) return false;
		}
		else if (!grid.equals(other.grid)) return false;
		if (maxCol != other.maxCol) return false;
		if (maxRow != other.maxRow) return false;
		if (minCol != other.minCol) return false;
		if (minRow != other.minRow) return false;
		return true;
	}
	
	
}

package SchroldOthello;
import java.util.Scanner;
/**
 * The Move class represents a move on the Othello game board.
 * 
 * @author Joshua Schrold
 * @version 10/3/2021
 *
 */
public class Move {
	
	private int index;
	private Double value;
	
	/**
	 * Constructor for the move object.
	 * 
	 * @param m	string representation of move
	 */
	Move(String m) {
		/* Process the string */
		Scanner s = new Scanner(m);
		s.next();	// skip move player string
		int c = -1;	// column
		int r = -1;	// row
		if (s.hasNext()) c = s.next().charAt(0)-96;
		if (s.hasNext()) r = s.nextInt();
		s.close();
		
		/* Check if passing, if not then store index of move */
		if (c == -1 || r == -1) {
			index = -1;
		} else {
			index = (r*10)+c;
		}
	}
	
	/**
	 * Constructor for a passing move.
	 */
	Move() {
		index = -1;
	}
	
	/**
	 * Returns the index of the move for a one-dimensional board representation.
	 * 
	 * @return the index of the move
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Get the value of this move.
	 * 
	 * @return the value of this move
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * Set the value of this move.
	 * 
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * Returns true if is a passing move; false otherwise.
	 * 
	 * @return true if is a passing move; false otherwise
	 */
	public boolean isPass() {
		if (index==-1) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		/* If the object is compared with itself then return true */
        if (o == this) {
            return true;
        }
 
        /* Check if o is an instance of Move or not */
        if (!(o instanceof Move)) {
            return false;
        }
         
        /* Cast o to Move */
        Move move = (Move) o;
         
        /* Check if is the same move by comparing index */
        return (index == move.index);
	}
	
	@Override
	public String toString() {
		if (isPass())
			return "";
		char col = (char)(Board1d.indexToCol(index)+96);
		int row = Board1d.indexToRow(index);
		return String.format("%c %d", col, row);
	}
}

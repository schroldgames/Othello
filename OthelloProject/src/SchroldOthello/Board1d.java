package SchroldOthello;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.ArrayList;
import java.util.regex.*;  
/**
 * A one-dimensional representation of an Othello game board.
 * 
 * @author Joshua Schrold
 * @version 10/3/2021
 *
 */
public class Board1d {
	
	/**
	 * Standard input stream
	 */
	public static final Scanner CONSOLE = new Scanner(System.in);
	/**
	 * Integer representation of a space occupied by my piece
	 */
	public static final int ME = 1; 
	/**
	 * Integer representation of a space occupied by opponent's piece
	 */
	public static final int OPPONENT = -1;
	/**
	 * Integer representation of a border space
	 */
	public static final int BORDER = -2;
	/**
	 * Integer representation of an empty space
	 */
	public static final int EMPTY = 0;
	
	private static final int[] DIRECTIONS= {-10, -9, 1, 11, 10, 9, -1, -11};
	
	private int board[];
	private String myColor;
	private String opponentColor;
	private int moveNumber = 0;
	
	private static double timeAllocation[] = {0.015, 0.015, 0.015, 0.015, 0.025, 0.025, 0.025, 0.025, 0.025, 0.025,
            0.048,  0.048, 0.048, 0.048, 0.048, 0.048, 0.050, 0.051, 0.052, 0.053,
            0.044,  0.045, 0.049, 0.049, 0.049, 0.051, 0.053, 0.055, 0.057, 0.059,
            0.060, 0.060, 0.061, 0.062, 0.063, 0.064, 0.065, 0.065, 0.065, 0.065,
            0.167, 0.168, 0.169, 0.169, 0.171, 0.172, 0.173, 0.175, 0.180, 0.180,
            0.181, 0.187, 0.196, 0.199, 0.220, 0.220, 0.220, 0.220, 0.220, 0.220,
            0.220, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250, 0.250};
	
	public static int timeRemaining;	/* Time remaining in seconds */
	public static Timer timer;
	public static boolean timeUP;
	
	/**
	 * Constructor to create a new board for my color.
	 * 
	 * @param myColor my color
	 */
	Board1d(String myColor) {
		this.myColor = myColor;
		if (myColor.equals("B")) opponentColor="W";
		else opponentColor="B";
		
		/* Initialize all cells to EMPTY */
		board = new int[100];
		Arrays.fill(board, EMPTY);
		
		/* Initialize all border cells to BORDER */
		for (int i=0; i < board.length; i++) {
			int row = indexToRow(i);
			int col = indexToCol(i);
			if (row == 0 || row == 9 || col == 0 || col == 9) {
				board[i] = BORDER;
			}
		}
				
		/**
		 * Initialize black and white starting pieces.
		 */
		int black = ME;
		int white = OPPONENT;
		if (myColor.equals("W")) {
			white=ME;
			black=OPPONENT;
		}
		// NW White position
		board[rowColToIndex(4, 4)] = white;
		// SE White position
		board[rowColToIndex(5, 5)] = white;
		// NE Black position
		board[rowColToIndex(4, 5)] = black;
		// SW Black position
		board[rowColToIndex(5, 4)] = black;
		
		printComment(toString());
	}
	
	/**
	 * Constructor to create a new board from a previous board state.
	 * 
	 * @param oldBoard the previous board state
	 */
	Board1d(Board1d oldBoard) {
		myColor = oldBoard.getMyColor();
		opponentColor = oldBoard.getOpponentColor();
		board = oldBoard.getBoard().clone();
		moveNumber = oldBoard.getMoveNumber();
	}
	
	/**
	 * Returns the one-dimensional array representing this board.
	 * 
	 * @return the board
	 */
	public int[] getBoard() {
		return board;
	}
	
	/**
	 * Returns my color for this board.
	 * 
	 * @return my color
	 */
	public String getMyColor() {
		return myColor;
	}
	
	/**
	 * Returns the opponent's color for this board.
	 * 
	 * @return the opponent's color
	 */
	public String getOpponentColor() {
		return opponentColor;
	}
	
	/**
	 * Return the current move number for this board.
	 * 
	 * @return the move number for this board
	 */
	public int getMoveNumber() {
		return moveNumber;
	}

	/* 
	 * Helper method for converting from row and column
	 * to 1d board index.
	 */
	public static int rowColToIndex(int r, int c) {
		return ( (r * 10) + c );
	}
	/* 
	 * Helper method for converting from 1d board index
	 * to row.
	 */
	public static int indexToRow(int i) {
		return ( i / 10 );
	}
	/* 
	 * Helper method for converting from 1d board index
	 * to column.
	 */
	public static int indexToCol(int i) {
		return ( i % 10 );
	}
	
	/**
	 * Generate all possible moves for a player.
	 * 
	 * @param player player to generate moves for; {@value ME} or {@value OPPONENT}
	 * @return a list of moves
	 */
	public ArrayList<Move> generateMoves(int player) {
		/* Get player color */
		String pColor = "";
		switch(player) {
			case ME:
				pColor=myColor;
				break;
			case OPPONENT:
				pColor=opponentColor;
				break;
		}
		
		/* List to store potential moves in */
		ArrayList<Move> moveList = new ArrayList<Move>();
		
		/* Iterate through each player piece on board */
		for (int pieceIndex = 0; pieceIndex < board.length; pieceIndex++) {
			int piece = board[pieceIndex];
			if (piece == player) {
				/* Iterate through each cardinal direction */
				for (int direction : DIRECTIONS) {
					/* Start off one position towards direction */
					int curIndex = pieceIndex+direction;
					
					/* Move through direction while opponent pieces are present */
					int count = 0;
					while (curIndex < 100 && board[curIndex] == player*(-1)) {
						curIndex += direction;
						count += 1;
					}
					
					/* Add move to moveList if legal */
					if (board[curIndex] == EMPTY && count > 0) {
						Move validMove = new Move(String.format("%s %c %d",
								pColor, (char)(indexToCol(curIndex)+96), indexToRow(curIndex)));
						/* Don't add duplicates */
						if (!moveList.contains(validMove))
							moveList.add(validMove);
					}
				}
			}
		}
		
		/* If no moves can be made, player should pass */
		if (moveList.isEmpty()) {
			moveList.add(new Move(pColor));
		}
		
		return moveList;
	}
	
	/**
	 * Apply a move to the board.
	 * 
	 * @param player the player making the move; ME or OPPONENT
	 * @param move the move to be made
	 */
	public void applyMove(int player, Move move) {
		/* Increment the move counter */
		moveNumber++;
		
		/* Passing move */
		if (move.isPass()) return;
		
		/* Index of the move */
		int index = move.getIndex();
		
		/* Iterate through each cardinal direction */
		for (int direction : DIRECTIONS) {
			/* Start off one position towards direction */
			int curIndex = index+direction;
			
			/* Move through direction while opponent pieces are present */
			int count = 0;
			while (curIndex < 100 && board[curIndex] == player*(-1)) {
				curIndex += direction;
				count += 1;
			}
			
			/* Apply move if legal */
			if (board[curIndex] == player) {
				board[index] = player;
				for (int i = 1; i<=count; i++) {
					board[index+(i*direction)] = player;
				}
			}
		}
	}

	/**
	 * Select the best possible move for me. If the MANUAL flag is
	 * set to true, then wait for manual input of move.
	 * 
	 * @return my move
	 */
	public Move getMyMove() {
		/* If verbose moves flag is set, print all my possible moves */
		if (OthelloDriver.VERBOSEMOVES) {
			ArrayList<Move> moveList = generateMoves(ME);
			printComment("My possible moves:");
			for (Move m : moveList) {
				printComment(m.toString());
			}
			printComment("");
		}
		
		/* If manual input mode is enabled, get my move from stdin */
		if (OthelloDriver.MANUAL) {
			String input = getInput();
			if (!Pattern.matches(String.format("^[%s]( [a-h] [1-8])?$", myColor), input)) {
				System.out.println("ERROR: Improper move " + input);
				System.exit(1);
			}
			return new Move(input);
		}
		
		/* Start the timer */
		timeUP = false;
		timer = new Timer();
		
		int timeForMove = (int)(timeAllocation[moveNumber]*(double)timeRemaining); 
		printComment("(Move Time:  " + timeForMove + " seconds)");
	    timer.schedule(new InterruptTask(), timeForMove*1000);
	    
		/* Get my move using alpha beta */
		double alpha = Double.MIN_VALUE;
        double beta = Double.MAX_VALUE;
        int maxDepth = 14;
        
        Move move = alphaBeta(this, 0, 1, alpha, beta, maxDepth);
        
        /* End the timer task and update variables */
        if (!timeUP)
        	timer.cancel();
        timeRemaining -= timeForMove;
        printComment("(Remaining Time:  " + timeRemaining + " seconds)");
        
        return move;
	}

	private Move alphaBeta(Board1d currentBoard, int ply, int player, double alpha, double beta, int maxDepth) {
	    /*
	    currentBoard -> the prospective board at level ply
	    ply -> current level in game tree
	    player --> player = 1  is the ai (max)
	               player = -1  is the opponent (min)
	    alpha --> the lowest value that max player will accept
	    beta  --> the highest value that min player will accept
	    maxDepth --> level of terminal nodes  (lookahead level)
	    */
		
		/* Check if reached terminal leaf node */
		if (ply >= maxDepth) {
			/* Return a passing move */
            Move returnMove = new Move();
            returnMove.setValue(currentBoard.evaluate());
            return returnMove;
        } else {
        	/* Generate moves for player */
        	ArrayList<Move> moves = currentBoard.generateMoves(player);
        	/* If moves list is empty, add pass move to list */
        	if (moves.isEmpty())	moves.add(new Move());
        	/* Set the best move to the first move in move list */
        	Move bestMove = moves.get(0);
        	/* Iterate through each move in the move list */
        	for (Move move: moves) {
        		/* If time has ran out, just return first move */
        		if (timeUP) {
        			bestMove.setValue(currentBoard.evaluate());
        			return bestMove;
        		}
        		/* Create a new board and apply the current move */
        		Board1d newBoard = new Board1d(currentBoard);
        		newBoard.applyMove(player, move);
        		/* Perform alpha beta for opponent's best move */
        		Move tempMove = alphaBeta(newBoard, ply+1, -player, -beta, -alpha, maxDepth);
        		/* Determine if this move is the best move */
        		move.setValue(-tempMove.getValue());
        		if (move.getValue() > alpha) {
        			bestMove = move;
        			alpha = move.getValue();
        			if (alpha > beta)
        				return bestMove;
        		}
        	}
        	return bestMove;
        }
	}

	/**
	 * Evaluates the current board state.
	 * 
	 * @return a double representing the value of the current board
	 */
	public double evaluate() {
		int playerMoveCount = generateMoves(ME).size();
		int oppMoveCount = generateMoves(OPPONENT).size();
		int totalMoveCount = playerMoveCount + oppMoveCount;
		int netMoves = playerMoveCount - oppMoveCount;
		int netPieces = 0;
		int totalPieces = 0;
		
		/* Calculate net pieces */
		for (int i=0; i<100; i++)
			if (board[i] != BORDER && board[i] != 0)
				netPieces += board[i];
				totalPieces++;
		
		/* End game condition */
		if (totalMoveCount == 0) {
			return netPieces * 1000000;
		} else {
			/* Evaluation function */
			double currentMobility = netMoves/(double)totalMoveCount;
			double np = netPieces/(double)totalPieces;
			
			return 50*currentMobility + 50*np;
		}
	}

	/**
	 * Get the opponent's move through standard input. If LOCAL
	 * flag is set to true, generate opponent move locally
	 * instead.
	 * 
	 * @return opponent's move
	 */
	public Move getOpponentMove() {
		/* Used if playing locally against AI */
		if (OthelloDriver.LOCAL) {
			/* Get opponent move randomly */
			ArrayList<Move> moves = generateMoves(OPPONENT);
			Random rand = new Random();
			Move move = moves.get(rand.nextInt(moves.size()));
			printComment(String.format("%s %s", opponentColor, move.toString()));
			return move;
		}
		
		String input = getInput();
		/* Check if opponent declares end game. */
		if (Pattern.matches("^[0-9]+$", input)) {
			/* Game is over */
			printComment("GAME OVER.");
			System.out.println(String.format("%d", getBlackPieces()));
			CONSOLE.close();
			System.exit(0);
		}
		/* Check if the move is valid. */
		else if (!Pattern.matches(String.format("^[%s]( [a-h] [1-8])?$", opponentColor), input)) {
			System.out.println("ERROR: Opponent improper move " + input);
			System.exit(1);
		}
		return new Move(input);
	}
	
	/**
	 * Check to see if the game is over by determining if both players
	 * can only pass.
	 * 
	 * @return true if game is over; false otherwise
	 */
	public boolean gameOver() {
		ArrayList<Move> myMoves = generateMoves(ME);
		ArrayList<Move> opponentMoves = generateMoves(OPPONENT);
		
		if (myMoves.contains(new Move(myColor))
				&& opponentMoves.contains(new Move(opponentColor)))
			return true;
		
		return false;
	}
	
	/**
	 * Helper method to return the number of black pieces on
	 * the board.
	 * 
	 * @return the number of black pieces on board
	 */
	public int getBlackPieces() {
		int count = 0;
		int black = 0;
		
		/* Determine who is playing black */
		switch (myColor) {
			case "B":
				black = ME;
				break;
			case "W":
				black = OPPONENT;
				break;
		}
		
		/* Count up how many black pieces there are */
		for (int piece : board) {
			if (piece == black) count++;
		}
		
		return count;
	}

	@Override
	public String toString() {
		String s = "";
		
		// Top legends
		s += String.format("%2s", " ");
		s += String.format("%2s", " ");
		for (int i=65; i<65+8; i++)
			s += String.format("%2c", (char)(i));
		s += "\n";
		
		for (int i=0; i<board.length; i++) {
			int row = indexToRow(i);
			int col = indexToCol(i);
			
			// Side legends
			if (row != 0 && row != 9 && col==0)
				s += String.format("%2d", row);
			else if (col==0)
				s += String.format("%2s", " ");
			
			String value;
			switch(board[rowColToIndex(row, col)]) {
				case ME:
					value=myColor;
					break;
				case OPPONENT:
					value=opponentColor;
					break;
				case EMPTY:
					value="-";
					break;
				default:
					value="*";
					break;
			}
			
			s += String.format("%2s", value);
			
			if (col==9) {
				s += "\n";
			}
		}
		
		return s;
	}
	
	/**
	 * Read input from the console; skip over commented lines.
	 * 
	 * @return non-comment input from console
	 */
	public static String getInput() {
		String input = CONSOLE.nextLine();
		while (input.startsWith("C")) {
			input = CONSOLE.nextLine();
		}
		return input;
	}
	
	/**
	 * Print a comment to the console.
	 * 
	 * @param c the comment to print
	 */
	public static void printComment(String c) {
		Scanner s = new Scanner(c);
		while (s.hasNext()) {
			String line = s.nextLine();
			System.out.println("C " + line);
		}
		s.close();
	}
}

package SchroldOthello;
/**
 * The main driver class for the Othello board game program.
 * 
 * @author Joshua Schrold
 * @version 10/3/2021
 * 
 */
public class OthelloDriver {
	
    /**
     * An enum that packages all command line options together. User can use -h
     * option to print all available options.
     */
    public static enum OPTIONS {
    	/**
    	 * The help option.
    	 */
        h("          : Display help info. All other options ignored."),
        /**
    	 * A flag to run the program with a local AI opponent.
    	 */
    	l("           : Play against the local AI opponent."),
        /**
         * A flag to run the program in manual mode.
         */
        m("          : Run Othello in manual mode."),
    	/**
    	 * A flag to print my possible moves to console.
    	 */
    	vm("          : Run Othello in verbose moves mode.");
    	
        String helpString;

        OPTIONS(String hStr) {
            helpString = hStr;
        }

        String getHelpString() {
            return helpString;
        }
    }

    /**
     * Flag to determine if game should be run with local AI opponent
     * for debugging.
     */
    public static boolean LOCAL = false;
    /**
     * Flag to determine if my moves should be made manually for
     * debugging.
     */
    public static boolean MANUAL = false;
    /**
     * Flag to determine if my possible moves should be printed to
     * the console.
     */
    public static boolean VERBOSEMOVES = false;

	/**
	 * The main method to handle playing the Othello game.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		
		/* Handle command line arguments */
		if (args.length > 0) {
			parseCommandLine(args);
		}
		
		/* Get my color and initialize board */
		String myColor = getMyColor();
		Board1d board = new Board1d(myColor);
		
		/* Set time limit in seconds */
		Board1d.timeRemaining = 10 * 60;
		
		/* Determine the player to go first */
		int currentPlayer;
		if (myColor.equals("B")) currentPlayer=Board1d.ME;
		else currentPlayer=Board1d.OPPONENT;
		
		/* Ready up */
		System.out.println(String.format("R %s", myColor));
		
		/* Play until game is over */
		while (!board.gameOver()) {
			/* Get the next move */
			Move move;
			if (currentPlayer==Board1d.ME) {
				move = board.getMyMove();
				/* Output my move for the referee */
				String moveString = String.format("%s %s",
						myColor, move.toString()).trim();
				System.out.println(moveString);
			} else {
				move = board.getOpponentMove();
			}
			/* Apply the move and print out the board */
			board.applyMove(currentPlayer, move);
			Board1d.printComment(board.toString());
			if (currentPlayer==Board1d.ME) {
				/* Print out the current board evaluation */
		        Board1d.printComment(String.format("Board evaluation: %.3f", board.evaluate()));
			}
			/* Switch players */
			currentPlayer = (-1)*currentPlayer;
		}
		
		/* Game is over */
		Board1d.printComment("GAME OVER.");
		System.out.println(String.format("%d", board.getBlackPieces()));
		if (board.getBlackPieces() < 32)
			Board1d.printComment("White wins!");
		else
			Board1d.printComment("Black wins!");
		Board1d.CONSOLE.close();
	}
	
	/**
	 * Waits for input to determine what color I should play as.
	 * @return my color
	 */
	public static String getMyColor(){
		String input = Board1d.getInput();
		
		if (input.equals("I B")) return "B";
		else if (input.equals("I W")) return "W";
		else {
			System.out.println("Bad input! (" + input + ")");
			System.exit(1);
		}
		
		return "";
	}
	
	/**
     * Prints the command line options.
     */
    public static void usage() {
        System.out.println("Command line options for OthelloDriver:");
        for (OPTIONS opt : OPTIONS.values()) {
            System.out.printf("\t -%s %s\n", opt, opt.getHelpString());
        }
        System.exit(0);
    }
	
	/**
     * Parses and set parameters according to the command line options in args.
     * See {@link OPTIONS}
     * 
     * @param args command line arguments
     */
    public static void parseCommandLine(String[] args) {
        int argPos = 0;
        boolean parseError = false;

        while ((argPos < args.length) && !parseError) {
            if (args[argPos].charAt(0) == '-') {
                switch (OPTIONS.valueOf(args[argPos].substring(1))) {
                    case h:
                        usage();
                        System.exit(0);
                    case l:
                    	LOCAL = true;
                    	break;
                    case m:
                        MANUAL = true;
                        break;
                    case vm:
                    	VERBOSEMOVES = true;
                    	break;

                }

            } else {
                System.out.printf("Error:  Invalid argument '%s'. \n",
                        args[argPos]);
                parseError = true;
            }
            argPos++;
        }
        if (parseError) {
            System.out.println("Error:  Invalid commandline options");
            usage();
            System.exit(-1);
        }

    }

}

package pasjans;

public class Areas {
	private int shiftXOfZeroBoardStack;
	private int shiftYOfZeroBoardStack;
	
	private int shiftXOfFirstBoardStack; 
	private int shiftYOfFirstBoardStack;

	public Areas() {
		shiftXOfZeroBoardStack = 9;
		shiftYOfZeroBoardStack = 10;
		
		shiftXOfFirstBoardStack = 99;
		shiftYOfFirstBoardStack = 179;
	}
	
	public boolean isActionOfBoardStack(GameBoard gameBoard, double x, double y, int numberOfStack){				
		if (	(x > shiftXOfFirstBoardStack + (numberOfStack-1) * 75) 
				&& (x < shiftXOfFirstBoardStack + (numberOfStack-1) * 75 + Karta.CARDWIDTH) 
				&& (y > shiftYOfFirstBoardStack + gameBoard.getSizeBoardStack(numberOfStack) * 30 - 30) 
				&& (y < shiftYOfFirstBoardStack + gameBoard.getSizeBoardStack(numberOfStack) * 30 + Karta.CARDHEIGHT - 30)) 
			return true;		
		return false;
	}
	
	public boolean isPressedKartyOdlozoneStack(GameBoard gameBoard, double x, double y){
		if ((x>shiftXOfZeroBoardStack) 
				&& (x<shiftXOfZeroBoardStack + Karta.CARDWIDTH) 
				&& (y > 10 + gameBoard.visibleCardsOnLeftSide*30 - 30) 
				&& (y< 10 + gameBoard.visibleCardsOnLeftSide*30 + Karta.CARDHEIGHT - 30)) 
			return true;		
		return false;
	}
	
	
}

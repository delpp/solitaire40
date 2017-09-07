package pasjans;

public class Areas {
	private static PointXY newGameButton;
	private static PointXY resetButton;
	private static PointXY undoButton;
	private static PointXY redoButton;
	private static PointXY stackStart;
	private static PointXY zeroBoardStack;
	private static PointXY firstBoardStack;
	private static PointXY firstFinishStack;	

	public Areas() {
		newGameButton = new PointXY(174, 30);
		resetButton = new PointXY(174, 115);	
		undoButton = new PointXY(700, 115);	
		redoButton = new PointXY(775, 115);
		stackStart = new PointXY(99, 10);
		zeroBoardStack = new PointXY(9, 10);
		firstBoardStack = new PointXY(99, 179);
		firstFinishStack = new PointXY(249, 10);
	}
	
	public boolean isActionOfNewGameButton(GameBoard gameBoard, double x, double y){
		if ((x>newGameButton.x) && (x<newGameButton.x + 67) 
				&& (y>newGameButton.y) && (y<newGameButton.y + 55)) return true;
		return false;
	}
	
	public boolean isActionOfResetButton(GameBoard gameBoard, double x, double y){
		if ((x>resetButton.x) && (x<resetButton.x + 67) 
				&& (y>resetButton.y) && (y<resetButton.y + 55)) return true;
		return false;
	}
	
	public boolean isActionOfUndoButton(GameBoard gameBoard, double x, double y){
		if ((x>undoButton.x) && (x<undoButton.x + 67) 
				&& (y>undoButton.y) && (y<undoButton.y + 55)) return true;
		return false;
	}
	
	public boolean isActionOfRedoButton(GameBoard gameBoard, double x, double y){
		if ((x>redoButton.x) && (x<redoButton.x + 67) 
				&& (y>redoButton.y) && (y<redoButton.y + 55)) return true;
		return false;
	}
	
	
	public boolean isActionOfStartStack(GameBoard gameBoard, double x, double y){
		if ((x>stackStart.x) && (x<stackStart.x + Karta.CARDWIDTH) 
				&& (y>stackStart.y) && (y<stackStart.y + Karta.CARDHEIGHT)) return true;		
		return false;
	}
	
	public boolean isActionOfBoardZeroStack(GameBoard gameBoard, double x, double y){
		if (	(x > zeroBoardStack.x) 
				&& (x < zeroBoardStack.x + Karta.CARDWIDTH) 
				&& (y > zeroBoardStack.y + gameBoard.visibleCardsOnLeftSide*30 - 30) 
				&& (y < zeroBoardStack.y + gameBoard.visibleCardsOnLeftSide*30 + Karta.CARDHEIGHT - 30)) 
			return true;		
		return false;
	}
	
	public boolean isActionOfBoardFrom1To10Stack(GameBoard gameBoard, double x, double y, int numberOfStack){				
		if (	(x > firstBoardStack.x + (numberOfStack-1) * 75) 
				&& (x < firstBoardStack.x + (numberOfStack-1) * 75 + Karta.CARDWIDTH) 
				&& (y > firstBoardStack.y + gameBoard.getSizeBoardStack(numberOfStack) * 30 - 30) 
				&& (y < firstBoardStack.y + gameBoard.getSizeBoardStack(numberOfStack) * 30 + Karta.CARDHEIGHT - 30)) 
			return true;		
		return false;
	}
	
	public boolean isActionOfFinishStack(GameBoard gameBoard, double x, double y, int numberOfStack){
		if (	(x > firstFinishStack.x + numberOfStack * 75) 
				&& (x < firstFinishStack.x + numberOfStack * 75 + Karta.CARDWIDTH) 
				&& (y > firstFinishStack.y) 
				&& (y < firstFinishStack.y + Karta.CARDHEIGHT)) 
			return true;		
		return false;
	}
	
}

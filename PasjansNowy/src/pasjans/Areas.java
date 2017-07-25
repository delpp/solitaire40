package pasjans;

public class Areas {
	private static PointXY stackStart;
	private static PointXY zeroBoardStack;
	private static PointXY firstBoardStack;
	private static PointXY firstFinishStack;	

	public Areas() {
		stackStart = new PointXY(99, 10);
		zeroBoardStack = new PointXY(9, 10);
		firstBoardStack = new PointXY(99, 179);
		firstFinishStack = new PointXY(249, 10);
	}
	
	public int isPressedStartStack(GameBoard gameBoard, double x, double y){
		if ((x>stackStart.x) && (x<stackStart.x + Karta.CARDWIDTH) 
				&& (y>stackStart.y) && (y<stackStart.y+Karta.CARDHEIGHT)) return -1;		
		return 1;
	}
	
	public boolean isPressedKartyOdlozoneStack(GameBoard gameBoard, double x, double y){
		if (	(x > zeroBoardStack.x) 
				&& (x < zeroBoardStack.x + Karta.CARDWIDTH) 
				&& (y > zeroBoardStack.y + gameBoard.visibleCardsOnLeftSide*30 - 30) 
				&& (y < zeroBoardStack.y + gameBoard.visibleCardsOnLeftSide*30 + Karta.CARDHEIGHT - 30)) 
			return true;		
		return false;
	}
	
	public boolean isActionOfBoardStack(GameBoard gameBoard, double x, double y, int numberOfStack){				
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

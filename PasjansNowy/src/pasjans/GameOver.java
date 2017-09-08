package pasjans;

public class GameOver {
	private static Karta cardOnHand;
	private int sourceStackNumberCardOnHand;
	
	public boolean isGameOver(GameBoard gameBoard){	
		if (gameBoard.getSizeStartStack() > 0) return false;
		if (isCardFromZeroFixToAnyOther(gameBoard)) return false;
						
		for (int i = 1; i < 11; i++) 	{				
			if (gameBoard.getSizeBoardStack(i) == 0) return false;
			if (isCardFromBoardFixToAnyOther(i, gameBoard)) return false;	
		}			
		return true;
	}
	
	public boolean isCardFromZeroFixToAnyOther(GameBoard gameBoard){
		if (gameBoard.getSizeBoardStack(0) > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			for (int i = 1; i <= 10; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "boardStack", gameBoard)) {
					cardOnHand = null;
					return true;	
				}
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack", gameBoard)) {
					cardOnHand = null;
					return true;			
				}
		}		
		cardOnHand = null;
		return false;
	}
	
	public boolean isCardFromBoardFixToAnyOther(int numberStack, GameBoard gameBoard){
		
		cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
		sourceStackNumberCardOnHand = numberStack;
		
		System.out.println("Testuję kartę: " + cardOnHand.getCard() + " ze stosu: " + sourceStackNumberCardOnHand  + ". Jego aktualny stan: " + gameBoard.getSizeBoardStack(sourceStackNumberCardOnHand) + " kart");
			
		for (int j = 0; j < 8; j++){
			if (isCompatibilityCardOnStackAndOnHand(j, "finishStack", gameBoard)) {
				System.out.println("Karta ze stoku " + sourceStackNumberCardOnHand + " jest kompatybilna z kartą ze stoku górnego: " + j);
				gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
				cardOnHand = null;
				return true;
			}
		}	
		
		for (int numberOfBoardStack = 1; numberOfBoardStack < 11; numberOfBoardStack++){	
			if (numberOfBoardStack == sourceStackNumberCardOnHand) continue;

			System.out.println("Sprawdzam kartę ze stosu " + numberOfBoardStack);
				
			if (isCompatibilityCardOnStackAndOnHand(numberOfBoardStack, "boardStack", gameBoard)) {
				
				System.out.println("Karta ze stoku " + sourceStackNumberCardOnHand + " " + cardOnHand.getCard() + " jest kompatybilna z kartą ze stoku: " + numberOfBoardStack);
				
				if (!isCompatibilityCardOnStackAndOnHand(sourceStackNumberCardOnHand, "boardStack", gameBoard)){
					System.out.println("Karta nie może wrócić na swoje miejsce");
					gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
					cardOnHand = null;
					return true;
				}	
				else {
					if (gameBoard.getSizeBoardStack(sourceStackNumberCardOnHand) > 0){
						
					//	if (isCardFromBoardFixToAnyOther)
							
							
						System.out.println("Karta może wrócić na swoje miejsce. Ten układ nie jest brany pod uwagę do kontynuacji gry");
						gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
						cardOnHand = null;
						return false;
					}
					else {
						gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
						cardOnHand = null;
						return true;
					}
				}
			}
		}		
	
		gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
		cardOnHand = null;
		return false;
	}
	
	
	public boolean isCompatibilityCardOnStackAndOnHand(int stackNumber, String stackType, GameBoard gameBoard){
		if ((stackType.equals("boardStack")) && (gameBoard.getSizeBoardStack(stackNumber)==0)) return true;
		
		if (stackType.equals("boardStack")) 
				return cardOnHand.canPutCardToDecreaseStack(gameBoard.readCardFromStack(stackType, stackNumber));
			else 
				return cardOnHand.canPutCardToIncreaseStock(gameBoard.readCardFromStack(stackType, stackNumber));		
	}
}

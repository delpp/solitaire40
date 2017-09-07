package pasjans;

public class PossibleMoves {
	private static Karta cardOnHand;
	private int sourceStackNumberCardOnHand;
	
	
	public int count (GameBoard gameBoard){	
		int countOfPossibilityMoves = 0;
		int count = 0;

		if (gameBoard.getSizeStartStack() > 0) {
			countOfPossibilityMoves = 1;
			//System.out.println("Można wziąć kartę ze stosu startowego");
		}
		count = possibleMovesFromZeroToAnyOther(gameBoard);
		if (count > 0) 
			countOfPossibilityMoves += count;	
		
		for (int numberBoardStack = 1; numberBoardStack < 11; numberBoardStack++) 	{				
			if (gameBoard.getSizeBoardStack(numberBoardStack) == 0) 
				continue;
			count = possibleMovesFromBoardToAnyOther(numberBoardStack, gameBoard);
			if (count > 0) 
				countOfPossibilityMoves += count;
		}	
				
		//System.out.println("Liczba możliwych ruchów na tym poziomie: " + countOfPossibilityMoves);
		return countOfPossibilityMoves;
	}
	
	public int possibleMovesFromZeroToAnyOther(GameBoard gameBoard){
		int count = 0;
		
		if (gameBoard.getSizeBoardStack(0) > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack", gameBoard)) 	{
					//System.out.println("Można wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Final numer: " + i);
					count++;
					break;
				}
			
			for (int i = 1; i <= 10; i++) 
				if (cardOnHand.getCardNumber() != 1)	
					if (isCompatibilityCardOnStackAndOnHand(i, "boardStack", gameBoard)) 	{
						//System.out.println("Można wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Board numer: " + i);
						count++;
					}			
		}
		cardOnHand = null;
		return count;
	}
	
	public int possibleMovesFromBoardToAnyOther(int numberStack, GameBoard gameBoard){		
		int count = 0;
		cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
		sourceStackNumberCardOnHand = numberStack;
		
		for (int j = 0; j < 8; j++){
			if (isCompatibilityCardOnStackAndOnHand(j, "finishStack", gameBoard)) {
				//System.out.println("Można wziąć ze stosu Board numer: "  + sourceStackNumberCardOnHand + " kartę " + cardOnHand + " i położyć na stos Finish numer: " + j);
				
				count++;
				break;
			}
		}
		
		for (int numberOfBoardStack = 1; numberOfBoardStack < 11; numberOfBoardStack++){	
			if (numberOfBoardStack == numberStack) continue;
				
			if (cardOnHand.getCardNumber() != 1)
			if (isCompatibilityCardOnStackAndOnHand(numberOfBoardStack, "boardStack", gameBoard)) 			
				if (!isCompatibilityCardOnStackAndOnHand(sourceStackNumberCardOnHand, "boardStack", gameBoard))
				{
					System.out.println("Można wziąć ze stosu Board numer: "  + sourceStackNumberCardOnHand + " kartę " + cardOnHand + " i położyć na stos Board numer: " + numberOfBoardStack);
					count++;
				}
			/*	else 
					if (gameBoard.getSizeBoardStack(sourceStackNumberCardOnHand) == 0)		{	
						System.out.println("Można wziąć ze stosu Board numer: "  + sourceStackNumberCardOnHand + " kartę " + cardOnHand + " i położyć na stos Board numer: " + numberOfBoardStack);					
						count++;	
					}*/
		}
				
		gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
		cardOnHand = null;
		sourceStackNumberCardOnHand = -1;
		return count;
	}


	
	public boolean isCompatibilityCardOnStackAndOnHand(int stackNumber, String stackType, GameBoard gameBoard){
		if ((stackType.equals("boardStack")) && (gameBoard.getSizeBoardStack(stackNumber) == 0)) return true;
		
		if (stackType.equals("boardStack")) 
				return cardOnHand.canPutCardToDecreaseStack(gameBoard.readCardFromStack(stackType, stackNumber));
			else 
				return cardOnHand.canPutCardToIncreaseStock(gameBoard.readCardFromStack(stackType, stackNumber));		
	}
	
	
	
}

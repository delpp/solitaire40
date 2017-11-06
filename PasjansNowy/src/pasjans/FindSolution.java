package pasjans;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FindSolution {
	private static Karta cardOnHand;
	private int deepSteps;
	private int sprawdzoneDostepneRuchy = 0;
	private boolean koniecTestu;
	private boolean zrobionoRuchKartZBoard_1_10 = false;
	private boolean zrobionoRuch = false;
	private boolean przelozonoAsyLubDwojki;
	private long calkowitaLiczbaPrzetestowanychKombinacji;
	private UndoStep step;
	private int sourceStackNumberCardOnHand;
	private Set<GameBoard> possibleLayoutsOfCards;
	private GameBoard copyGameBoard;
	
	
	public void checkSolutions(GameBoard gameBoard, PossibleMoves possibleMoves){
		possibleLayoutsOfCards = new HashSet();
		deepSteps = 0;
		calkowitaLiczbaPrzetestowanychKombinacji = 0;
		gameBoard.ruchyJuzWykonane = 0;	
		przelozonoAsyLubDwojki = false;
		koniecTestu = false;
		
		do {
			przelozonoAsyLubDwojki = przelozAsaLubDwojkeZBoardZero(gameBoard);
		}
		while (przelozonoAsyLubDwojki);
	
		do {
			przelozonoAsyLubDwojki = przelozAsaLubDwojkeZBoard(gameBoard);
		}
		while (przelozonoAsyLubDwojki);
	
	gameBoard.possibleMoves = possibleMoves.count(gameBoard);
	
	System.out.println("Całkowita Liczba Przetestowanych Kombinacji przed pętlą: " + calkowitaLiczbaPrzetestowanychKombinacji + "\n");
	System.out.println("Liczba możliwych ruchów na tym poziomie: " + gameBoard.possibleMoves);
	czekajNaEnter();
	do 			
	{		
		System.out.println("WEWNATRZ PETLI");
		System.out.println("gameBoard.ruchyJuzWykonane: " + gameBoard.ruchyJuzWykonane);
		System.out.println("gameBoard.possibleMoves: " + gameBoard.possibleMoves);
		
		if (gameBoard.ruchyJuzWykonane < gameBoard.possibleMoves){
			
			zrobionoRuch = zrobKrokZStosuStartowego(gameBoard, possibleMoves);
			
			/*do {
				zrobionoRuch = zrobKrokZStosuStartowego(gameBoard, possibleMoves);
				przelozonoAsyLubDwojki = przelozAsaLubDwojkeZBoardZero(gameBoard);
			}
			while (przelozonoAsyLubDwojki);*/
			
			
			zrobionoRuch = zrobKrokZeStosuZeroBoard(gameBoard, possibleMoves);
			
			zrobKrokZeStosuBoard(gameBoard, possibleMoves);
			
			calkowitaLiczbaPrzetestowanychKombinacji++;
			System.out.println("Całkowita Liczba Przetestowanych Kombinacji: " + calkowitaLiczbaPrzetestowanychKombinacji);
			
			gameBoard.possibleMoves = possibleMoves.count(gameBoard);
			gameBoard.ruchyJuzWykonane = 0;
		}
		else 
			if ((gameBoard.ruchyJuzWykonane == gameBoard.possibleMoves) && (deepSteps > 0)){						
				zrobUndo(gameBoard);	
			}			
		sprawdzoneDostepneRuchy = 0;
		
		System.out.println("Deep steps: " + deepSteps + "\n");
		
		if (deepSteps == 0) 
			if (gameBoard.ruchyJuzWykonane == gameBoard.possibleMoves){
				System.out.println("koniecTestu = true. Deep steps: " + deepSteps + " Ruchy już wykonanane: " + gameBoard.ruchyJuzWykonane + ". Możliwe ruchy: " + gameBoard.possibleMoves);
				koniecTestu = true;
				czekajNaEnter();
			}
		if ((calkowitaLiczbaPrzetestowanychKombinacji == 20) || (calkowitaLiczbaPrzetestowanychKombinacji == 20)) czekajNaEnter();
	} 
	while (!koniecTestu);
	
	System.out.println("WYJSCIE. Deep steps: " + deepSteps + " Ruchy już wykonanane: " + gameBoard.ruchyJuzWykonane + ". Możliwe ruchy: " + gameBoard.possibleMoves);
	
};

	public boolean przelozAsaLubDwojkeZBoardZero(GameBoard gameBoard) {
		if (gameBoard.getSizeZeroBoardStack() > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			if ((cardOnHand.getCardNumber() == 1) || (cardOnHand.getCardNumber() == 2)){
				for (int i = 0; i < 8; i++)
					if (isCompatibilityCardOnStackAndOnHand(i, "finishStack", gameBoard)) 	{
						cardOnHand = gameBoard.getCardFromZeroBoardStack();
						gameBoard.pushCardToStack("finishStack", i, cardOnHand);
						sprawdzoneDostepneRuchy = 1;					
						gameBoard.ruchyJuzWykonane = 1;
						step = new UndoStep(cardOnHand, 0, "finishStack", i, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
						gameBoard.pushCardToStack("finishStack", i, cardOnHand);
						gameBoard.pushUndo(step);	
						deepSteps ++;
						gameBoard.ruchyJuzWykonane = 0;
						System.out.println("Przelozono " + cardOnHand + " na board final");
						//czekajNaEnter();
						cardOnHand = null;
						return true;
					}
			}
		}		
		return false;
	}
	
	public boolean przelozAsaLubDwojkeZBoard(GameBoard gameBoard) {
		for (int numberStack = 1; numberStack < 11; numberStack++){
		
			if (gameBoard.getSizeBoardStack(numberStack) > 0){
				cardOnHand = gameBoard.readCardFromStack("boardStack", numberStack);
				if ((cardOnHand.getCardNumber() == 1) || (cardOnHand.getCardNumber() == 2)){
					for (int i = 0; i < 8; i++)
						if (isCompatibilityCardOnStackAndOnHand(i, "finishStack", gameBoard)) 	{
							cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
							gameBoard.pushCardToStack("finishStack", i, cardOnHand);
							sprawdzoneDostepneRuchy = 1;					
							gameBoard.ruchyJuzWykonane = 1;
							step = new UndoStep(cardOnHand, numberStack, "finishStack", i, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
							gameBoard.pushCardToStack("finishStack", i, cardOnHand);
							gameBoard.pushUndo(step);	
							deepSteps ++;
							gameBoard.ruchyJuzWykonane = 0;
							System.out.println("Prze�o�ono " + cardOnHand + " z " + numberStack + " na board final");
							//czekajNaEnter();
							cardOnHand = null;
							return true;
						}
				}
			}	
		
		}
		return false;
	}
	
	public boolean zrobKrokZStosuStartowego(GameBoard gameBoard, PossibleMoves possibleMoves){
		System.out.println("Przechodzę przez Start Stack");
		if (gameBoard.getSizeStartStack() > 0) {
			System.out.println("Mozna zrobic krok z stosu startowego. Rozmiar stosu startowego: " + gameBoard.getSizeStartStack());
			sprawdzoneDostepneRuchy++;
			//System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + ". Ruchy już wykonane: " + gameBoard.ruchyJuzWykonane);
		
			if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
				System.out.println("Liczba ruchów wykonanych w tym układzie = 0. Stos startowy = " + gameBoard.getSizeStartStack() + " . Robię kolejny krok: biorę nową kartę ze stosu startowego: " + gameBoard.readCardFromStartStack());		
				gameBoard.ruchyJuzWykonane++;	
				step = new UndoStep(gameBoard.readCardFromStartStack(), -1, "boardStack", 0, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
				gameBoard.pushCardToStack("boardStack", 0, gameBoard.getCardFromStartStack());	
				gameBoard.pushUndo(step);	
				deepSteps ++;
				gameBoard.ruchyJuzWykonane = 0;
				gameBoard.possibleMoves = possibleMoves.count(gameBoard);
				cardOnHand = null;
				
				possibleLayoutsOfCards.add((GameBoard) gameBoard.clone());
				
				return true;
			}
		}	
		return false;
		//System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + " Ruchy już wykonane na tym poziomie: " + gameBoard.ruchyJuzWykonane);

	}
	
	public boolean zrobKrokZeStosuZeroBoard(GameBoard gameBoard, PossibleMoves possibleMoves){
		
		System.out.println("Przechodzę przez Board 0");
		//System.out.println("Rozmiar Stosu Board 0: " + gameBoard.getSizeZeroBoardStack());
		
		if (gameBoard.getSizeZeroBoardStack() > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack", gameBoard)) 	{
					//System.out.println("MOŻNA wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Final numer: " + i);
					sprawdzoneDostepneRuchy++;
					if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
						cardOnHand = gameBoard.getCardFromZeroBoardStack();
						
						gameBoard.ruchyJuzWykonane++;
						step = new UndoStep(cardOnHand, 0, "finishStack", i, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
						gameBoard.pushCardToStack("finishStack", i, cardOnHand);
						gameBoard.pushUndo(step);	
						deepSteps ++;
						gameBoard.ruchyJuzWykonane = 0;
						System.out.println("Robię kolejny krok: biorę kartę ze stosu zerowego kartę " + cardOnHand + " i kładę ją na stos Finish numer: " + i);
						
						gameBoard.possibleMoves = possibleMoves.count(gameBoard);
						cardOnHand = null;
						return true;
					}		
				}
			
			
			for (int i = 1; i <= 10; i++) 
				if (cardOnHand.getCardNumber() != 1)
					if (isCompatibilityCardOnStackAndOnHand(i, "boardStack", gameBoard)) 	{
						//System.out.println("MOŻNA wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Board numer: " + i);
						sprawdzoneDostepneRuchy++;
					
						System.out.println("SPRAWDZONE dostępne ruchy : " + sprawdzoneDostepneRuchy + " Ruchy już wykonane: " + gameBoard.ruchyJuzWykonane + ". Mozliwe ruchy: " + gameBoard.possibleMoves);
						if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
							cardOnHand = gameBoard.getCardFromZeroBoardStack();
							
							cardOnHand.setSourceStack(0);
							
							System.out.println("Stos: 0. " + "Wzięta z niego karta: " + cardOnHand + ". Karta pod spodem: " + gameBoard.readCardFromStack("boardStack", 0));
							
							gameBoard.ruchyJuzWykonane++;
							step = new UndoStep(cardOnHand, 0, "boardStack", i, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
							gameBoard.pushCardToStack("boardStack", i, cardOnHand);
							gameBoard.pushUndo(step);	
							deepSteps ++;
							gameBoard.ruchyJuzWykonane = 0;
							System.out.println("Robię kolejny krok: biorę kartę ze stosu zerowego kartę " + cardOnHand + " i kładę ją na stos Board numer: " + i);
							
							gameBoard.possibleMoves = possibleMoves.count(gameBoard);
							cardOnHand = null;
							return true;
						}
					}		
			
		}
	return false;
		//System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + " Ruchy już wykonane na tym poziomie: " + gameBoard.ruchyJuzWykonane);
	}
	
	
	public boolean zrobKrokZeStosuBoard(GameBoard gameBoard, PossibleMoves possibleMoves){
		boolean dokonanoRuchNaFinishBoard = false;
		
		for (int numberStack = 1; numberStack < 11; numberStack++){
			
			if (gameBoard.getSizeBoardStack(numberStack) > 0){
				cardOnHand = gameBoard.readCardFromStack("boardStack", numberStack);
				sourceStackNumberCardOnHand = numberStack;
				
				for (int numberOfFinishStack = 0; numberOfFinishStack < 8; numberOfFinishStack++){
					if (isCompatibilityCardOnStackAndOnHand(numberOfFinishStack, "finishStack", gameBoard)) {
						//System.out.println("Można wziąć ze stosu Board numer: "  + sourceStackNumberCardOnHand + " kartę " + cardOnHand + " i położyć na stos Finish numer: " + numberOfFinishStack);
						
						sprawdzoneDostepneRuchy++;
						if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
							cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
							
							if (gameBoard.getSizeBoardStack(numberStack)>0){
								cardOnHand.setSourceStack(numberStack);
								cardOnHand.setPositionOnStack(gameBoard.getSizeBoardStack(numberStack));
								cardOnHand.setCardUnder(gameBoard.readCardFromStack("boardStack", numberStack));
							}														
								else {
									cardOnHand.setSourceStack(numberStack);
									cardOnHand.setPositionOnStack(gameBoard.getSizeBoardStack(numberStack));
									cardOnHand.setCardUnder(null);
								}
							
							System.out.println("");
							System.out.println("Stos: " + cardOnHand.readSourceStack() +  ". Wzięta z niego karta: " + cardOnHand + ". Karta pod spodem: " + cardOnHand.readCardUnder() + ". Liczba pozostałych kart na stosie: " + cardOnHand.readPositionOnStack());
							
							gameBoard.ruchyJuzWykonane++;
							
							System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + ". Ruchy już wykonane na tym poziomie: " + gameBoard.ruchyJuzWykonane + ". Mozliwe ruchy: " + gameBoard.possibleMoves);	
							
							step = new UndoStep(cardOnHand, numberStack, "finishStack", numberOfFinishStack, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
							gameBoard.pushCardToStack("finishStack", numberOfFinishStack, cardOnHand);
							gameBoard.pushUndo(step);	
							deepSteps ++;
							gameBoard.ruchyJuzWykonane = 0;
							
							System.out.println("ROBIE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Finish numer: " + numberOfFinishStack);
							dokonanoRuchNaFinishBoard = true;
							gameBoard.possibleMoves = possibleMoves.count(gameBoard);
							cardOnHand = null;
							sourceStackNumberCardOnHand = -1;
							return true;	
						}
					}
				}
				
				if (dokonanoRuchNaFinishBoard == false)
				for (int numberOfBoardStack = 1; numberOfBoardStack < 11; numberOfBoardStack++){	
					if (numberOfBoardStack == numberStack) continue;
					
					if (cardOnHand.getCardNumber() != 1)
						if (isCompatibilityCardOnStackAndOnHand(numberOfBoardStack, "boardStack", gameBoard)) {
							
							
							cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
							
							if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
								
								gameBoard.ruchyJuzWykonane++;
								step = new UndoStep(cardOnHand, numberStack, "boardStack", numberOfBoardStack, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
								gameBoard.pushCardToStack("boardStack", numberOfBoardStack, cardOnHand);
								gameBoard.pushUndo(step);	
								deepSteps ++;
								gameBoard.ruchyJuzWykonane = 0;
								System.out.println("ROBIE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Board numer: " + numberOfBoardStack);
								
								gameBoard.possibleMoves = possibleMoves.count(gameBoard);
								cardOnHand = null;
								sourceStackNumberCardOnHand = -1;
								return true;
							}
							
							/*if (!isCompatibilityCardOnStackAndOnHand(sourceStackNumberCardOnHand, "boardStack", gameBoard)){
								
								//System.out.println("Żródłowy stos: " + sourceStackNumberCardOnHand + " karty w ręku: " + cardOnHand + ". Czy karta może wrócić na swoje źródło: " + isCompatibilityCardOnStackAndOnHand(sourceStackNumberCardOnHand, "boardStack"));
								
								sprawdzoneDostepneRuchy++;							
								if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
									
									gameBoard.ruchyJuzWykonane++;
									step = new UndoStep(cardOnHand, numberStack, "boardStack", numberOfBoardStack, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
									gameBoard.pushCardToStack("boardStack", numberOfBoardStack, cardOnHand);
									gameBoard.pushUndo(step);	
									deepSteps ++;
									gameBoard.ruchyJuzWykonane = 0;
									System.out.println("ROBIE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Board numer: " + numberOfBoardStack);
									
									gameBoard.possibleMoves = possibleMoves.count(gameBoard);
									cardOnHand = null;
									sourceStackNumberCardOnHand = -1;
									return true;
								}	
							}*/
							/*else 
								if (gameBoard.getSizeBoardStack(sourceStackNumberCardOnHand) == 0)		{	
									sprawdzoneDostepneRuchy++;
									if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
										//cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
										gameBoard.ruchyJuzWykonane++;
										step = new UndoStep(cardOnHand, numberStack, "boardStack", numberOfBoardStack, gameBoard.ruchyJuzWykonane, gameBoard.possibleMoves);
										gameBoard.pushCardToStack("boardStack", numberOfBoardStack, cardOnHand);
										gameBoard.pushUndo(step);	
										deepSteps ++;
										gameBoard.ruchyJuzWykonane = 0;
										System.out.println("ROBIEE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Board numer: " + numberOfBoardStack);
										
										gameBoard.possibleMoves = possibleMoves.count(gameBoard);
										cardOnHand = null;
										sourceStackNumberCardOnHand = -1;
										return true;	
									}	
								}*/
							gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
						}
							
				}
			
			}
			//gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
			cardOnHand = null;
			sourceStackNumberCardOnHand = -1;
		}
		return false;
	}
	
	
	public void zrobUndo(GameBoard gameBoard){
		System.out.println("Robię UNDO");
		gameBoard.undoStep();	
		deepSteps--;
		gameBoard.countVisibleCardsOnLeftSide();
	}
	

	public boolean isCompatibilityCardOnStackAndOnHand(int stackNumber, String stackType, GameBoard gameBoard){
		if ((stackType.equals("boardStack")) && (gameBoard.getSizeBoardStack(stackNumber) == 0)) return true;
		
		if (stackType.equals("boardStack")) 
				return cardOnHand.canPutCardToDecreaseStack(gameBoard.readCardFromStack(stackType, stackNumber));
			else 
				return cardOnHand.canPutCardToIncreaseStock(gameBoard.readCardFromStack(stackType, stackNumber));		
	}

	public void czekajNaEnter(){
		System.out.println("Wcisniej ENTER");
		Scanner skaner = new Scanner(System.in);
		String a = skaner.nextLine();
	}
}

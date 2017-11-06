package pasjans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class GameBoard implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final int STOCKSIZE = 104;	
	private ArrayList<Karta> taliaStart = new ArrayList<Karta>(STOCKSIZE);
	
	private Stack<Karta> startStack = new Stack<Karta>();
	private Stack<Karta> zeroBoardStack = new Stack<Karta>();
	private Stack<Karta>[] boardStack = new Stack[11];
	private Stack<Karta>[] finishStack = new Stack[8];
	
	private final Random random;
	private String[] cardColor = {"trefl", "kier", "pik", "karo"};
	
	public ArrayList<UndoStep> listUndoSteps = new ArrayList<UndoStep>();
	public int positionInUndoList;
	
	private UndoStep step = new UndoStep();
	
	public int ruchyJuzWykonane;
	public int possibleMoves;
	
	public int visibleCardsOnLeftSide;

	public GameBoard() {
		random = new Random();	
		createFinishStacks();
		createBoardStacks();
		positionInUndoList = 0;
	}
	
	public void run(){
		visibleCardsOnLeftSide = 0;
		shuffleCards();
		distributeCards();	
		addToFinishStackOneStartCard();
	}
	
	public void shuffleCards(){
		int counter = 104;		
		fillCards();			
		for (int i = 1; i <= 104; i++){
			int index = random.nextInt(counter); // losuje od 0 do counter, czyli losuje indeks do listy: taliaStart
						
			startStack.push(taliaStart.get(index)); // pobiera wylosowana karte i kladzie na stosie startowym
			if (i==1) System.out.println(startStack.peek().getCard());
			
			taliaStart.remove(index); // usuwa wylosowaną kartę z puli kart do losowania		
			counter--; // zmniejsza zakres kart do losowania o 1
		}
	}
	
	public void fillCards(){
	// wstawia do listy dwie talie kart
		for (int j=0; j <=1; j++){ // dwie talie
			for (int i=0; i<4; i++){ // wypełnia listę kartami z jednej talii
				for (int n=1; n <=13; n++) // wypełnia listę kartami z jednego koloru
					taliaStart.add(new Karta(n, cardColor[i]));							
			}
		}
	}
	
	 public void distributeCards(){			
		for (int i = 1; i < 11; i++)			
			for (int liczbaKartNaKazdymStosie = 1; liczbaKartNaKazdymStosie <= 4; liczbaKartNaKazdymStosie++)
				boardStack[i].push(getCardFromStartStack());				
	}
	 
	public void addToFinishStackOneStartCard(){
		for (int j = 0; j <= 1 ; j++)
			for (int i = 0; i < 4; i++) 
				finishStack[j*4 + i].add(new Karta(0, cardColor[i]));
	}
	
	
	// ustala ile ma być pokazanych kart ze stosu KartyOdlozone - max. 10
	public void countVisibleCardsOnLeftSide(){	
		if (getSizeZeroBoardStack() < 10)
			visibleCardsOnLeftSide = getSizeZeroBoardStack();
		else visibleCardsOnLeftSide = 10;
	}	
	
	public void pushUndo(UndoStep step){
/*		System.out.println("Pozycja w liście UNDO przed dodaniem ruchu: " + positionInUndoList);
		System.out.println("Rozmiar listy UNDO przed dodaniem ruchu: " + listUndoSteps.size());*/
		while (positionInUndoList < listUndoSteps.size())
			listUndoSteps.remove(listUndoSteps.size()-1);
		listUndoSteps.add(step);
		positionInUndoList++;
/*		System.out.println("Pozycja w liście UNDO po dodaniu ruchu: " + positionInUndoList );
		System.out.println("Rozmiar listy UNDO po dodaniu ruchu: " + listUndoSteps.size() + "\n");*/
	}
	
	public int getSizeZeroBoardStack(){
		return zeroBoardStack.size();
	}
	
	public int readCountUndoSteps(){
		return listUndoSteps.size();
	}
	
	public int readPositionInUndoList(){
		return positionInUndoList;
	}
	
	private UndoStep getUndo(){
		step = listUndoSteps.get(positionInUndoList-1);
		return step;
	}
	
	public Karta getCardFromUndo(){
		step = listUndoSteps.get(positionInUndoList-1);	
		return step.card;
	}
	
	public void resetUndoSteps(){
		listUndoSteps.clear();
		positionInUndoList = 0;
	}
	
	public void undoStep(){
		step = getUndo();
		if (step.numberSource >= 0) {
			if (step.typeTarget.equals("boardStack")) {
				getCardFromBoardStack(step.numberTarget);
			}
			else if (step.typeTarget.equals("finishStack")) getCardFromFinishStack(step.numberTarget);
				pushCardToStack("boardStack", step.numberSource, step.card);			
			}
			else if (step.numberSource < 0){
				getCardFromZeroBoardStack();
				pushCardToStartStack(step.card);				
			}
		ruchyJuzWykonane = step.ruchyJuzWykonane;
		possibleMoves = step.mozliweRuchy;
		positionInUndoList--;
	}
	
	public void redoStep(){
		if (positionInUndoList < listUndoSteps.size()){
			step = getUndo();
			if (step.numberSource >= 0) {
				if (step.typeTarget.equals("boardStack")) {
					getCardFromBoardStack(step.numberTarget);
				}
				else if (step.typeTarget.equals("finishStack")) getCardFromFinishStack(step.numberTarget);
					pushCardToStack("boardStack", step.numberSource, step.card);
				
			}
			else if (step.numberSource < 0){
					getCardFromZeroBoardStack();
					pushCardToStartStack(step.card);				
				}
		}
		positionInUndoList++;
	}
	
	public int getPozXSourceUndo(){
		step = listUndoSteps.get(positionInUndoList-1);
		if (step.typeTarget.equals("boardStack")) return step.numberTarget*75+24;
			else if (step.typeTarget.equals("finishStack")) return step.numberTarget*75+249;	
		return 9;
	}
	
	public int getPozYSourceUndo(){
		step = listUndoSteps.get(positionInUndoList-1); 
		if (step.typeTarget.equals("finishStack")) return 10;		
		if (step.typeTarget.equals("boardStack"))
			if (step.numberTarget == 0) {
				if (getSizeZeroBoardStack() >= 10) return 280;
				else if (getSizeZeroBoardStack() < 10) return 10 + getSizeZeroBoardStack()*30-30;
			}		
		return 179 + 30 *  getSizeBoardStack(step.numberTarget)-30;
	}
	
	public int getPozXTargetUndo(){
		step = listUndoSteps.get(positionInUndoList-1);
		if (step.numberSource > 0) return step.numberSource*75+24;
			else if (step.numberSource == 0) return 9;
		return 99;
	}
	
	public int getPozYTargetUndo(){
		step = listUndoSteps.get(positionInUndoList-1);
		if (step.numberSource > 0) return 179 + 30 * getSizeBoardStack(step.numberSource);
			else if (step.numberSource == 0) {
				if (getSizeZeroBoardStack() < 10) return 10 + getSizeZeroBoardStack()*30;
					else return 280; 
			}
		return 10;
	}
	
	public void createFinishStacks(){
		for (int i = 0; i < 8; i++) finishStack[i] = new Stack<Karta>();
	}
	
	public void createBoardStacks(){
		for (int i = 0 ; i < 11; i++) boardStack[i] = new Stack<Karta>();
	}
	
	public void removeAllCardFromStartStack(){
		startStack.removeAllElements();
	}
	
	public void removeAllCardFromZeroBoardStack(){
		zeroBoardStack.removeAllElements();
	}
	
	public void removeAllCardFromBoardStack(){
		for (int i = 0 ; i < 11; i++) boardStack[i].removeAllElements();
	}
	
	public void removeAllCardFromFinishStack(){
		for (int i = 0; i < 8; i++) finishStack[i].removeAllElements();
	}
	 
	public int getSizeStartStack(){
		return startStack.size();
	}
	
	public Karta getCardFromStartStack(){
		return startStack.pop();
	}
	
	public Karta readCardFromStartStack(){
		return startStack.peek();
	}
	
	public void pushCardToStartStack(Karta card){
		startStack.push(card);
	}
	
	
	
	public int getSizeBoardStack(int numberOfStack){
		return boardStack[numberOfStack].size();
	}
	
	public Karta getCardFromBoardStack(int numberOfStack){
		return boardStack[numberOfStack].pop();
	}
	
	
	public int getSizeFinishStack(int numberOfStack){
		return finishStack[numberOfStack].size();
	}
	
	public Karta getCardFromFinishStack(int numberOfStack){
		return finishStack[numberOfStack].pop();
	}
	
	public Karta getCardFromZeroBoardStack(){
		return zeroBoardStack.pop();
	}

	
	public Karta readCardFromStack(String stackType, int numberStack){
		if (stackType.equals("boardStack")) {
			if (numberStack == 0) return zeroBoardStack.peek();
			else return boardStack[numberStack].peek();
		}
			else if (stackType.equals("finishStack")) return finishStack[numberStack].peek();
		return null;
	}
	
	public void pushCardToStack(String stackType, int numberStack, Karta card){		
		if (stackType.equals("boardStack")) 	{
			if (numberStack == 0) zeroBoardStack.push(card);
			else boardStack[numberStack].push(card);	
		}
			else if (stackType.equals("finishStack")) 	finishStack[numberStack].push(card);
	}
	
	
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(boardStack);
		result = prime * result + Arrays.hashCode(finishStack);
		result = prime * result + ((startStack == null) ? 0 : startStack.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameBoard other = (GameBoard) obj;
		if (!Arrays.equals(boardStack, other.boardStack))
			return false;
		if (!Arrays.equals(finishStack, other.finishStack))
			return false;
		if (startStack == null) {
			if (other.startStack != null)
				return false;
		} else if (!startStack.equals(other.startStack))
			return false;
		return true;
	}

	@Override
	public Object clone() {
			GameBoard copyGameBoard = new GameBoard();
			copyGameBoard.startStack = (Stack<Karta>) this.startStack.clone();
			
			for (int i = 0; i < 11; i++)
				copyGameBoard.boardStack[i] = (Stack<Karta>) this.boardStack[i].clone();
				
			for (int i = 0; i < 8; i++) 
				copyGameBoard.finishStack[i] = (Stack<Karta>) this.finishStack[i].clone();
					
			copyGameBoard.listUndoSteps = (ArrayList<UndoStep>) this.listUndoSteps.clone();
			return copyGameBoard;
	}

}

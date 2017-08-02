package pasjans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameBoard implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final int STOCKSIZE = 104;	
	private ArrayList<Karta> taliaStart = new ArrayList<Karta>(STOCKSIZE);
	
	private Stack<Karta> startStack = new Stack<Karta>();
	private Stack<Karta>[] boardStack = new Stack[11];
	private Stack<Karta>[] finishStack = new Stack[8];
	
	private final Random random;
	private String[] cardColor = {"trefl", "kier", "pik", "karo"};
	
	public ArrayList<UndoStep> listUndoSteps = new ArrayList<UndoStep>();
	private UndoStep step = new UndoStep();
	
	public int ruchyJuzWykonane;
	public int mozliweRuchy;
	public boolean zrobionoRuchKartyZBoard_0;
	
	public int visibleCardsOnLeftSide;

	public GameBoard() {
		random = new Random();	
		createFinishStacks();
		createBoardStacks();
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
		if (getSizeBoardStack(0) < 10)
			visibleCardsOnLeftSide = getSizeBoardStack(0);
		else visibleCardsOnLeftSide = 10;
	}	
	
	public void pushUndo(UndoStep step){
		listUndoSteps.add(step);
	}
	
	public int readCountUndoSteps(){
		return listUndoSteps.size();
	}
	
	private UndoStep getUndo(){
		step = listUndoSteps.get(listUndoSteps.size()-1);
		listUndoSteps.remove(listUndoSteps.size()-1);
		return step;
	}
	
	public Karta getCardFromUndo(){
		step = listUndoSteps.get(listUndoSteps.size()-1);	
		return step.card;
	}
	
	public int getPozXSourceUndo(){
		step = listUndoSteps.get(listUndoSteps.size()-1);
		if (step.typeTarget.equals("boardStack")) return step.numberTarget*75+24;
			else if (step.typeTarget.equals("finishStack")) return step.numberTarget*75+249;	
		return 9;
	}
	
	public int getPozYSourceUndo(){
		step = listUndoSteps.get(listUndoSteps.size()-1); 
		if (step.typeTarget.equals("finishStack")) return 10;		
		if (step.typeTarget.equals("boardStack"))
			if (step.numberTarget == 0) {
				if (getSizeBoardStack(0) >= 10) return 280;
				else if (getSizeBoardStack(0) < 10) return 10 + getSizeBoardStack(0)*30-30;
			}		
		return 179 + 30 *  getSizeBoardStack(step.numberTarget)-30;
	}
	
	public int getPozXTargetUndo(){
		step = listUndoSteps.get(listUndoSteps.size()-1);
		if (step.numberSource > 0) return step.numberSource*75+24;
			else if (step.numberSource == 0) return 9;
		return 99;
	}
	
	public int getPozYTargetUndo(){
		step = listUndoSteps.get(listUndoSteps.size()-1);
		if (step.numberSource > 0) return 179 + 30 * getSizeBoardStack(step.numberSource);
			else if (step.numberSource == 0) {
				if (getSizeBoardStack(0) < 10) return 10 + getSizeBoardStack(0)*30;
					else return 280; 
			}
		return 10;
	}
	
	public void resetUndoSteps(){
		listUndoSteps.clear();
	}
	
	public void undoStep(){
		zrobionoRuchKartyZBoard_0 = false;
		if (listUndoSteps.size() > 0) {
			step = getUndo();
			if (step.numberSource >= 0) {
				if (step.typeTarget.equals("boardStack")) {
					getCardFromBoardStack(step.numberTarget);
					if (step.numberSource == 0) zrobionoRuchKartyZBoard_0 = true;
				}
				else if (step.typeTarget.equals("finishStack")) getCardFromFinishStack(step.numberTarget);
					pushCardToStack("boardStack", step.numberSource, step.card);
				
			}
			else if (step.numberSource < 0){
					getCardFromBoardStack(0);
					pushCardToStartStack(step.card);
				
				}
			ruchyJuzWykonane = step.ruchyJuzWykonane;
			mozliweRuchy = step.mozliweRuchy;
		}
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

	
	public Karta readCardFromStack(String stackType, int numerStack){
		if (stackType.equals("boardStack")) return boardStack[numerStack].peek();
			else if (stackType.equals("finishStack")) return finishStack[numerStack].peek();
		return null;
	}
	
	public void pushCardToStack(String stackType, int numerStack, Karta card){
		if (stackType.equals("boardStack")) 	boardStack[numerStack].push(card);	
			else if (stackType.equals("finishStack")) 	finishStack[numerStack].push(card);
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

package pasjans;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameBoard implements Cloneable {
	private static final int STOCKSIZE = 104;	
	private static final ArrayList<Karta> taliaStart = new ArrayList<Karta>(STOCKSIZE);
	private static final PointXY pozXYCardOfStackStart = new PointXY(99, 10);
	
	private Stack<Karta> startStack = new Stack<Karta>();
	private Stack<Karta>[] boardStack = new Stack[11];
	private Stack<Karta>[] finishStack = new Stack[8];
	
	private final Random random;
	private String[] cardColor = {"trefl", "kier", "pik", "karo"};
	
	public ArrayList<UndoSteps> listSteps = new ArrayList<UndoSteps>();
	private UndoSteps step = new UndoSteps();
	
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
	
	public int whichAreaPressedOrDropped(double x, double y){
		if ((x>pozXYCardOfStackStart.x) && (x<pozXYCardOfStackStart.x + Karta.CARDWIDTH) 
				&& (y>pozXYCardOfStackStart.y) && (y<pozXYCardOfStackStart.y+Karta.CARDHEIGHT)) return -1;
		
		return 1;
	}
	
	// ustala ile ma być pokazanych kart ze stosu KartyOdlozone - max. 10
	public void countVisibleCardsOnLeftSide(){	
		if (getSizeBoardStack(0) < 10)
			visibleCardsOnLeftSide = getSizeBoardStack(0);
		else visibleCardsOnLeftSide = 10;
	}	
	
	public void pushUndo(UndoSteps step){
		listSteps.add(step);
	}
	
	public int readCountUndoSteps(){
		return listSteps.size();
	}
	
	private UndoSteps getUndo(){
		step = listSteps.get(listSteps.size()-1);
		listSteps.remove(listSteps.size()-1);
		return step;
	}
	
	public Karta getCardFromUndo(){
		step = listSteps.get(listSteps.size()-1);	
		return step.card;
	}
	
	public int getPozXSourceUndo(){
		step = listSteps.get(listSteps.size()-1);
		if (step.typeTarget.equals("boardStack")) return step.numberTarget*75+24;
			else if (step.typeTarget.equals("finishStack")) return step.numberTarget*75+249;	
		return 9;
	}
	
	public int getPozYSourceUndo(){
		step = listSteps.get(listSteps.size()-1); 
		if (step.typeTarget.equals("finishStack")) return 10;		
		if (step.typeTarget.equals("boardStack"))
			if (step.numberTarget == 0) {
				if (getSizeBoardStack(0) >= 10) return 280;
				else if (getSizeBoardStack(0) < 10) return 10 + getSizeBoardStack(0)*30-30;
			}		
		return 179 + 30 *  getSizeBoardStack(step.numberTarget)-30;
	}
	
	public int getPozXTargetUndo(){
		step = listSteps.get(listSteps.size()-1);
		if (step.numberSource > 0) return step.numberSource*75+24;
			else if (step.numberSource == 0) return 9;
		return 99;
	}
	
	public int getPozYTargetUndo(){
		step = listSteps.get(listSteps.size()-1);
		if (step.numberSource > 0) return 179 + 30 * getSizeBoardStack(step.numberSource);
			else if (step.numberSource == 0) {
				if (getSizeBoardStack(0) < 10) return 10 + getSizeBoardStack(0)*30;
					else return 280; 
			}
		return 10;
	}
	
	public void resetUndoSteps(){
		listSteps.clear();
	}
	
	public void undoStep(){
		if (listSteps.size() > 0) {
			step = getUndo();
			//System.out.println("Odczytano kartę:" + step.card + " , która wraca z: " + step.typeTarget + " " + step.numberTarget + " na stos: " +step.numberSource);
			if (step.numberSource >= 0) {
				if (step.typeTarget.equals("boardStack")) getCardFromBoardStack(step.numberTarget);
					else if (step.typeTarget.equals("finishStack")) getCardFromFinishStack(step.numberTarget);
				pushCardToBoardStack(step.numberSource, step.card);
			}
			if (step.numberSource < 0){
				getCardFromBoardStack(0);
				pushCardToStartStack(step.card);
			}
		}
	//	System.out.println("Liczba kroków możliwych do cofnięcia: " + counterUndo);
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

	public void pushCardToBoardStack(int numberOfStack, Karta kartaDoOdlozenia){
		boardStack[numberOfStack].push(kartaDoOdlozenia);
	}
	
	public Karta readCardFromBoardStack(int numberOfStack){
		return boardStack[numberOfStack].peek();
	}
	
	
	
	public int getSizeFinishStack(int numberOfStack){
		return finishStack[numberOfStack].size();
	}
	
	public Karta readCardFromFinishStack(int numberOfStack){
		return finishStack[numberOfStack].peek();
	}
	
	public Karta getCardFromFinishStack(int numberOfStack){
		return finishStack[numberOfStack].pop();
	}
	
	public void pushCardToFinishStack(int numberOfStack, Karta kartaDoOdlozenia){
		finishStack[numberOfStack].push(kartaDoOdlozenia);
	}
	
	public Karta readCardFromStack(String stack, int i){
		if (stack.equals("boardStack")) return boardStack[i].peek();
		else return finishStack[i].peek();
	}
	
	public void pushCardToStack(String stack, int i, Karta card){
		if (stack.equals("boardStack")) 	boardStack[i].push(card);	
			else if (stack.equals("finishStack")) 	finishStack[i].push(card);
	}
	
	@Override
	public Object clone() {
			GameBoard copyGameBoard = new GameBoard();
			copyGameBoard.startStack = (Stack<Karta>) this.startStack.clone();
			
			for (int i = 0; i < 11; i++)
				copyGameBoard.boardStack[i] = (Stack<Karta>) this.boardStack[i].clone();
				
			for (int i = 0; i < 8; i++) 
				copyGameBoard.finishStack[i] = (Stack<Karta>) this.finishStack[i].clone();
					
			copyGameBoard.listSteps = (ArrayList<UndoSteps>) this.listSteps.clone();
			return copyGameBoard;
	}

}

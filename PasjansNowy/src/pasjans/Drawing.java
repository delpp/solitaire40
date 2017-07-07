package pasjans;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Drawing {
	private final int WIDTH = 1200;
	private final int HEIGHT = 580;
	private GraphicsContext gc;
	private GameBoard gameBoard;
	private Scene scene;
	private PixelReader pixReader;
	private WritableImage cart;
	private Image tlo;
	private Image deck;
	private Karta[] tableOfCards; 
	
	public Drawing(GameBoard gameBoard, GraphicsContext gc, Scene scene){
		this.gameBoard = gameBoard;
		this.gc = gc;
		this.scene = scene;
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));					
		gc.setFill(Color.DARKORANGE);
		gc.setTextAlign(TextAlignment.CENTER);
		tableOfCards = new Karta[16];
		
		tlo = new Image("/tlo.png");
		deck = new Image("/talia.png");
	}
	
	public void setGameBoard(GameBoard gameBoard){
		this.gameBoard = gameBoard;
	}
	
	public void clearRect(){
		gc.clearRect(0, 0, WIDTH, HEIGHT);
	}
	
	public void drawBackground(){
		gc.drawImage(tlo, 0, 0);
	}
	
	public void drawBackDeckCard(){
		// rysowanie zaslepki w miejscu gdzie jest cala talia		
		if (gameBoard.getSizeStartStack() > 0)			
			drawCard(264, 0, 99, 10);		
	}
	
	
	public void drawCardsFromStackKartyOdlozone(){
		int x;
		int y;
		int count = 0;
		tableOfCards = new Karta[16];

		if (gameBoard.visibleCardsOnLeftSide > 0){
			// pokazuje po lewej stronie max. 10 pierwszych kart ze stosu KartyOdlozone
			for (int i = 0; i < gameBoard.visibleCardsOnLeftSide; i ++) 
				tableOfCards[i] = gameBoard.getCardFromBoardStack(0);
				
			for (int i = gameBoard.visibleCardsOnLeftSide-1; i >= 0; i--){
				x = tableOfCards[i].shiftHorizontal();
				y = tableOfCards[i].shiftVertical();
				drawCard(x, y, 9, 10 + count*30);

				gameBoard.pushCardToBoardStack(0, tableOfCards[i]);
				count++;
			}
		}
	}
	
	public void drawCardsFromBoardStacks(){
		int x;
		int y;
		int count;
		int sizeStack;
		
		for (int i = 1; i < 11; i++){
			count = 0;
			sizeStack = gameBoard.getSizeBoardStack(i);
			
			if (gameBoard.getSizeBoardStack(i) > 0) {
				
				for (int j = 0; j < sizeStack; j++) 
					tableOfCards[j] = gameBoard.getCardFromBoardStack(i);
				for (int j = sizeStack-1; j >= 0; j --){
					x = tableOfCards[j].shiftHorizontal();
					y = tableOfCards[j].shiftVertical();					
					drawCard(x, y, 24 + i*75, 179 + count*30);
					
					gameBoard.pushCardToBoardStack(i, tableOfCards[j]); // odklada z powrotem karty na stos boardStack[i]
					count++;
				}				
			}
		}
	}
	
	public void drawCardsFromFinishStacks(){
		int x;
		int y;	
		for (int i = 0; i < 8; i++){
			tableOfCards[0] = gameBoard.readCardFromFinishStack(i);		
			if (gameBoard.getSizeFinishStack(i) > 1) {
				x = tableOfCards[0].shiftHorizontal();
				y = tableOfCards[0].shiftVertical();			
				drawCard(x, y, 249 + i * 75, 10);
			}
		}
	}

	public void drawCurrentCard(Karta cardOnHand, Double pozXCardOnHand, Double pozYCardOnHand){
		int x = cardOnHand.shiftHorizontal();
		int y = cardOnHand.shiftVertical();		
		drawCard(x, y, pozXCardOnHand, pozYCardOnHand);
	}
	
	public void drawCard(int shiftX, int shiftY, double pozX, double poxY){
		pixReader = deck.getPixelReader();
		cart = new WritableImage(pixReader, shiftX, shiftY, Karta.CARDWIDTH, Karta.CARDHEIGHT);
		gc.drawImage(cart, pozX, poxY);
	}
	
	public void drawTextGameOver(){
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 80));					
		gc.setFill(Color.BROWN);
		gc.setTextAlign(TextAlignment.CENTER);				
		gc.fillText("GAME OVER", scene.getWidth()/2, scene.getHeight()/2);
	}
	
	public void drawTextCountCardsOfStartStack(){
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));					
		gc.setFill(Color.DARKORANGE);
		gc.setTextAlign(TextAlignment.CENTER);		
		gc.fillText(gameBoard.getSizeStartStack() + "", 132, 125);		
	}
}

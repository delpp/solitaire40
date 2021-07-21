package pasjans;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Pasjans implements Cloneable{
	private final int WIDTH = 900;
	private final int HEIGHT = 580;
	private boolean gameOverStatus;
	private Stage stage;
	private GameAnimationTimer animationTimer;
	private Group root;
	private Canvas canvas;
	private Scene scene;
	private GraphicsContext gc;
	private UserInputQueue userInputQueue;
	private GameBoard gameBoard; 
	private GameOver gameOver;
	private Drawing drawing;
	private Areas areas;
	private int destinationBackNumberStack;
	private boolean undo;
	private boolean backCard;

	double dragDeltaX;
	double dragDeltaY;
	double pozXCardOnHand;
	double pozYCardOnHand;
	int sourceStackNumberCardOnHand;
	private static Karta cardOnHand;
	private static Karta cardToAnimate;
	private boolean tryingToShiftTheCard;
	
	private boolean animation;
	double xSourceAnimation, ySourceAnimation;
	double xTargetAnimation, yTargetAnimation;
	double stepXAnimation;
	double stepYAnimation;
	int numberOfFramesAnimation;
	int countDoneFramesAnimation;
	long timeWhenFrameShowed;
	
	private Button saveGame;
	private Button loadGame;
	
	boolean undoIsPressed = false;
	private GameBoard reset = new GameBoard();
	private UndoStep step;	
	
	public Pasjans(Stage primaryStage){
		stage = primaryStage;
		stage.setTitle("Pasjans");
	}
		
	private class GameAnimationTimer extends AnimationTimer{
		@Override
		public void handle(long currentNanoTime) { 
			update(currentNanoTime);
			draw(currentNanoTime);
		}
	}
		
	public void run() {
		initialize();	
		stage.show();	
		animationTimer = new GameAnimationTimer();
		animationTimer.start();
	}
	
	public void initialize() {
		gameBoard = new GameBoard();
		areas = new Areas();
		gameOver = new GameOver();
		gameBoard.run();
		reset = (GameBoard) gameBoard.clone();
		gameBoard.movesDone = 0;	

		saveGame = new Button("SAVE");
		saveGame.setLayoutX(99+250+150);
		saveGame.setLayoutY(110);
		
		saveGame.setOnMousePressed(e->{	
			try {
				IOgame.savaGame(gameBoard);		
			}
			catch (Exception ex){
				
			}
		});
		
		loadGame = new Button("LOAD");
		loadGame.setLayoutX(99+250+220);
		loadGame.setLayoutY(110);
		
		loadGame.setOnMousePressed(e->{	
			try {
				gameBoard = IOgame.loadGame();
				drawing.setGameBoard(gameBoard);
				gameBoard.countVisibleCardsOnLeftSide();
				System.out.println("Animation: " + animation);
				gameOverStatus = gameOver.isGameOver(gameBoard);
			}
			catch (Exception ex){
				
			}
		});
		
		
		gameOverStatus = false;
		animation = false;
		
		numberOfFramesAnimation = 10;
		countDoneFramesAnimation = 0;
		timeWhenFrameShowed = System.nanoTime();
		
		sourceStackNumberCardOnHand = -1;
		dragDeltaX = 0;
		dragDeltaY = 0;
		tryingToShiftTheCard = false;
		
				
		root = new Group();
		canvas = new Canvas(WIDTH, HEIGHT);	
		
		root.getChildren().add(canvas);

		root.getChildren().add(saveGame);
		root.getChildren().add(loadGame);

		scene = new Scene(root);
		
		gc = canvas.getGraphicsContext2D();
		drawing = new Drawing(gameBoard, gc, scene, WIDTH, HEIGHT);
		
		stage.setScene(scene);
		stage.setResizable(false);
		stage.sizeToScene();
		
		userInputQueue = new UserInputQueue();
		scene.setOnMousePressed(mouseEvent -> userInputQueue.addMouseEvent(mouseEvent));
		
		scene.setOnMouseDragged(e -> {
			pozXCardOnHand = e.getSceneX() - dragDeltaX;
			pozYCardOnHand = e.getSceneY() - dragDeltaY;
		});
		
		scene.setOnMouseReleased(e -> {
			if (gameOverStatus==false){
				if ((cardOnHand != null) && (animation==false)){
				
					// czy opuszczono karte na jednym ze stosów boardStack	
					for (int i = 1; i < 11; i++)
						if (areas.isActionOfBoardFrom1To10Stack(gameBoard, e.getSceneX(), e.getSceneY(), i)) 	
							if (i != sourceStackNumberCardOnHand) {
								pushOrBackCardOnHand(i, "boardStack");							
								tryingToShiftTheCard = true;
							}
							
					// czy opuszczono karte na jednym ze stosów finalnych finalStack
					for (int i = 0; i < 8; i++)
						if (areas.isActionOfFinishStack(gameBoard, e.getSceneX(), e.getSceneY(), i)) {
							pushOrBackCardOnHand(i, "finishStack");
							tryingToShiftTheCard = true;
						}
					}
			
				// odloz z powrotem na zrodlowy stos
				if (tryingToShiftTheCard == false)										
					backCardToSourceStack();				
					
				cardOnHand = null;
				if (animation == false) gameOverStatus = gameOver.isGameOver(gameBoard);
				
				sourceStackNumberCardOnHand = -1;
				dragDeltaX = 0;
				dragDeltaY = 0;
				pozXCardOnHand = 0;
				pozYCardOnHand = 0;	
				tryingToShiftTheCard = false;
				//System.out.println("Gave over: " + gameOverStatus);
			}
		});
		
		stage.setOnCloseRequest(e -> stage_CloseRequest(e));
	}
		


	

	

	

	
	

	

	

	
		
	

		
	public void pushOrBackCardOnHand(int numberOfStack, String typeStack){		
			if (isCompatibilityCardOnStackAndOnHand(numberOfStack, typeStack)){			
				step = new UndoStep(cardOnHand, sourceStackNumberCardOnHand, typeStack, numberOfStack, gameBoard.movesDone, gameBoard.possibleMoves);
				gameBoard.pushUndo(step);
				gameBoard.pushCardToStack(typeStack, numberOfStack, cardOnHand);					
			}
			else backCardToSourceStack();
	}

	
	public void backCardToSourceStack(){
		for (int i = 0; i < 11; i++)
			if (sourceStackNumberCardOnHand == i) setBackCardAnimationMove();				
	}
	
	public void setBackCardAnimationMove() {
		destinationBackNumberStack = sourceStackNumberCardOnHand;
		animation = true;
		cardToAnimate = cardOnHand;
		
		xSourceAnimation = pozXCardOnHand;
		ySourceAnimation = pozYCardOnHand;
	
	
		if (sourceStackNumberCardOnHand > 0) xTargetAnimation = sourceStackNumberCardOnHand*75+24;
		if (sourceStackNumberCardOnHand == 0) xTargetAnimation = 9;

	
		if (sourceStackNumberCardOnHand > 0) yTargetAnimation = 179 + 30 * gameBoard.getSizeBoardStack(sourceStackNumberCardOnHand);
		if (sourceStackNumberCardOnHand == 0) {
			if (gameBoard.getSizeZeroBoardStack() < 10) yTargetAnimation = 10 + gameBoard.getSizeZeroBoardStack()*30;
			else yTargetAnimation = 280; 
		}
	
		stepXAnimation = (xTargetAnimation - xSourceAnimation)/numberOfFramesAnimation;
		stepYAnimation = (yTargetAnimation - ySourceAnimation)/numberOfFramesAnimation;		
		backCard = true;
		
	}
	
	
	public boolean isCompatibilityCardOnStackAndOnHand(int stackNumber, String stackType){
		if (stackType.equals("boardStack")) 
			if ((gameBoard.getSizeZeroBoardStack() == 0) || (gameBoard.getSizeBoardStack(stackNumber) == 0)) return true;
		
		if (stackType.equals("boardStack")) 
				return cardOnHand.canPutCardToDecreaseStack(gameBoard.readCardFromStack(stackType, stackNumber));
			else 
				return cardOnHand.canPutCardToIncreaseStock(gameBoard.readCardFromStack(stackType, stackNumber));		
	}
	

	public void handleMouseInput(){
		MouseEvent mouseState = userInputQueue.getMouseEvent();
		
		if (mouseState == null) return;
		double x = mouseState.getSceneX();
		double y = mouseState.getSceneY();
		
		// jesli kliknieto lewy klawisz myszki
		if ((mouseState.getButton() == MouseButton.PRIMARY)	&& (gameOverStatus==false) && (animation == false)){
			
			if (areas.isActionOfStartStack(gameBoard, x, y)) 
				takeCardFromStartStack();
																
			if (areas.isActionOfBoardZeroStack(gameBoard, x, y))
				takeCardFromBoardZeroStack(x, y);
			
			for (int i = 1; i < 11; i++)
				if (areas.isActionOfBoardFrom1To10Stack(gameBoard, x, y, i))
					takeCardFromBoardStack(i, x, y);
			
			if (areas.isActionOfNewGameButton(gameBoard, x, y))
				newGame();
			
			if (areas.isActionOfResetButton(gameBoard, x, y))
				resetGame();	
			
			if (areas.isActionOfUndoButton(gameBoard, x, y))
				undo();
		}				
	}	
	
	public void takeCardFromStartStack(){
		if (gameBoard.getSizeStartStack() > 0 ) {
			step = new UndoStep(gameBoard.readCardFromStartStack(), -1, "boardStack", 0, gameBoard.movesDone, gameBoard.possibleMoves);
			gameBoard.pushCardToStack("boardStack", 0, gameBoard.getCardFromStartStack());				
			gameBoard.pushUndo(step);
			gameBoard.countVisibleCardsOnLeftSide();
		}		
	}
	
	public void takeCardFromBoardZeroStack(double x, double y){
		if (gameBoard.getSizeZeroBoardStack() > 0 ) {
			cardOnHand = gameBoard.getCardFromZeroBoardStack();
			sourceStackNumberCardOnHand = 0; // zabrano ze stosu KartyOdlozone	- BoardStack stos 0-y	
			dragDeltaX = x - 9;
			dragDeltaY = y - 10 - gameBoard.visibleCardsOnLeftSide*30+30;
			pozXCardOnHand = 9;
			pozYCardOnHand = 10 + gameBoard.visibleCardsOnLeftSide*30-30;
			gameBoard.countVisibleCardsOnLeftSide();
		}
	}
	
	public void takeCardFromBoardStack(int i, double x, double y){
		if (gameBoard.getSizeBoardStack(i) > 0 ) {
			cardOnHand = gameBoard.getCardFromBoardStack(i);
			sourceStackNumberCardOnHand = i; // zabrano z i stosu BoardStack					
			dragDeltaX = x - 24 - i*75;
			dragDeltaY = y - 179 - gameBoard.getSizeBoardStack(i) *30;
			pozXCardOnHand = 24 + i*75;
			pozYCardOnHand = 179 + gameBoard.getSizeBoardStack(i)*30;
		}
	}
	
	public void newGame(){
		gameBoard.removeAllCardFromStartStack();
		gameBoard.removeAllCardFromZeroBoardStack();
		gameBoard.removeAllCardFromBoardStack();
		gameBoard.removeAllCardFromFinishStack();

		gameBoard.resetUndoSteps();
		gameBoard.run();
		gameOverStatus = false;			
		sourceStackNumberCardOnHand = -1;
		dragDeltaX = 0;
		dragDeltaY = 0;
		tryingToShiftTheCard = false;
		reset = (GameBoard) gameBoard.clone();
	}
	
	public void resetGame(){
		gameBoard = (GameBoard) reset.clone();
		drawing.setGameBoard(gameBoard);
		gameBoard.countVisibleCardsOnLeftSide();
		gameOverStatus = gameOver.isGameOver(gameBoard);
	}
	
	public void undo(){
		if ((gameBoard.readCountUndoSteps() > 0) && (gameBoard.positionInUndoList > 0)){
			setUndoAnimationMove();
			undo = true;
		}
	}
	
	public void setUndoAnimationMove() {
		animation = true;
		gameOverStatus = false;
		cardToAnimate = gameBoard.getCardFromUndo();
		
		xSourceAnimation = gameBoard.getPozXSourceUndo();
		xTargetAnimation = gameBoard.getPozXTargetUndo();

		ySourceAnimation = gameBoard.getPozYSourceUndo();
		yTargetAnimation = gameBoard.getPozYTargetUndo();
			
		stepXAnimation = (xTargetAnimation - xSourceAnimation)/numberOfFramesAnimation;
		stepYAnimation = (yTargetAnimation - ySourceAnimation)/numberOfFramesAnimation;
		
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));					
		gc.setFill(Color.DARKORANGE);
		
	}
	
	public void update(long currentNanoTime) {
		handleMouseInput();		
		if (animation == true) setAnimateCardParameter(currentNanoTime);
	}
	
	public void setAnimateCardParameter(long currentTime){
		if ((currentTime-timeWhenFrameShowed)/1E9 > 0.1/numberOfFramesAnimation){			
			pozXCardOnHand = xSourceAnimation + stepXAnimation;
			pozYCardOnHand = ySourceAnimation + stepYAnimation;		
			xSourceAnimation = pozXCardOnHand;
			ySourceAnimation = pozYCardOnHand;			
			countDoneFramesAnimation++;			
			timeWhenFrameShowed = currentTime;
		}
		
		if (countDoneFramesAnimation == numberOfFramesAnimation) {
			if (undo){
				gameBoard.undoStep();			
				gameBoard.countVisibleCardsOnLeftSide();
				undo = false;
			}
			if (backCard){
				gameBoard.pushCardToStack("boardStack", destinationBackNumberStack, cardToAnimate);	
				gameBoard.countVisibleCardsOnLeftSide();
				backCard = false;
				destinationBackNumberStack = -1;
			}
			countDoneFramesAnimation = 0;
			cardToAnimate = null;
			animation = false;
		}
	}
	
	public void draw(long currentNanoTime){

			drawing.clearRect();
			drawing.drawBackground();	
			drawing.drawBackDeckCard();
			drawing.drawCardsFromStackKartyOdlozone();
			drawing.drawCardsFromBoardStacks();
			drawing.drawCardsFromFinishStacks();
			drawing.drawTextCountCardsOfStartStack();
			
			if (cardOnHand != null) drawing.drawCurrentCard(cardOnHand, pozXCardOnHand, pozYCardOnHand);		
			if (cardToAnimate != null) drawing.drawCurrentCard(cardToAnimate, pozXCardOnHand, pozYCardOnHand);

			if (gameOverStatus) drawing.drawTextGameOver();		
	}
	
	
	
	private void stage_CloseRequest(WindowEvent windowEvent) {
	    	windowEvent.consume();    	
	    	Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	            	if (AlertBox.showAndWait(
	            			AlertType.CONFIRMATION, 
	            			"PASJANS", 
	            			"Czy chcesz wyjść z gry?")
	            			.orElse(ButtonType.CANCEL) == ButtonType.OK) {
						animationTimer.stop();
						unloadContent();
						stage.close();
					}
	            }
	    	} );
	    }
	 
	public void unloadContent(){
		
	}
}

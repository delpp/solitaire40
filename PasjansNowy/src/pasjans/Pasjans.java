package pasjans;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Pasjans implements Cloneable{
	private final int WIDTH = 1200;
	private final int HEIGHT = 580;
	private boolean gameOver;
	private Stage stage;
	private GameAnimationTimer animationTimer;
	private Group root;
	private Canvas canvas;
	private Scene scene;
	private GraphicsContext gc;
	private UserInputQueue userInputQueue;
	private GameBoard gameBoard; 
	private Drawing drawing;
	private int numberOfMovesMade;
	private int destinationBackNumberStack;
	private boolean undo;
	private boolean backCard;


	double dragDeltaX;
	double dragDeltaY;
	double pozXCardOnHand;
	double pozYCardOnHand;
	int sourceCardOnHand;
	private static Karta cardOnHand;
	private static Karta cardToAnimate;
	private boolean probaPrzelozeniaKarty;
	
	private boolean animation;
	double xSourceAnimation, ySourceAnimation;
	double xTargetAnimation, yTargetAnimation;
	double stepXAnimation;
	double stepYAnimation;
	int numberOfFramesAnimation;
	int countDoneFramesAnimation;
	long timeWhenFrameShowed;
	
	private Button zapamietajUklad;
	private Button przywrocUklad;
	private Button noweRozdanie;
	private Button cofnij;
	private TableView<UndoSteps> tableOfUndos;
	private ObservableList<UndoSteps> undoSteps = FXCollections.observableArrayList(new UndoSteps());
	
	boolean undoIsPressed = false;
	private GameBoard[] back = new GameBoard[5];
	private UndoSteps step;
	private Table tableOfUndo;
	
	
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
		gameBoard.run();
		back[0] = (GameBoard) gameBoard.clone();

		
		zapamietajUklad = new Button("Zapamiętaj układ");
		zapamietajUklad.setLayoutX(99+80);
		zapamietajUklad.setLayoutY(110);
		
		zapamietajUklad.setOnMousePressed(e->{			
				back[0] = (GameBoard) gameBoard.clone();		
				
				System.out.println("Zapamiętano układ");
		});
		
		noweRozdanie = new Button("Nowe rozdanie");
		noweRozdanie.setLayoutX(99+250);
		noweRozdanie.setLayoutY(110);
		noweRozdanie.setOnMousePressed( e -> {
			back[0] = null;
			gameBoard.removeAllCardFromStartStack();
			gameBoard.removeAllCardFromBoardStack();
			gameBoard.removeAllCardFromFinishStack();
			gameBoard.resetUndoSteps();
			gameBoard.run();
			gameOver = false;			
			sourceCardOnHand = -1;
			dragDeltaX = 0;
			dragDeltaY = 0;
			probaPrzelozeniaKarty = false;
			numberOfMovesMade = 0;
	
		});
				
		przywrocUklad = new Button("Resetuj układ");
		przywrocUklad.setLayoutX(99+80);
		przywrocUklad.setLayoutY(145);
		
		przywrocUklad.setOnMousePressed(e->{
			if (back[0] != null){
				gameBoard = (GameBoard) back[0].clone();
				drawing.setGameBoard(gameBoard);
				gameBoard.countVisibleCardsOnLeftSide();
				System.out.println("Zresetowano układ");
			}
		});
		
		cofnij = new Button("UNDO");
		cofnij.setLayoutX(99+250);
		cofnij.setLayoutY(145);
		
		cofnij.setOnMousePressed(e->{
			
			if (gameBoard.readCountUndoSteps() > 0) {
				setUndoAnimationMove();
				undo = true;
			}
		});
		
		tableOfUndos = new TableView<UndoSteps>();
		TableColumn<UndoSteps, Integer> numberOfMoveColumn = new TableColumn("Numer ruchu");
		numberOfMoveColumn.setMinWidth(150);
		/*numberOfMoveColumn.setCellValueFactory(cellData ->
			cellData.getValue().getIdProperty());*/
		
		tableOfUndos.setLayoutX(880);
		tableOfUndos.setLayoutY(10);
		tableOfUndos.setPrefWidth(150);
		tableOfUndos.getColumns().addAll(numberOfMoveColumn);
		tableOfUndos.setItems(undoSteps);
		
		
		gameOver = false;
		animation = false;
		
		numberOfFramesAnimation = 10;
		countDoneFramesAnimation = 0;
		timeWhenFrameShowed = System.nanoTime();
		
		sourceCardOnHand = -1;
		//tableOfCards = new Karta[16];
		dragDeltaX = 0;
		dragDeltaY = 0;
		probaPrzelozeniaKarty = false;
		numberOfMovesMade = 0;
				
		root = new Group();
		canvas = new Canvas(WIDTH, HEIGHT);	
		
		//tableOfUndo = new Table(root, 100, 100, 70, 30, 10, 10);
					
		root.getChildren().add(canvas);
		root.getChildren().add(zapamietajUklad);
		root.getChildren().add(przywrocUklad);
		root.getChildren().add(noweRozdanie);
		root.getChildren().add(cofnij);
		root.getChildren().add(tableOfUndos);

		scene = new Scene(root);
		
		gc = canvas.getGraphicsContext2D();
		drawing = new Drawing(gameBoard, gc, scene);
		
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
			if (gameOver==false){
				if ((cardOnHand != null) && (animation==false)){
				
					// czy opuszczono karte na jednym ze stosów boardStack	
					for (int i = 1; i < 11; i++)
						if (isActionOnAreaOfBoardStack(e.getSceneX(), e.getSceneY(), i)) 	
							if (i != sourceCardOnHand) {
								pushOrBackCardOnHand(i, "boardStack");							
								probaPrzelozeniaKarty = true;
							}
							
					// czy opuszczono karte na jednym ze stosów finalnych finalStack
					for (int i = 0; i < 8; i++)
						if (isActionOnAreaOfFinishStack(e.getSceneX(), e.getSceneY(), i)) {
							pushOrBackCardOnHand(i, "finishStack");
							probaPrzelozeniaKarty = true;
						}
					}
			
				// odloz z powrotem na zrodlowy stos
				if (probaPrzelozeniaKarty == false)										
					backCardToSourceStack();				
					
				if (animation == false) gameOver = isGameOver();
				cardOnHand = null;
				sourceCardOnHand = -1;
				dragDeltaX = 0;
				dragDeltaY = 0;
				pozXCardOnHand = 0;
				pozYCardOnHand = 0;	
				probaPrzelozeniaKarty = false;
				//System.out.println("Gave over: " + gameOver);
			}
		});
		
		stage.setOnCloseRequest(e -> stage_CloseRequest(e));
	}

	public void setUndoAnimationMove() {
		animation = true;
		gameOver = false;
		cardToAnimate = gameBoard.getCardFromUndo();
		undoSteps.remove(cardToAnimate);
		xSourceAnimation = gameBoard.getPozXSourceUndo();
		xTargetAnimation = gameBoard.getPozXTargetUndo();

		ySourceAnimation = gameBoard.getPozYSourceUndo();
		yTargetAnimation = gameBoard.getPozYTargetUndo();
			
		stepXAnimation = (xTargetAnimation - xSourceAnimation)/numberOfFramesAnimation;
		stepYAnimation = (yTargetAnimation - ySourceAnimation)/numberOfFramesAnimation;
		
		gc.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));					
		gc.setFill(Color.DARKORANGE);
		
	}
	
	public boolean isGameOver(){	
		if (gameBoard.getSizeStartStack() > 0) return false;
		if (isCardFromZeroFixToAnyOther()) return false;
						
		for (int i = 1; i < 11; i++) 	{				
			if (gameBoard.getSizeBoardStack(i) == 0) return false;
			if (isCardFromBoardFixToAnyOther(i)) return false;	
		}			
		return true;
	}
	
	public boolean isCardFromZeroFixToAnyOther(){
		if (gameBoard.getSizeBoardStack(0) > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			for (int i = 1; i <= 10; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "boardStack")) return true;	
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack")) return true;			
		}
		return false;
	}
	
	public boolean isCardFromBoardFixToAnyOther(int numberStack){
		cardOnHand = gameBoard.getCardFromBoardStack(numberStack); 		
		sourceCardOnHand = numberStack;
		
		System.out.println("Testuję kartę: " + cardOnHand.getCard() + " ze stosu: " + sourceCardOnHand  + ". Jego aktualny stan: " + gameBoard.getSizeBoardStack(sourceCardOnHand) + " kart");
				
		for (int j = 1; j < 11; j++){						
			if (j != sourceCardOnHand) {
				System.out.println("Sprawdzam kartę ze stosu " + j);
				
				if (isCompatibilityCardOnStackAndOnHand(j, "boardStack")) {
				
					System.out.println("Karta ze stoku " + sourceCardOnHand + " jest kompatybilna z kartą ze stoku: " + j);
				
					if (!isCompatibilityCardOnStackAndOnHand(sourceCardOnHand, "boardStack")){
						System.out.println("Karta nie może wrócić na swoje miejsce");
						gameBoard.pushCardToBoardStack(numberStack, cardOnHand);
						return true;
					}	
					else {
						if (gameBoard.getSizeBoardStack(sourceCardOnHand) > 0){
							System.out.println("Karta może wrócić na swoje miejsce. Ten układ nie jest brany pod uwagę do kontynuacji gry");
							gameBoard.pushCardToBoardStack(sourceCardOnHand, cardOnHand);
							return false;
						}
						else {
							gameBoard.pushCardToBoardStack(numberStack, cardOnHand);
							return true;
						}
					}
				}
			}
		}
		
		for (int j = 0; j < 8; j++){
			if (isCompatibilityCardOnStackAndOnHand(j, "finishStack")) {
				System.out.println("Karta ze stoku " + sourceCardOnHand + " jest kompatybilna z kartą ze stoku górnego: " + j);
				gameBoard.pushCardToBoardStack(numberStack, cardOnHand);
				return true;
			}
		}		
		gameBoard.pushCardToBoardStack(sourceCardOnHand, cardOnHand);
		return false;
	}
		
	public void pushOrBackCardOnHand(int i, String typeStack){
			if (isCompatibilityCardOnStackAndOnHand(i, typeStack)){
				step = new UndoSteps(cardOnHand, sourceCardOnHand, typeStack, i);
				gameBoard.pushUndo(step);
				undoSteps.add(step);
				gameBoard.pushCardToStack(typeStack, i, cardOnHand);					
				numberOfMovesMade++;
			}
			else backCardToSourceStack();
	}
	
	public void backCardToSourceStack(){
		for (int i = 0; i < 11; i++)
			if (sourceCardOnHand == i) setBackCardAnimationMove();				
			

	}
	
	public void setBackCardAnimationMove() {
		destinationBackNumberStack = sourceCardOnHand;
		animation = true;
		cardToAnimate = cardOnHand;
		
		xSourceAnimation = pozXCardOnHand;
		ySourceAnimation = pozYCardOnHand;
	
	
		if (sourceCardOnHand > 0) xTargetAnimation = sourceCardOnHand*75+24;
		if (sourceCardOnHand == 0) xTargetAnimation = 9;

	
		if (sourceCardOnHand > 0) yTargetAnimation = 179 + 30 * gameBoard.getSizeBoardStack(sourceCardOnHand);
		if (sourceCardOnHand == 0) {
			if (gameBoard.getSizeBoardStack(0) < 10) yTargetAnimation = 10 + gameBoard.getSizeBoardStack(0)*30;
			else yTargetAnimation = 280; 
		}
	
		stepXAnimation = (xTargetAnimation - xSourceAnimation)/numberOfFramesAnimation;
		stepYAnimation = (yTargetAnimation - ySourceAnimation)/numberOfFramesAnimation;		
		backCard = true;
		
	}
	
	
	
	
	
	public boolean isCompatibilityCardOnStackAndOnHand(int stackNumber, String stackType){
		if ((stackType.equals("boardStack")) && (gameBoard.getSizeBoardStack(stackNumber)==0)) return true;
		
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
		if ((mouseState.getButton() == MouseButton.PRIMARY)	&& (gameOver==false) && (animation == false)){
			
			// czy kliknięto obszar brania kolejnej karty (StartStack)
			if (gameBoard.whichAreaPressedOrDropped(x, y) == -1) {
				if (gameBoard.getSizeStartStack() > 0 ) {
					step = new UndoSteps(gameBoard.readCardFromStartStack(), -1, "boardStack", 0);
					gameBoard.pushCardToBoardStack(0, gameBoard.getCardFromStartStack());				
					gameBoard.pushUndo(step);
				}
				gameBoard.countVisibleCardsOnLeftSide();	
			}			
								
			// czy kliknieto obszar na talii - widoczne karty po lewej stronie - BoardStack stos 0-y
			if (isPressedAreaOfKartyOdlozoneStack(x, y)){
				if (gameBoard.getSizeBoardStack(0) > 0 ) {
					cardOnHand = gameBoard.getCardFromBoardStack(0);
					sourceCardOnHand = 0; // zabrano ze stosu KartyOdlozone	- BoardStack stos 0-y	
					dragDeltaX = x - 9;
					dragDeltaY = y - 10 - gameBoard.visibleCardsOnLeftSide*30+30;
					pozXCardOnHand = 9;
					pozYCardOnHand = 10 + gameBoard.visibleCardsOnLeftSide*30-30;
					gameBoard.countVisibleCardsOnLeftSide();
				}
			}
			
			// czy kliknieto jeden z obszarow 10-u stosow BoardStack
			for (int i = 1; i < 11; i++)
				if (isActionOnAreaOfBoardStack(x, y, i)){
					if (gameBoard.getSizeBoardStack(i) > 0 ) {
						cardOnHand = gameBoard.getCardFromBoardStack(i);
						sourceCardOnHand = i; // zabrano z i stosu BoardStack					
						dragDeltaX = x - 24 - i*75;
						dragDeltaY = y - 179 - gameBoard.getSizeBoardStack(i) *30;
						pozXCardOnHand = 24 + i*75;
						pozYCardOnHand = 179 + gameBoard.getSizeBoardStack(i)*30;
					}
			}
		}				
	}
	
	public boolean isPressedAreaOfKartyOdlozoneStack(double x, double y){
		if ((x>9) && (x<9 + Karta.CARDWIDTH) 
				&& (y > 10 + gameBoard.visibleCardsOnLeftSide*30 - 30) && ( y< 10 + gameBoard.visibleCardsOnLeftSide*30 + Karta.CARDHEIGHT - 30)) return true;	
		return false;
	}
	
	
	
	public boolean isActionOnAreaOfBoardStack(double x, double y, int numberOfStack){
		if ((x > 24 + numberOfStack * 75) && (x < 24 + numberOfStack * 75 + Karta.CARDWIDTH) 
				&& (y > 179 + gameBoard.getSizeBoardStack(numberOfStack) * 30 - 30) && (y < 179 + gameBoard.getSizeBoardStack(numberOfStack) * 30 + Karta.CARDHEIGHT - 30)) return true;
		return false;
	}
	
	public boolean isActionOnAreaOfFinishStack(double x, double y, int numberOfStack){
		if ((x > 249 + numberOfStack * 75) && (x < 249 + numberOfStack * 75 + Karta.CARDWIDTH) 
				&& (y > 10) && (y < 10 + Karta.CARDHEIGHT)) return true;
		return false;
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
				gameBoard.pushCardToBoardStack(destinationBackNumberStack, cardToAnimate);	
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

			if (gameOver) drawing.drawTextGameOver();		
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

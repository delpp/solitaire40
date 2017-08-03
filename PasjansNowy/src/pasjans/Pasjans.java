package pasjans;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Scanner;

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
	private Areas areas;
	private int destinationBackNumberStack;
	private boolean undo;
	private boolean backCard;

	private int deepSteps;
	private int sprawdzoneDostepneRuchy;


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
	
	private Button saveGame;
	private Button loadGame;
	private Button zapamietajUklad;
	private Button przywrocUklad;
	private Button noweRozdanie;
	private Button cofnij;
	private Button countPossiblityMoves;
	private Button checkSolutions;
	
	private TableView<UndoStep> tableOfUndos;
	private ObservableList<UndoStep> undoSteps = FXCollections.observableArrayList(new UndoStep());
	
	boolean undoIsPressed = false;
	private GameBoard[] back = new GameBoard[5];
	private UndoStep step;
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
		areas = new Areas();
		gameBoard.run();
		back[0] = (GameBoard) gameBoard.clone();
		gameBoard.ruchyJuzWykonane = 0;
		
		checkSolutions = new Button("Sprawdź czy układ ma rozwiązanie");
		checkSolutions.setLayoutX(99+250+300);
		checkSolutions.setLayoutY(145);
		checkSolutions.setOnMousePressed(e->{
			deepSteps = 0;
			gameBoard.mozliweRuchy = countPossibilityMoves();
			sprawdzoneDostepneRuchy = 0;
			boolean koniecTestu = false;
			boolean zrobionoRuchKartZBoard_1_10 = false;
			boolean zrobionoRuch = false;
			boolean przelozonoAsy = false;
			long calkowitaLiczbaPrzetestowanychKombinacji = 0;
			
			gameBoard.ruchyJuzWykonane = 0;
			Scanner skaner = new Scanner(System.in);
			String a;
			
			
			do {

				
				if (gameBoard.ruchyJuzWykonane < gameBoard.mozliweRuchy){
					
					przelozonoAsy = przelozWszystkieAsy();
					
					zrobionoRuch = zrobKrokZStosuStartowego();
					zrobionoRuch = zrobKrokZeStosuZeroBoard();
					
					zrobKrokZeStosuBoard();
					
					calkowitaLiczbaPrzetestowanychKombinacji++;
					System.out.println("Całkowita Liczba Przetestowanych Kombinacji: " + calkowitaLiczbaPrzetestowanychKombinacji);
					
					gameBoard.mozliweRuchy = countPossibilityMoves();
					gameBoard.ruchyJuzWykonane = 0;
				}
				else 
					if ((gameBoard.ruchyJuzWykonane == gameBoard.mozliweRuchy) && (deepSteps > 0)){						
						zrobUndo();	
					}			
				sprawdzoneDostepneRuchy = 0;
				
				System.out.println("Deep steps: " + deepSteps);
				
				if (deepSteps == 0) 
					if (gameBoard.ruchyJuzWykonane == gameBoard.mozliweRuchy){
						System.out.println("koniecTestu = true. Deep steps: " + deepSteps + " Ruchy już wykonanane: " + gameBoard.ruchyJuzWykonane + ". Możliwe ruchy: " + gameBoard.mozliweRuchy);
						koniecTestu = true;
					}
				System.out.println("Wcisnij ENTER");
				//a = skaner.nextLine();
			} 
			while (!koniecTestu);
			
			System.out.println("WYJSCIE. Deep steps: " + deepSteps + " Ruchy już wykonanane: " + gameBoard.ruchyJuzWykonane + ". Możliwe ruchy: " + gameBoard.mozliweRuchy);
			
		});
		
		countPossiblityMoves = new Button("Oblicz mozliwe ruchy");
		countPossiblityMoves.setLayoutX(99+250+150);
		countPossiblityMoves.setLayoutY(145);
		countPossiblityMoves.setOnMousePressed(e->{
			countPossibilityMoves();
		});
		
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
				gameOver = isGameOver();
			}
			catch (Exception ex){
				
			}
		});
		
		
		zapamietajUklad = new Button("Zapamiętaj układ");
		zapamietajUklad.setLayoutX(99+80);
		zapamietajUklad.setLayoutY(110);
		
		zapamietajUklad.setOnMousePressed(e->{			
				back[0] = (GameBoard) gameBoard.clone();		
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
			
	
		});
				
		przywrocUklad = new Button("Przywróć układ");
		przywrocUklad.setLayoutX(99+80);
		przywrocUklad.setLayoutY(145);
		
		przywrocUklad.setOnMousePressed(e->{
			if (back[0] != null){
				gameBoard = (GameBoard) back[0].clone();
				drawing.setGameBoard(gameBoard);
				gameBoard.countVisibleCardsOnLeftSide();
				gameOver = isGameOver();
				
				System.out.println("Przywrócono układ");
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
		
		tableOfUndos = new TableView<UndoStep>();
		TableColumn<UndoStep, Integer> numberOfMoveColumn = new TableColumn("Numer ruchu");
		numberOfMoveColumn.setMinWidth(150);
		
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
		
				
		root = new Group();
		canvas = new Canvas(WIDTH, HEIGHT);	
		
		//tableOfUndo = new Table(root, 100, 100, 70, 30, 10, 10);
					
		root.getChildren().add(canvas);
		root.getChildren().add(zapamietajUklad);
		root.getChildren().add(przywrocUklad);
		root.getChildren().add(noweRozdanie);
		root.getChildren().add(cofnij);
		root.getChildren().add(saveGame);
		root.getChildren().add(loadGame);
		root.getChildren().add(countPossiblityMoves);
		root.getChildren().add(checkSolutions);
		
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
						if (areas.isActionOfBoardStack(gameBoard, e.getSceneX(), e.getSceneY(), i)) 	
							if (i != sourceCardOnHand) {
								pushOrBackCardOnHand(i, "boardStack");							
								probaPrzelozeniaKarty = true;
							}
							
					// czy opuszczono karte na jednym ze stosów finalnych finalStack
					for (int i = 0; i < 8; i++)
						if (areas.isActionOfFinishStack(gameBoard, e.getSceneX(), e.getSceneY(), i)) {
							pushOrBackCardOnHand(i, "finishStack");
							probaPrzelozeniaKarty = true;
						}
					}
			
				// odloz z powrotem na zrodlowy stos
				if (probaPrzelozeniaKarty == false)										
					backCardToSourceStack();				
					
				cardOnHand = null;
				if (animation == false) gameOver = isGameOver();
				
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
	
	public boolean przelozWszystkieAsy() {
		return false;
	}
	
	public boolean zrobKrokZStosuStartowego(){
		//System.out.println("Przechodzę przez Start Stack");
		if (gameBoard.getSizeStartStack() > 0) {
			//System.out.println("Mozna zrobic krok z stosu startowego. Rozmiar stosu startowego: " + gameBoard.getSizeStartStack());
			sprawdzoneDostepneRuchy++;
			//System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + ". Ruchy już wykonane: " + gameBoard.ruchyJuzWykonane);
		
			if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
				System.out.println("Liczba ruchów wykonanych w tym układzie = 0. Stos zerowy = " + gameBoard.getSizeStartStack() + " . Robię kolejny krok: biorę nową kartę ze stosu startowego: " + gameBoard.readCardFromStartStack());		
				gameBoard.ruchyJuzWykonane++;	
				step = new UndoStep(gameBoard.readCardFromStartStack(), -1, "boardStack", 0, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
				gameBoard.pushCardToStack("boardStack", 0, gameBoard.getCardFromStartStack());	
				gameBoard.pushUndo(step);	
				deepSteps ++;
				gameBoard.ruchyJuzWykonane = 0;
				gameBoard.mozliweRuchy = countPossibilityMoves();
				return true;
			}
		}	
		return false;
		//System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + " Ruchy już wykonane na tym poziomie: " + gameBoard.ruchyJuzWykonane);

	}
	
	public boolean zrobKrokZeStosuZeroBoard(){
		
		//System.out.println("Przechodzę przez Board 0");
		//System.out.println("Rozmiar Stosu Board 0: " + gameBoard.getSizeBoardStack(0));
		
		if (gameBoard.getSizeBoardStack(0) > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack")) 	{
					//System.out.println("MOŻNA wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Final numer: " + i);
					sprawdzoneDostepneRuchy++;
					if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
						cardOnHand = gameBoard.getCardFromBoardStack(0);
						gameBoard.ruchyJuzWykonane++;
						step = new UndoStep(cardOnHand, 0, "finishStack", i, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
						gameBoard.pushCardToStack("finishStack", i, cardOnHand);
						gameBoard.pushUndo(step);	
						deepSteps ++;
						gameBoard.ruchyJuzWykonane = 0;
						System.out.println("Robię kolejny krok: biorę kartę ze stosu zerowego kartę " + cardOnHand + " i kładę ją na stos Finish numer: " + i);
						
						gameBoard.mozliweRuchy = countPossibilityMoves();
						cardOnHand = null;
						return true;
					}		
				}
			
			
			for (int i = 1; i <= 10; i++) 
				if (cardOnHand.getCardNumber() != 1)
					if (isCompatibilityCardOnStackAndOnHand(i, "boardStack")) 	{
						//System.out.println("MOŻNA wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Board numer: " + i);
						sprawdzoneDostepneRuchy++;
					
						System.out.println("SPRAWDZONE dostępne ruchy : " + sprawdzoneDostepneRuchy + " Ruchy już wykonane: " + gameBoard.ruchyJuzWykonane + ". Mozliwe ruchy: " + gameBoard.mozliweRuchy);
						if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
							cardOnHand = gameBoard.getCardFromBoardStack(0);
							gameBoard.ruchyJuzWykonane++;
							step = new UndoStep(cardOnHand, 0, "boardStack", i, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
							gameBoard.pushCardToStack("boardStack", i, cardOnHand);
							gameBoard.pushUndo(step);	
							deepSteps ++;
							gameBoard.ruchyJuzWykonane = 0;
							System.out.println("Robię kolejny krok: biorę kartę ze stosu zerowego kartę " + cardOnHand + " i kładę ją na stos Board numer: " + i);
							
							gameBoard.mozliweRuchy = countPossibilityMoves();
							cardOnHand = null;
							return true;
						}
					}

			
			
			
		}
	return false;
		//System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + " Ruchy już wykonane na tym poziomie: " + gameBoard.ruchyJuzWykonane);
	}
	
	public boolean zrobKrokZeStosuBoard(){
		boolean dokonanoRuchNaFinishBoard = false;
		
		for (int numberStack = 1; numberStack < 11; numberStack++){
			
			if (gameBoard.getSizeBoardStack(numberStack) > 0){
				cardOnHand = gameBoard.readCardFromStack("boardStack", numberStack);
				sourceCardOnHand = numberStack;
				
				for (int numberOfFinishStack = 0; numberOfFinishStack < 8; numberOfFinishStack++){
					if (isCompatibilityCardOnStackAndOnHand(numberOfFinishStack, "finishStack")) {
						//System.out.println("Można wziąć ze stosu Board numer: "  + sourceCardOnHand + " kartę " + cardOnHand + " i położyć na stos Finish numer: " + numberOfFinishStack);
						
						sprawdzoneDostepneRuchy++;
						if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
							cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
							gameBoard.ruchyJuzWykonane++;
							
							System.out.println("Sprawdzone dostępne ruchy: " + sprawdzoneDostepneRuchy + ". Ruchy już wykonane na tym poziomie: " + gameBoard.ruchyJuzWykonane + ". Mozliwe ruchy: " + gameBoard.mozliweRuchy);	
							
							step = new UndoStep(cardOnHand, numberStack, "finishStack", numberOfFinishStack, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
							gameBoard.pushCardToStack("finishStack", numberOfFinishStack, cardOnHand);
							gameBoard.pushUndo(step);	
							deepSteps ++;
							gameBoard.ruchyJuzWykonane = 0;
							
							System.out.println("ROBIE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Finish numer: " + numberOfFinishStack);
							dokonanoRuchNaFinishBoard = true;
							gameBoard.mozliweRuchy = countPossibilityMoves();
							cardOnHand = null;
							sourceCardOnHand = -1;
							return true;	
						}
					}
				}
				
				if (dokonanoRuchNaFinishBoard == false)
				for (int numberOfBoardStack = 1; numberOfBoardStack < 11; numberOfBoardStack++){	
					if (numberOfBoardStack == numberStack) continue;
					
					if (cardOnHand.getCardNumber() != 1)
						if (isCompatibilityCardOnStackAndOnHand(numberOfBoardStack, "boardStack")) {
							
							
							cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
							if (!isCompatibilityCardOnStackAndOnHand(sourceCardOnHand, "boardStack")){
								
								System.out.println("Żródłowy stos: " + sourceCardOnHand + " karty w ręku: " + cardOnHand + ". Czy karta może wrócić na swoje źródło: " + isCompatibilityCardOnStackAndOnHand(sourceCardOnHand, "boardStack"));
								
								sprawdzoneDostepneRuchy++;							
								if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
									
									gameBoard.ruchyJuzWykonane++;
									step = new UndoStep(cardOnHand, numberStack, "boardStack", numberOfBoardStack, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
									gameBoard.pushCardToStack("boardStack", numberOfBoardStack, cardOnHand);
									gameBoard.pushUndo(step);	
									deepSteps ++;
									gameBoard.ruchyJuzWykonane = 0;
									System.out.println("ROBIE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Board numer: " + numberOfBoardStack);
									
									gameBoard.mozliweRuchy = countPossibilityMoves();
									cardOnHand = null;
									sourceCardOnHand = -1;
									return true;
								}	
							}
							else 
								if (gameBoard.getSizeBoardStack(sourceCardOnHand) == 0)		{	
									sprawdzoneDostepneRuchy++;
									if (sprawdzoneDostepneRuchy == gameBoard.ruchyJuzWykonane + 1) {
										//cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
										gameBoard.ruchyJuzWykonane++;
										step = new UndoStep(cardOnHand, numberStack, "boardStack", numberOfBoardStack, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
										gameBoard.pushCardToStack("boardStack", numberOfBoardStack, cardOnHand);
										gameBoard.pushUndo(step);	
										deepSteps ++;
										gameBoard.ruchyJuzWykonane = 0;
										System.out.println("ROBIEE kolejny krok: biorę kartę ze stosu " + numberStack + ": " + cardOnHand + " i kładę ją na stos Board numer: " + numberOfBoardStack);
										
										gameBoard.mozliweRuchy = countPossibilityMoves();
										cardOnHand = null;
										sourceCardOnHand = -1;
										return true;	
									}	
								}
							gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
						}
							
				}
			
			}
			//gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
			cardOnHand = null;
			sourceCardOnHand = -1;
		}
		return false;
	}
	
	
	public void zrobUndo(){
		System.out.println("Robię UNDO");
		gameBoard.undoStep();	
		deepSteps--;
		gameBoard.countVisibleCardsOnLeftSide();
	
		
		//System.out.println("Liczba możliwych ruchów na tym poziomie (po UNDO): " + gameBoard.mozliweRuchy);
		//System.out.println("Liczba ruchów wykonanych na tym poziomie (po UNDO): " + gameBoard.ruchyJuzWykonane);
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
				if (isCompatibilityCardOnStackAndOnHand(i, "boardStack")) {
					cardOnHand = null;
					return true;	
				}
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack")) {
					cardOnHand = null;
					return true;			
				}
		}		
		cardOnHand = null;
		return false;
	}
	
	public int countPossibilityMoves(){	
		int countOfPossibilityMoves = 0;
		int count = 0;

		if (gameBoard.getSizeStartStack() > 0) {
			countOfPossibilityMoves = 1;
			//System.out.println("Można wziąć kartę ze stosu startowego");
		}
		count = possibleMovesFromZeroToAnyOther();
		if (count > 0) 
			countOfPossibilityMoves += count;	
		
		for (int numberBoardStack = 1; numberBoardStack < 11; numberBoardStack++) 	{				
			if (gameBoard.getSizeBoardStack(numberBoardStack) == 0) 
				continue;
			count = possibleMovesFromBoardToAnyOther(numberBoardStack);
			if (count > 0) 
				countOfPossibilityMoves += count;
		}	
				
		//System.out.println("Liczba możliwych ruchów na tym poziomie: " + countOfPossibilityMoves);
		return countOfPossibilityMoves;
	}
	
	public int possibleMovesFromZeroToAnyOther(){
		int count = 0;
		
		if (gameBoard.getSizeBoardStack(0) > 0){
			cardOnHand = gameBoard.readCardFromStack("boardStack", 0);
			
			for (int i = 0; i < 8; i++) 
				if (isCompatibilityCardOnStackAndOnHand(i, "finishStack")) 	{
					//System.out.println("Można wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Final numer: " + i);
					count++;
					break;
				}
			
			for (int i = 1; i <= 10; i++) 
				if (cardOnHand.getCardNumber() != 1)	
					if (isCompatibilityCardOnStackAndOnHand(i, "boardStack")) 	{
						//System.out.println("Można wziąć ze stosu zerowego kartę " + cardOnHand + " i położyć na stos Board numer: " + i);
						count++;
					}			
		}
		cardOnHand = null;
		return count;
	}
	
	public int possibleMovesFromBoardToAnyOther(int numberStack){		
		int count = 0;
		cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
		sourceCardOnHand = numberStack;
		
		for (int j = 0; j < 8; j++){
			if (isCompatibilityCardOnStackAndOnHand(j, "finishStack")) {
				//System.out.println("Można wziąć ze stosu Board numer: "  + sourceCardOnHand + " kartę " + cardOnHand + " i położyć na stos Finish numer: " + j);
				
				count++;
				break;
			}
		}
		
		for (int numberOfBoardStack = 1; numberOfBoardStack < 11; numberOfBoardStack++){	
			if (numberOfBoardStack == numberStack) continue;
				
			if (cardOnHand.getCardNumber() != 1)
			if (isCompatibilityCardOnStackAndOnHand(numberOfBoardStack, "boardStack")) 			
				if (!isCompatibilityCardOnStackAndOnHand(sourceCardOnHand, "boardStack")){
					//System.out.println("Można wziąć ze stosu Board numer: "  + sourceCardOnHand + " kartę " + cardOnHand + " i położyć na stos Board numer: " + numberOfBoardStack);
					count++;
				}
				else 
					if (gameBoard.getSizeBoardStack(sourceCardOnHand) == 0)		{	
						//System.out.println("Można wziąć ze stosu Board numer: "  + sourceCardOnHand + " kartę " + cardOnHand + " i położyć na stos Board numer: " + numberOfBoardStack);					
						count++;	
					}
		}
				
		gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
		cardOnHand = null;
		sourceCardOnHand = -1;
		return count;
	}
	
	public boolean isCardFromBoardFixToAnyOther(int numberStack){
		
		cardOnHand = gameBoard.getCardFromBoardStack(numberStack);
		sourceCardOnHand = numberStack;
		
		System.out.println("Testuję kartę: " + cardOnHand.getCard() + " ze stosu: " + sourceCardOnHand  + ". Jego aktualny stan: " + gameBoard.getSizeBoardStack(sourceCardOnHand) + " kart");
			
		for (int j = 0; j < 8; j++){
			if (isCompatibilityCardOnStackAndOnHand(j, "finishStack")) {
				System.out.println("Karta ze stoku " + sourceCardOnHand + " jest kompatybilna z kartą ze stoku górnego: " + j);
				gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
				cardOnHand = null;
				return true;
			}
		}	
		
		for (int numberOfBoardStack = 1; numberOfBoardStack < 11; numberOfBoardStack++){	
			if (numberOfBoardStack == sourceCardOnHand) continue;

			System.out.println("Sprawdzam kartę ze stosu " + numberOfBoardStack);
				
			if (isCompatibilityCardOnStackAndOnHand(numberOfBoardStack, "boardStack")) {
				
				System.out.println("Karta ze stoku " + sourceCardOnHand + " " + cardOnHand.getCard() + " jest kompatybilna z kartą ze stoku: " + numberOfBoardStack);
				
				if (!isCompatibilityCardOnStackAndOnHand(sourceCardOnHand, "boardStack")){
					System.out.println("Karta nie może wrócić na swoje miejsce");
					gameBoard.pushCardToStack("boardStack", numberStack, cardOnHand);
					cardOnHand = null;
					return true;
				}	
				else {
					if (gameBoard.getSizeBoardStack(sourceCardOnHand) > 0){
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
		
	public void pushOrBackCardOnHand(int numberOfStack, String typeStack){
			if (isCompatibilityCardOnStackAndOnHand(numberOfStack, typeStack)){
				step = new UndoStep(cardOnHand, sourceCardOnHand, typeStack, numberOfStack, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
				gameBoard.pushUndo(step);
				undoSteps.add(step);
				gameBoard.pushCardToStack(typeStack, numberOfStack, cardOnHand);					
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
			if (areas.isPressedStartStack(gameBoard, x, y) == -1) {
				if (gameBoard.getSizeStartStack() > 0 ) {
					step = new UndoStep(gameBoard.readCardFromStartStack(), -1, "boardStack", 0, gameBoard.ruchyJuzWykonane, gameBoard.mozliweRuchy);
					gameBoard.pushCardToStack("boardStack", 0, gameBoard.getCardFromStartStack());				
					gameBoard.pushUndo(step);
				}
				gameBoard.countVisibleCardsOnLeftSide();	
			}			
								
			// czy kliknieto obszar kart odlozonych - widoczne karty po lewej stronie - BoardStack stos 0-y
			if (areas.isPressedKartyOdlozoneStack(gameBoard, x, y)){
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
				if (areas.isActionOfBoardStack(gameBoard, x, y, i)){
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

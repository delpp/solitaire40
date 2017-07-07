package pasjans;
import javafx.application.Application;
import javafx.stage.Stage;

public class Startowa extends Application{
	
	@Override
	public void start(Stage primaryStage) throws CloneNotSupportedException{
		Pasjans gra = new Pasjans(primaryStage);
		gra.run();
	} 
	
	public static void main(String[] args) {
		launch(args);

	}
}

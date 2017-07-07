package pasjans;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class UndoSteps {
	private static int count = 0; 
	public Karta card;
	public int numberSource;
	public int numberTarget;
	public String typeTarget;
	private int id;
	
	public UndoSteps(){
		
	}
	
	public UndoSteps(Karta card, int numberSource, String typeTarget, int numberTarget){
		this.card = card;
		this.numberSource = numberSource;
		this.typeTarget = typeTarget;
		this.numberTarget = numberTarget;
		count++;
		id = count;
	}
	
	public IntegerProperty getIdProperty(){
		SimpleIntegerProperty idProperty = new SimpleIntegerProperty(id);
		return idProperty;
	}
}

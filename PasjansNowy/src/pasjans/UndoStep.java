package pasjans;

import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class UndoStep implements Serializable{
	private static int count = 0; 
	public Karta card;
	public int numberSource;
	public int numberTarget;
	public String typeTarget;
	public int numberOfPossiblityMoves = 0;
	private int id;
	
	public UndoStep(){
		
	}
	
	public UndoStep(Karta card, int numberSource, String typeTarget, int numberTarget, int numberOfPossiblityMoves){
		this.card = card;
		this.numberSource = numberSource;
		this.typeTarget = typeTarget;
		this.numberTarget = numberTarget;
		this.numberOfPossiblityMoves = numberOfPossiblityMoves;
		count++;
		id = count;
	}
	
	public IntegerProperty getIdProperty(){
		SimpleIntegerProperty idProperty = new SimpleIntegerProperty(id);
		return idProperty;
	}
}

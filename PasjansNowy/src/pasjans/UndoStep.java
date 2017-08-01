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
	public int ruchyJuzWykonane = 0;
	public int mozliweRuchy = 0;
	private int id;
	
	public UndoStep(){
		
	}
	
	public UndoStep(Karta card, int numberSource, String typeTarget, int numberTarget, int ruchyJuzWykonane, int mozliweRuchy){
		this.card = card;
		this.numberSource = numberSource;
		this.typeTarget = typeTarget;
		this.numberTarget = numberTarget;
		this.ruchyJuzWykonane = ruchyJuzWykonane;
		this.mozliweRuchy = mozliweRuchy;
		count++;
		id = count;
	}
	
	public IntegerProperty getIdProperty(){
		SimpleIntegerProperty idProperty = new SimpleIntegerProperty(id);
		return idProperty;
	}
}

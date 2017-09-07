package pasjans;

import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class UndoStep implements Serializable {
	private static int count = 0;
	public Karta card;
	public int numberSource;
	public int numberTarget;
	public String typeTarget;
	public int ruchyJuzWykonane = 0;
	public int mozliweRuchy = 0;
	private int id;
	public double xSourceAnimation, ySourceAnimation;
	public double xTargetAnimation, yTargetAnimation;
	private int positionInSourceBoard;

	public UndoStep() {

	}

	public UndoStep(Karta card, int numberSource, String typeTarget, int numberTarget, int ruchyJuzWykonane,
			int mozliweRuchy) {
		this.card = card;
		this.numberSource = numberSource;
		this.typeTarget = typeTarget;
		this.numberTarget = numberTarget;
		this.ruchyJuzWykonane = ruchyJuzWykonane;
		this.mozliweRuchy = mozliweRuchy;
		//this.positionInSourceBoard = positionInSourceBoard;
		count++;
		id = count;

	/*	if (typeTarget.equals("boardStack")){
			xSourceAnimation = numberTarget * 75 + 24;
		}
		else if (typeTarget.equals("finishStack"))
			xSourceAnimation = numberTarget * 75 + 249;
		else
			xSourceAnimation = 9;



		if (typeTarget.equals("finishStack"))
			ySourceAnimation = 10;
		
		
		if (typeTarget.equals("boardStack"))
			if (numberTarget == 0) {
				if (positionInSourceBoard >= 10)
					ySourceAnimation = 280;
				else if (positionInSourceBoard < 10)
					ySourceAnimation = 10 + positionInSourceBoard * 30 - 30;
			} else
				ySourceAnimation = 179 + 30 * positionInSourceBoard - 30;
		
		
		
		
		
		if (numberSource > 0){
			xTargetAnimation = numberSource * 75 + 24;
			yTargetAnimation = 179 + 30 * positionInSourceBoard;
		}
		else if (numberSource == 0){
			xTargetAnimation = 9;
			if (positionInSourceBoard < 10)
				yTargetAnimation = 10 + positionInSourceBoard * 30;
			else
				yTargetAnimation = 280;
		}
		else{
			xTargetAnimation = 99;
			yTargetAnimation = 10;
		}*/
		
		

/*		if (numberSource > 0)
			yTargetAnimation = 179 + 30 * positionInSourceBoard;
		else if (numberSource == 0) {
			if (positionInSourceBoard < 10)
				yTargetAnimation = 10 + positionInSourceBoard * 30;
			else
				yTargetAnimation = 280;
		} else
			yTargetAnimation = 10;*/
	}

}

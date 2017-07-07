package pasjans;

import java.util.Arrays;
import java.util.Stack;

public class Karta implements Cloneable{
	public static int[] cardNumbers;			
	
	public static final int CARDWIDTH = 66;
	public static final int CARDHEIGHT = 94;
	
	private int cardNumber = 0;
	private String cardType = "";
	
	public Karta(){
		cardNumber = 0;
		cardType = "";
	}
	
	public Karta(int number, String type){
		cardNumber = number;
		cardType = type;
	}
	
	public void setCard(int number, String type){
		cardNumber = number;
		cardType = type;
	}
	
	public String getCard(){
		return cardType + cardNumber;
	}
	
	public int shiftVertical() { 
		return cardNumber*Karta.CARDHEIGHT - Karta.CARDHEIGHT; 
	}
	
	public int shiftHorizontal() { 
		if (cardType.equals("pik")) return Karta.CARDWIDTH - Karta.CARDWIDTH;
		else if (cardType.equals("karo")) return 2*Karta.CARDWIDTH - Karta.CARDWIDTH;
		else if (cardType.equals("trefl")) return 3*Karta.CARDWIDTH - Karta.CARDWIDTH;
		else if (cardType.equals("kier")) return 4*Karta.CARDWIDTH - Karta.CARDWIDTH;
		return 0;		
	}

	public boolean canPutCardToIncreaseStock(Karta underCard){
		if ((this.cardType.equals(underCard.cardType))
			&& (this.cardNumber == underCard.cardNumber+1)) return true;
		return false;
	}
	
	public boolean canPutCardToDecreaseStack(Karta underCard){
		if ((this.cardType.equals(underCard.cardType))
			&& (this.cardNumber == underCard.cardNumber-1)) return true;
		return false;
	}
	
	@Override
	public String toString(){
		return cardNumber + " " + cardType;
	}
	
	@Override
	public Object clone() {
		try{
			return super.clone();
		}
		catch (Exception e){
			return null;
		}
	}
		
}

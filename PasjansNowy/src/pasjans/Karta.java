package pasjans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Stack;

public class Karta implements Cloneable, Serializable{
	public static int[] cardNumbers;			
	
	public static final int CARDWIDTH = 66;
	public static final int CARDHEIGHT = 94;
	
	private int cardNumber = 0;
	private String cardType = "";
	private int sourceStack;
	private int positionOnStack;
	private Karta cardUnder;
	
	public Karta(){
		cardNumber = 0;
		cardType = "";
		sourceStack = -1;
		cardUnder = null;
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
	
	public int getCardNumber(){
		return cardNumber;
	}
	
	public void setSourceStack(int sourceStack){
		this.sourceStack = sourceStack;
	}
	
	public int readSourceStack(){
		return sourceStack;
	}
	
	public Karta readCardUnder(){
		return cardUnder;
	}
	
	public void setCardUnder(Karta cardUnder){
		this.cardUnder = cardUnder;
	}
	
	public void setPositionOnStack(int position){
		positionOnStack = position;
	}
	
	public int readPositionOnStack(){
		return positionOnStack;
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
		if (cardNumber ==1) return "As " + cardType;
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

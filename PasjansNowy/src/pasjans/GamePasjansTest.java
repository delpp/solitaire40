package pasjans;

import static org.junit.Assert.*;

import org.junit.Test;

public class GamePasjansTest {
	private Karta card;
	private GameBoard gameBoard;
	

	@Test
	public void givenEmptyCard_WhenGettingCardNumber_ThenReturnedZero() {		
		card = new Karta();
		
		int number = card.shiftVertical();
		
		assertEquals(0, number);
	}
	

	
	
	@Test
	public void givenEmptyCard_WhenGettingCardType_ThenReturnedCardType() {
		card = new Karta();
		
		int type = card.shiftHorizontal();
		
		assertEquals(0, type);
	}
		
	@Test
	public void givenCardTypePIK_WhenGettingCardType_ThenReturnedCardType() {
		card = new Karta(5, "pik");
		
		int type = card.shiftHorizontal();
		
		assertEquals(1, type);
	}
	
	@Test
	public void givenCardTypeKARO_WhenGettingCardType_ThenReturnedCardType() {
		card = new Karta(5, "karo");
		
		int type = card.shiftHorizontal();
		
		assertEquals(2, type);

	}
	
	@Test
	public void givenCardTypeTREFL_WhenGettingCardType_ThenReturnedCardType() {
		card = new Karta(5, "trefl");
		
		int type = card.shiftHorizontal();
		
		assertEquals(3, type);
	}
	
	@Test
	public void givenCardTypeKIER_WhenGettingCardType_ThenReturnedCardType() {
		card = new Karta(5, "kier");
		
		int type = card.shiftHorizontal();
		
		assertEquals(4, type);
	}
	

	@Test
	public void givenTwoCards_WhenSettingNumberAndType_ThenReturnedFullCard(){
		card = new Karta();		
		card.setCard(5, "pik");
		
		String fullCard = card.getCard();
		
		assertEquals("pik5", fullCard);		
	}
	
	@Test
	public void givenStartingGameBoard_WhenGettingSizeOfStartStack_Return64() {
		gameBoard = new GameBoard();
		
		int i = gameBoard.getSizeStartStack();
		
		assertEquals(64, i);

	}
	
	
	@Test
	public void givenStartingGameBoard_WhenReadCardFromStartStack_ReturnNotEmptyCard() {
		gameBoard = new GameBoard();
		
		card = gameBoard.readCardFromStartStack();
		
		assertFalse(card.equals(null));

	}

	
}





	
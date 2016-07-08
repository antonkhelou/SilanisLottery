/*
 * Simple data class that represents a winner in the lottery by a tuple
 * that contains the firstName and winnings.
 */
public class LottoWinnerTuple {
	private String firstName;
	private int winnings;
	
	public LottoWinnerTuple(String firstName, int winnings){
		this.firstName = firstName;
		this.winnings = winnings;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public int getWinnings() {
		return winnings;
	}
}

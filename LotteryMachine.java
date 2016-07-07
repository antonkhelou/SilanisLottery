import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * GENERAL COMMENTS FOR REVIEWER:
 * 
 * The main thing I am not satisfied with is the tight coupling in the implementation between
 * NUM_BALLS_DRAWN, the prize constants (i.e. FIRST_PLACE_PRCT, ...) and the instance variables
 * for the winners/winnings. This leads to an ugly implementation inside the distributePrizes() method,
 * where I have code repeat due to the fact that I have defined variables separately rather than
 * holding them all in a data structure, and applying the computation against it.
 * 
 * To elaborate, ideally, what I should have done here, is create a List that holds as many
 * "winning percentages" as the developer/lottery manager would want to use in his lottery system.
 * In parallel, I would dynamically create List that holds tuples defined by <first_name, winning>
 * and is ordered by first prize to the last prize, where winning is based on the size of the "winning percentages" 
 * list I mentioned above. "The winning percentages" list would also indicate the number of balls to be drawn
 * (no longer need the NUM_BALLS_DRAWN constant) through its .size(). I would also have to verify that
 * the total sum of the elements in the "winning percentages" list is equal to 1.0f, in order to properly 
 * distribute the current draw's prize pot.
 */

public class LotteryMachine {
	public static final int NUM_BALLS = 50; //has to be greater or equal to NUM_BALLS_DRAWN
	public static final int NUM_BALLS_DRAWN = 3;
	public static final int DRAW_TICKET_PRICE = 10;
	
	public static final float FIRST_PLACE_PRCT = 0.75f;
	public static final float SECOND_PLACE_PRCT = 0.15f;
	public static final float THIRD_PLACE_PRCT = 0.1f;
	public static final float DRAW_POT_PRCT = 0.5f;
	
	private int prizePot;
	
	//The following 2 sets of instance variables will serve as holders for the lotto winners after a draw() has
	//been executed and will maintain their state until another draw() is executed.
	private String latestFirstPlaceWinner, latestSecondPlaceWinner, latestThirdPlaceWinner;
	private int latestFirstPlaceWinnings, latestSecondPlaceWinnings, latestThirdPlaceWinnings;
	
	//Map that associates ticket(draw) numbers to their buyer's first name.
	private Map<Integer, String> tickets;
	private List<Integer> latestDrawNumbers;
	
	//Random object. Decided to make it an instance variable to avoid constantly creating new instances every
	//time generateRandomNumberRange() is called.
	private Random rand;
	
	public LotteryMachine(){
		this.prizePot = 200;
		
		this.latestFirstPlaceWinner = "N/A";
		this.latestSecondPlaceWinner = "N/A";
		this.latestThirdPlaceWinner = "N/A";
		
		this.latestFirstPlaceWinnings = 0;
		this.latestSecondPlaceWinnings = 0;
		this.latestThirdPlaceWinnings = 0;
		
		this.tickets = new HashMap<Integer, String>();
		this.latestDrawNumbers = new ArrayList<Integer>(NUM_BALLS_DRAWN);
		
		this.rand = new Random();
	}

	/*
	 * The following method implements the draw functionality for the LotteryMachine.
	 * It generates a NUM_BALLS_DRAWN amount of random numbers which are then used to
	 * determine who has winning tickets for the current draw. The 'latestDrawNumbers'
	 * ArrayList holds the drawn numbers in order of 1st prize up to NUM_BALLS_DRAWNth prize.
	 */
	public List<Integer> draw(){
		int drawNumber;
		int numBallsDrawn = 0;
		
		//Reset the drawn numbers from any previous draw()
		this.latestDrawNumbers = new ArrayList<Integer>(NUM_BALLS_DRAWN);
		
		//The following block ensures that NUM_BALLS amount of unique values 
		//are being randomly generated.
		do{
			drawNumber = generateRandomNumberRange(1, NUM_BALLS);
			
			if(latestDrawNumbers.indexOf(drawNumber) == -1){
				this.latestDrawNumbers.add(drawNumber);
				numBallsDrawn++;
			}	
			
		} while(numBallsDrawn < NUM_BALLS_DRAWN);
		
		distributePrizes();
		resetDraw();
		
		return this.latestDrawNumbers;
	}
	
	/*
	 * The following method implements the functionality of the LotteryMachine.
	 * It basically creates a ticket by randomly generating an unused number
	 * and mapping it to the firstName parameter inputing into the method, and
	 * finally increases the prizePot.
	 */
	public int purchaseDraw(String firstName) throws DrawNotAvailableException{
		int drawNumber = generateRandomAvailableTicketNumber();
		
		tickets.put(drawNumber, firstName);
		prizePot += DRAW_TICKET_PRICE;
		
		return drawNumber;
	}

	public int getPrizePot(){
		return prizePot;
	}
	
	public List<Integer> getLatestDrawNumbers(){
		//Integer objects are immutable, so nothing to worry about.
		return latestDrawNumbers;
	}
	
	public String getLatestFirstPlaceWinner() {
		return latestFirstPlaceWinner;
	}

	public String getLatestSecondPlaceWinner() {
		return latestSecondPlaceWinner;
	}

	public String getLatestThirdPlaceWinner() {
		return latestThirdPlaceWinner;
	}

	public int getLatestFirstPlaceWinnings() {
		return latestFirstPlaceWinnings;
	}

	public int getLatestSecondPlaceWinnings() {
		return latestSecondPlaceWinnings;
	}

	public int getLatestThirdPlaceWinnings() {
		return latestThirdPlaceWinnings;
	}
	
	///////////////////////////////////PRIVATE METHODS////////////////////////////////////////////////
	
	/*
	 * The following method implements the functionality responsible for determining who has
	 * won the prizes and their winnings.
	 */
	private void distributePrizes(){
		//Variable used to subtract from the prizePool at the end of the method
		int totalDrawPrize = 0;
		String tempFirstName = "";
		
		//The following repeated block of code checks if someone has a ticket of the winning number.
		//If someone does, then it will determine his name and compute his winnings based on constants.
		//It is tightly coupled with the prize order inside the 'latestDrawNumbers' ArrayList.
		if((tempFirstName = tickets.get(this.latestDrawNumbers.get(0))) != null){
			this.latestFirstPlaceWinner = tempFirstName;
			this.latestFirstPlaceWinnings = (int) Math.ceil(this.prizePot * DRAW_POT_PRCT * FIRST_PLACE_PRCT);
			totalDrawPrize += this.latestFirstPlaceWinnings;
		}
		else{
			//set the name to "N/A" and reset the winnings to 0
			this.latestFirstPlaceWinner = "N/A";
			this.latestFirstPlaceWinnings = 0;
		}

		if((tempFirstName = tickets.get(this.latestDrawNumbers.get(1))) != null){
			this.latestSecondPlaceWinner = tempFirstName;
			this.latestSecondPlaceWinnings = (int) Math.ceil(this.prizePot * DRAW_POT_PRCT * SECOND_PLACE_PRCT);
			totalDrawPrize += this.latestSecondPlaceWinnings;
		}
		else{
			this.latestSecondPlaceWinner = "N/A";
			this.latestSecondPlaceWinnings = 0;
		}
		
		if((tempFirstName = tickets.get(this.latestDrawNumbers.get(2))) != null){
			this.latestThirdPlaceWinner = tempFirstName;
			this.latestThirdPlaceWinnings = (int) Math.ceil(this.prizePot * DRAW_POT_PRCT * THIRD_PLACE_PRCT);
			totalDrawPrize += this.latestThirdPlaceWinnings;
		}
		else{
			this.latestThirdPlaceWinner = "N/A";
			this.latestThirdPlaceWinnings = 0;
		}
		
		//subtract the total distributed prize money from the pot
		prizePot -= totalDrawPrize;
	}
	
	private void resetDraw(){
		this.tickets = new HashMap<Integer, String>();
	}

	/*
	 * Checks the size of the Map against the NUM_BALLS constant to determine
	 * if there are any tickets available in the current draw
	 */
	private boolean availableTicketsCurrentDraw(){
		return (this.tickets.size() < NUM_BALLS);
	}
	
	/*
	 * The following method generates a random ticket number that is currently not purchased
	 * by anybody (i.e used in the draw). It also throws an DrawNotAvailableException 
	 * in the events that are no more tickets available for the current draw.
	 */
	private int generateRandomAvailableTicketNumber() throws DrawNotAvailableException{		
		if(!availableTicketsCurrentDraw()){
			throw new DrawNotAvailableException();
		}
		
		int randomNumber;
		
		//The following block keeps generating random numbers until it finds one
		//that has not been purchased.
		do{
			randomNumber = generateRandomNumberRange(1, NUM_BALLS);
		} while(tickets.get(randomNumber) != null);
		
		return randomNumber;
	}
	
	private int generateRandomNumberRange(int min, int max){
		return rand.nextInt(max) + min;
	}
}

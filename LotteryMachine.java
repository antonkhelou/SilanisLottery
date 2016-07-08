import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LotteryMachine {
	public static final int NUM_BALLS = 50;
	public static final int DRAW_TICKET_PRICE = 10;
	
	//Default percentage constants for standard lottery with 3 winners used in empty constructor
	public static final float FIRST_PLACE_PRCT = 0.75f;
	public static final float SECOND_PLACE_PRCT = 0.15f;
	public static final float THIRD_PLACE_PRCT = 0.1f;
	
	public static final float DRAW_POT_PRCT = 0.5f;
	
	private int prizePot;
	private int numNumbersDrawn;
	
	//Map that associates ticket(draw) numbers to their buyer's first name.
	private Map<Integer, String> tickets;
	private List<Integer> latestDrawNumbers;
	
	//Random object. Decided to make it an instance variable to avoid constantly creating new instances every
	//time generateRandomNumberRange() is called.
	private Random rand;
	
	//Holds a list of the winning percentages. The size of this list indicates the number of balls/numbers
	//to be drawn in each draw
	private List<Float> winningPercentages;
	//List of tuples that contain the first name of the winner and his winnings
	private List<LottoWinnerTuple> latestWinners;
	
	/*
	 * Default constructor, uses the first, second and third place percentage constants
	 */
	public LotteryMachine() throws InvalidWinningPercentagesException{
		this(new ArrayList<Float>(Arrays.asList(FIRST_PLACE_PRCT, SECOND_PLACE_PRCT, THIRD_PLACE_PRCT)));
	}
	
	/*
	 * Overloaded constructor, takes in a winningPercentages List as a parameter
	 */
	public LotteryMachine(List<Float> winningPercentages) throws InvalidWinningPercentagesException{
		//checks if the percentages total up to 1.0f
		if(!isPercentageListValid(winningPercentages))
			throw new InvalidWinningPercentagesException();
		
		this.prizePot = 200;
		//save the size of the winningPercentages, which represents the number of balls to be drawn per draw
		this.numNumbersDrawn = winningPercentages.size();
		
		this.tickets = new HashMap<Integer, String>();
		this.latestDrawNumbers = new ArrayList<Integer>(winningPercentages.size());
		this.latestWinners = new ArrayList<LottoWinnerTuple>(winningPercentages.size());
		
		this.winningPercentages = winningPercentages;
		
		this.rand = new Random();
	}

	/*
	 * The following method implements the draw functionality for the LotteryMachine.
	 * It generates a winningPercentages.size()/numNumbersDrawn amount of random numbers which are then used to
	 * determine who has winning tickets for the current draw. The 'latestDrawNumbers'
	 * list holds the drawn numbers in order of 1st prize up to numNumbersDrawn-th prize.
	 */
	public List<Integer> draw(){
		int drawNumber;
		int numBallsDrawn = 0;
		
		//Reset the drawn numbers from any previous draw()
		this.latestDrawNumbers = new ArrayList<Integer>(this.numNumbersDrawn);
		
		//The following block ensures that numNumbersDrawn amount of unique values 
		//are being randomly generated.
		do{
			drawNumber = generateRandomNumberRange(1, NUM_BALLS);
			
			if(latestDrawNumbers.indexOf(drawNumber) == -1){
				this.latestDrawNumbers.add(drawNumber);
				numBallsDrawn++;
			}	
			
		} while(numBallsDrawn < this.numNumbersDrawn);
		
		resetLatestWinners();
		distributePrizes();
		resetTickets();
		
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
		return this.latestWinners.get(0).getFirstName();
	}

	public String getLatestSecondPlaceWinner() {
		return this.latestWinners.get(1).getFirstName();
	}

	public String getLatestThirdPlaceWinner() {
		return this.latestWinners.get(2).getFirstName();
	}
	
	public String getLatestNthPlaceWinner(int n) {
		return this.latestWinners.get(n).getFirstName();
	}

	public int getLatestFirstPlaceWinnings() {
		return this.latestWinners.get(0).getWinnings();
	}

	public int getLatestSecondPlaceWinnings() {
		return this.latestWinners.get(1).getWinnings();
	}

	public int getLatestThirdPlaceWinnings() {
		return this.latestWinners.get(2).getWinnings();
	}
	
	public int getLatestNthPlaceWinnings(int n) {
		return this.latestWinners.get(n).getWinnings();
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
		int tempWinnings = 0;
		
		//The following block of code loops through the latestDrawNumbers and winningPercentages
		//lists, determines whether there is a person that has purchased a winning number and
		//computes his/her winnings.
		for(int i = 0; i < this.numNumbersDrawn; i++){
			if((tempFirstName = tickets.get(this.latestDrawNumbers.get(i))) != null){
				tempWinnings = (int) Math.ceil(this.prizePot * DRAW_POT_PRCT * this.winningPercentages.get(i));
				this.latestWinners.add(new LottoWinnerTuple(tempFirstName, tempWinnings));
				totalDrawPrize += tempWinnings;
			}
			else{
				this.latestWinners.add(new LottoWinnerTuple("N/A", 0));
			}
		}
		
		//subtract the total distributed prize money from the pot
		prizePot -= totalDrawPrize;
	}
	
	private void resetLatestWinners(){
		this.latestWinners = new ArrayList<LottoWinnerTuple>(this.numNumbersDrawn);
	}
	
	private void resetTickets(){
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
	
	/*
	 * Static helper method that checks whether the total percent of the percentagesList parameter
	 * list totals to 1.0f
	 */
	private static boolean isPercentageListValid(List<Float> percentagesList){
		float totalPercentages = 0.0f;
		
		for(Float prct : percentagesList){
			totalPercentages += prct;
		}
		
		return (totalPercentages == 1.0f);
	}
}

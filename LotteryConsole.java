import java.util.List;
import java.util.Scanner;

public class LotteryConsole {

	public static void main(String[] args) {
		printLotteryMainMenu();
	}

	public static void printLotteryMainMenu(){
		Scanner inputScanner = new Scanner(System.in);
		
		LotteryMachine lottoMachine = null;
		
		try {
			lottoMachine = new LotteryMachine();
		} catch (InvalidWinningPercentagesException e1) {
			System.out.println("Error: The winning percentages do not add up to 1.0!");
		}
		
		if(lottoMachine != null){
			String selectedOption;
			String menu, participantName = "";
			boolean isFinished = false;
			
			//The following block of code will keep looping until the user types "4" or "quit"
			do{
				menu = String.format("%s %n 1. %s %n 2. %s %n 3. %s %n 4. %s %n",
													"Silanis Lottery! Please select an option by typing the number or the name:",
													"purchase",
													"draw",
													"winners",
													"quit");
				System.out.print(menu);
	
				selectedOption = inputScanner.next();
				
				if(selectedOption.equals("1") || selectedOption.contains("purchase")){
					int lottoNumber = -1;
					menu = String.format("%n%s %n",
							"Please enter the first name of the participant.");
					
					System.out.print(menu);
					
					participantName = inputScanner.next();
					
					try {
						lottoNumber = lottoMachine.purchaseDraw(participantName);
						
						String purchaseOutput = String.format("%nLotto Number for %s : %d",
								participantName,
								lottoNumber);
						
						System.out.print(purchaseOutput);
					} catch (DrawNotAvailableException e) {
						System.out.println();
						System.out.print("Error: There are no more draws to be sold. Please wait for the next round!");
					} catch (InvalidFirstNameException e) {
						System.out.println();
						System.out.print("Error: Please enter a first name.");
					}
				}
				else if(selectedOption.equals("2") || selectedOption.contains("draw")){
					List<Integer> drawnNumbers = lottoMachine.draw();
					
					//The following prints the drawn numbers in order of first prize to last prize
					System.out.println();
					for(int i = 0; i < drawnNumbers.size(); i++){
						System.out.print(drawnNumbers.get(i) + " ");
					}
				}
				else if(selectedOption.equals("3") || selectedOption.contains("winners")){
					String resultsOutput = String.format("%n%s\t%s\t%s%n%d\t%d\t%d",
							lottoMachine.getLatestFirstPlaceWinner(),
							lottoMachine.getLatestSecondPlaceWinner(),
							lottoMachine.getLatestThirdPlaceWinner(),
							lottoMachine.getLatestFirstPlaceWinnings(),
							lottoMachine.getLatestSecondPlaceWinnings(),
							lottoMachine.getLatestThirdPlaceWinnings());
					
					System.out.print(resultsOutput);
				}
				else if(selectedOption.equals("4") || selectedOption.contains("quit")){
					isFinished = true;
				}
				else{
					System.out.println();
					System.out.print("Invalid option!");
				}
				
				System.out.println();
				System.out.println("---------------------------------------------------------------------------------------------");
				
			} while (!isFinished);
		}
		
		inputScanner.close();
		System.out.println("Thank you for using the Silanis Lottery, have a good day!");
	}
}

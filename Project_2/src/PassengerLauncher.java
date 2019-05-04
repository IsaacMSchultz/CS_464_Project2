// Isaac Schultz 11583435
// Publisher launching is handled within this file.

public class PassengerLauncher {
	public static void main(String[] args) {
			if (args.length != 3)
				System.err.println("Invalid arguments!");
			
			Passenger passenger = new Passenger(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			passenger.start();
	}		
}

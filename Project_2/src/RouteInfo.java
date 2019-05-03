// Isaac Schultz
// Helper class to store routeInfo
// Has print built in for easy debugging!

public class RouteInfo {
	private int numVehicles;
	private int numRuns;
	private int numStops;
	private int accidentProbability; // 1 out of this number.
	private double timeBetweenStops;
	private String routeName;
	private String busName;

	public RouteInfo(int numVehicles, int numRuns, int numStops, int timeBetweenStops, String routeName, String busName, int accidentProbability) {
		this.numVehicles = numVehicles;
		this.numRuns = numRuns;
		this.numStops = numStops;
		this.timeBetweenStops = timeBetweenStops;
		this.routeName = routeName;
		this.busName = busName;
		this.accidentProbability = accidentProbability;
	}

	public int Vehicles() {
		return numVehicles;
	}

	public int Runs() {
		return numRuns;
	}

	public int Stops() {
		return numStops;
	}

	public double TimeBetweenStops() {
		return timeBetweenStops;
	}

	public String Name() {
		return routeName;
	}

	public String BusName() {
		return busName;
	}
	
	public int AccidentProbability() {
		return accidentProbability;
	}

	public void Print() // WOW! it can print itself!!!!
	{
		System.out.println("numVehicles: " + Integer.toString(numVehicles) + "\n" + "numRuns: "
				+ Integer.toString(numRuns) + "\n" + "numStops: " + Integer.toString(numStops) + "\n"
				+ "timeBetweenStops: " + Double.toString(timeBetweenStops) + "\n" + "routeName: " + routeName
				+ "\nbusName: " + busName);
	}
}
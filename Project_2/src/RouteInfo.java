// Isaac Schultz
// Helper class to store routeInfo
// Has print built in for easy debugging!

public class RouteInfo {
	private int numVehicles;
	private int numRuns;
	private int numStops;
	private int timeBetweenStops;
	private String routeName;
	private String busName;

	public RouteInfo(int numVehicles, int numRuns, int numStops, int timeBetweenStops, String routeName, String busName) {
		this.numVehicles = numVehicles;
		this.numRuns = numRuns;
		this.numStops = numStops;
		this.timeBetweenStops = timeBetweenStops;
		this.routeName = routeName;
		this.busName = busName; // does a deep copy
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

	public int TimeBetweenStops() {
		return timeBetweenStops;
	}

	public String Name() {
		return routeName;
	}

	public String BusName() {
		return busName;
	}

	public void Print() // WOW! it can print itself!!!!
	{
		System.out.println("numVehicles: " + Integer.toString(numVehicles) + "\n" + "numRuns: "
				+ Integer.toString(numRuns) + "\n" + "numStops: " + Integer.toString(numStops) + "\n"
				+ "timeBetweenStops: " + Integer.toString(timeBetweenStops) + "\n" + "routeName: " + routeName
				+ "\nbusName: " + busName);
	}
}
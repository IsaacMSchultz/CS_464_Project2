// Isaac Schultz 11583435
// Publisher launching is handled within this file.

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

public class PubLauncher {
	public static void main(String[] args) {

		// Read from the properties file and start threads from it
		try (InputStream input = new FileInputStream("pub.properties")) {
			// load a properties file
			Properties prop = new Properties();		
			prop.load(input);
			
			// Variables to start threads.
			int numRoutes;
			int numVehicles;
			int numRuns;
			int numThreads;
			int curThread = 0;
			PubThread[] pubThreads;
			
			// Variables parsed from each route that is used to launch individual threads
			int numStops;
			int timeBetweenStops;
			String routeName;
			String busName;
			String currRoutePrefix;
			RouteInfo info;
			
			//Parsing data from the properties file
			numRoutes = Integer.parseInt(prop.getProperty("numRoutes", "0")); // stores the number of routes as an integer, default 0
			numVehicles = Integer.parseInt(prop.getProperty("numVehicles", "0")); // stores the number of vehicles as an integer, default 0
			numRuns = Integer.parseInt(prop.getProperty("numRuns", "0")); // stores the number of runs as an integer, default 0
			numThreads = numRoutes * numVehicles;
			pubThreads = new PubThread[numThreads]; // make an array of enough threads.
			

			//Parse out the data for all the routes and then start a thread for that route
			for (int route = 0; route < numRoutes; route++)
			{
				currRoutePrefix = "route" + Integer.toString(route + 1); //set the name of the current route to access in the properties file
				//System.out.println(currRoutePrefix);
						
				numStops = Integer.parseInt(prop.getProperty(currRoutePrefix + "numStops", "0"));
				timeBetweenStops = Integer.parseInt(prop.getProperty(currRoutePrefix + "TimeBetweenStops", "0"));
				routeName = prop.getProperty(currRoutePrefix);
				
				for (int nameNum = 1; nameNum < numVehicles + 1; nameNum++)
				{
					busName = prop.getProperty(currRoutePrefix + "Vehicle" + Integer.toString(nameNum)); //add 1 since we start at 0
					info = new RouteInfo(numVehicles, numRuns, numStops, timeBetweenStops, routeName, busName); // put all our data into a routeinfo object
					//info.Print();
					pubThreads[curThread] = new PubThread(info); //create a thread for it, but don't start it yet
					curThread++;
				}
				//System.out.println(route);
			}
			
			
			
			for (int i = 0; i < numThreads; i++)
			{
				System.out.println(i);
				pubThreads[i].start();
			}
			
			//Don't really need to wait on join since starting threads is the last thing this class does.
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}		
}

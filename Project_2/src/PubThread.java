
// Isaac Schultz
// Copy/paste of publisher example but with tweaks for project 2

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PubThread extends Thread {
	RouteInfo routeInfo;

	public PubThread(RouteInfo info) {
		routeInfo = info;
	}

	public double TimeBetweenStops() {
		return routeInfo.TimeBetweenStops();
	}

	// function that a thread uses to execute. From this point on the class runs as
	// a thread
	// Is called through PubThread.start()
	public void run() {
		DomainParticipant participant = null;
		Publisher publisher = null;
		Topic positionTopic = null;
		Topic accidentTopic = null;
		PositionDataWriter positionWriter = null;
		AccidentDataWriter accidentWriter = null;
		Random rand = new Random();
		int trafficNum = 0;

		System.out.println("Thread " + routeInfo.BusName() + " Started!");

		try {
			// --- Create participant --- //

			participant = DomainParticipantFactory.TheParticipantFactory.create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (participant == null) {
				System.err.println("create_participant error\n");
				return;
			}

			// --- Create publisher --- //

			publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (publisher == null) {
				System.err.println("create_publisher error\n");
				return;
			}

			// --- Create topic --- //

			/* Register type before creating topic */
			//we publish both accidents and positions from this file.
			String positionTypeName = PositionTypeSupport.get_type_name();
			PositionTypeSupport.register_type(participant, positionTypeName);

			String accidentTypeName = AccidentTypeSupport.get_type_name();
			AccidentTypeSupport.register_type(participant, accidentTypeName);

			//set up different topics for both so that subscribers dont have to get both accident and position.

			positionTopic = participant.create_topic("P2464_ischultz:POS", positionTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (positionTopic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			accidentTopic = participant.create_topic("P2464_ischultz:ACC", accidentTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (accidentTopic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			// --- Create writer --- //

			positionWriter = (PositionDataWriter) publisher.create_datawriter(positionTopic, Publisher.DATAWRITER_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (positionWriter == null) {
				System.err.println("create_datawriter error\n");
				return;
			}

			accidentWriter = (AccidentDataWriter) publisher.create_datawriter(accidentTopic, Publisher.DATAWRITER_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (accidentWriter == null) {
				System.err.println("create_datawriter error\n");
				return;
			}

			// --- Write --- //

			/* Create data sample for writing */
			Position positionInstance = new Position(routeInfo); // Modified Position and Accident classes so they can
																	// accept RouteInfo to set some default attributes
			Accident accidentInstance = new Accident(routeInfo);

			InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
			/*
			 * For a data type that has a key, if the same instance is going to be written
			 * multiple times, initialize the key here and register the keyed instance prior
			 * to writing
			 */

			final long tenSeconds = 10000;
			long betweenStops = (long) (routeInfo.TimeBetweenStops() * 1000); // time between stops in ms
			long actualWaitTime = betweenStops;
			SimpleDateFormat timeStamper = new SimpleDateFormat("h:mm:ss a"); // time format object that will take
																				// number of miliseconds and turn it
																				// into a readable timestamp
			boolean isAccident = false;

			for (int round = 0; round < routeInfo.Runs(); ++round) { //only loop through the number of runs specified!
				for (int stop = 1; stop < routeInfo.Stops() + 1; ++stop) {
					actualWaitTime = betweenStops; // reset the wait time
					positionInstance.trafficConditions = "Normal"; // reset traffic conditions.

					// roll the dice on an accident as we begin to leave the bus stop. 0 has a 1 in
					// 10 (10%) chance of being the output if the range input is 10!
					if (rand.nextInt(routeInfo.AccidentProbability()) == 0) // uses the probability parsed from the .properties file
					{
						accidentInstance.stopNumber = stop;
						accidentInstance.timestamp = timeStamper.format(new Date()); // makes a timestamp for the current time;
																					 // This will be different than the position timestamp,
																					 // But it makes sense because the accident happened BEFORE
																					 // The bus arrived at the stop.

						// wait fixed length of 10 seconds for accident.
						try {
							Thread.sleep(tenSeconds);
						} catch (InterruptedException ix) {
							System.err.println("INTERRUPTED");
							break;
						}

						isAccident = true; // set that there was an accident so it can be published when the bus arrives.
					}

					// Roll the dice for the traffic conditions!
					trafficNum = rand.nextInt(100);
					if (trafficNum >= 90) // only happens 10% of the time. heavy traffic.
					{
						actualWaitTime = (long) Math.floor(betweenStops * 1.5); // increase time by 50% rounding down
						positionInstance.trafficConditions = "Heavy";
					} else if (trafficNum >= 75) // only happens 25% of the time. Light traffic.
					{
						actualWaitTime = (long) Math.floor(betweenStops * 1.25); // increase time by 25% rounding down
						positionInstance.trafficConditions = "Light";
					}
					// if it isnt one of those conditions then its just normal time between stops

					//set the new position data that we want to publish
					positionInstance.stopNumber = stop;
					positionInstance.timestamp = timeStamper.format(new Date()); // makes a timestamp for the current time;
					positionInstance.timeBetweenStops = (double) (actualWaitTime / 1000.0); // show the actual wait time instead of the standard one
					positionInstance.fillInRatio = rand.nextInt(100); // random ratio of bus passengers.

					// wait for the length of traffic
					try {
						Thread.sleep(actualWaitTime);
					} catch (InterruptedException ix) {
						System.err.println("INTERRUPTED");
						break;
					}

					// only publish an accident if there actual was one. We should publish accidents
					// first, since subscribers may only want to check based on position publishing
					if (isAccident) {
						accidentWriter.write(accidentInstance, instance_handle);
						System.out.println(routeInfo.BusName() + " published an accident message at stop #" + stop + " on route " + routeInfo.Name() + " at " + accidentInstance.timestamp);
						isAccident = false; // Reset the accident flag
					}

					positionWriter.write(positionInstance, instance_handle);
					System.out.println(routeInfo.BusName() + " published a position message at stop #" + stop + " on route " + routeInfo.Name() + " at " + positionInstance.timestamp);
				}
			}
		} finally {
			// --- Shutdown --- //
			if (participant != null) {
				participant.delete_contained_entities();

				DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
			}
		}

	}
}
// Isaac Schultz
// Copy/paste of publisher example but with tweaks for project 2

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubThread extends Thread {
		RouteInfo routeInfo;

		public SubThread(RouteInfo info) {
			routeInfo = info;
		}

		//function that a thread uses to execute. From this point on the class runs as a thread
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
				String positionTypeName = PositionTypeSupport.get_type_name();
				PositionTypeSupport.register_type(participant, positionTypeName);

	            String accidentTypeName = AccidentTypeSupport.get_type_name();
	            AccidentTypeSupport.register_type(participant, accidentTypeName);

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
				Position positionInstance = new Position(routeInfo); //Modified Position and Accident classes so they can accept RouteInfo to set some default attributes
				Accident accidentInstance = new Accident(routeInfo);

				InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
				/*
				 * For a data type that has a key, if the same instance is going to be written
				 * multiple times, initialize the key here and register the keyed instance prior
				 * to writing
				 */
				// instance_handle = writer.register_instance(instance);

				final long tenSeconds = 10000;
				long betweenStops = (long) (routeInfo.TimeBetweenStops() * 1000); // time between stops in ms
				long actualWaitTime = betweenStops;
				SimpleDateFormat timeStamper = new SimpleDateFormat("h:mm:ss a"); // time format object that will take number of miliseconds and turn it into a readable timestamp
				boolean isAccident = false;

				for (int stop = 1; stop < routeInfo.Stops() + 1; ++stop) {
					actualWaitTime = betweenStops; //reset the wait time
					String timeString = timeStamper.format(new Date()); //makes a timestamp for the current time
					positionInstance.trafficConditions = "Normal"; //reset traffic conditions.
					
					// roll the dice on an accident as we begin to leave the bus stop. 5 has a 1 in 10 (10%) chance of being the output!
					if (rand.nextInt(10) == 5)
					{
						accidentInstance.stopNumber = stop;
						accidentInstance.timestamp = timeString;
						
						// wait fixed length of 10 seconds for accident.
						try {
							Thread.sleep(tenSeconds);
						} catch (InterruptedException ix) {
							System.err.println("INTERRUPTED");
							break;
						}
						
						isAccident = true; // set that there was an accident
					}
										
					
					// Roll the dice for the traffic conditions!
					trafficNum = rand.nextInt(100);
					if (trafficNum > 90) // only happens 10% of the time. heavy traffic.
					{
						actualWaitTime = (long) Math.floor(betweenStops * 1.5); //increase time by 50% rounding down
						positionInstance.trafficConditions = "Heavy";
					}
					else if (trafficNum > 75) // only happens 25% of the time. Light traffic.
					{
						actualWaitTime = (long) Math.floor(betweenStops * 1.25); //increase time by 25% rounding down
						positionInstance.trafficConditions = "Light";
					}
					// if it isnt one of those conditions then its just normal time between stops
					
					positionInstance.stopNumber = stop;
					positionInstance.timestamp = timeString;
					positionInstance.timeBetweenStops = (double) (actualWaitTime / 1000);
					positionInstance.fillInRatio = rand.nextInt(100); //random ratio of bus passengers.

					// wait for the length of traffic
					try {
						Thread.sleep(actualWaitTime);
					} catch (InterruptedException ix) {
						System.err.println("INTERRUPTED");
						break;
					}

					/* Write data */
					positionWriter.write(positionInstance, instance_handle);
					System.out.println(routeInfo.BusName() + " published a position message at stop #" + stop + " on route " + routeInfo.Name() + " at " + timeString);
					
					// only publish an accident if there actual was one
					if (isAccident)
					{
						accidentWriter.write(accidentInstance, instance_handle);
						System.out.println(routeInfo.BusName() + " published an accident message at stop #" + stop + " on route " + routeInfo.Name() + " at " + timeString);
						isAccident = false; // Reset the accident flag
					}	
				}

				// writer.unregister_instance(instance, instance_handle);

			} finally {
				// --- Shutdown --- //
				if (participant != null) {
					participant.delete_contained_entities();

					DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
				}
			}

		}
	}
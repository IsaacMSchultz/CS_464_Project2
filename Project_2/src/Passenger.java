// Isaac Schultz
// Passenger.java
// This class creates a thread for a passenger, which listens to all publications for a certain route. And then only one bus once they get on it. And then unsubscribes once they get off.

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;

// ===========================================================================

public class Passenger extends Thread {
	static String route; //route the passenger wants to get on
	static int start; //stop they are waiting at first
	static int end; //stop they get off at
	
	public Passenger(String route, int start, int end) {
		this.route = route;
		this.start = start;
		this.end = end;
	}

	//function that a thread uses to execute. From this point on the class runs as a thread
	// Is called through PubThread.start()
	public void run() {
		
		

        DomainParticipant participant = null;
        Subscriber subscriber = null;        
        Topic positionTopic = null;
        Topic accidentTopic = null;
        DataReaderListener accidentListener = null;
        AccidentDataReader accidentReader = null;
        PositionDataReader positionReader = null;

        try {

            // --- Create participant --- //

            /* To customize participant QoS, use
            the configuration file
            USER_QOS_PROFILES.xml */

            participant = DomainParticipantFactory.TheParticipantFactory.
            create_participant(
                0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (participant == null) {
                System.err.println("create_participant error\n");
                return;
            }                         

            // --- Create subscriber --- //

            subscriber = participant.create_subscriber(
                DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
                StatusKind.STATUS_MASK_NONE);
            if (subscriber == null) {
                System.err.println("create_subscriber error\n");
                return;
            }
            
            // Generate the lists that we will use to for topic filtering. We start by listening only for routes
            String list[] = {route}; // RTI wants the parameters in this format.		
			StringSeq parameters = new StringSeq(java.util.Arrays.asList(list)); // RTI wants the parameters in this format.
			
			// Register typenames first.
			String positionTypeName = PositionTypeSupport.get_type_name();
			PositionTypeSupport.register_type(participant, positionTypeName);
			
			String accidentTypeName = AccidentTypeSupport.get_type_name();
	        AccidentTypeSupport.register_type(participant, accidentTypeName);

			positionTopic = participant.create_topic("P2464_ischultz:POS", positionTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (positionTopic == null) {
				System.err.println("create_topic error\n");
			}
			
			accidentTopic = participant.create_topic("P2464_ischultz:ACC", accidentTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (accidentTopic == null) {
				System.err.println("create_topic error\n");
			}
			
			ContentFilteredTopic positionTopicFiltered =  participant.create_contentfilteredtopic_with_filter(
	                "P2464_ischultz:POS", positionTopic, "route MATCH %0", 
	                parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
	        if (positionTopicFiltered == null) {
	            System.err.println("create_contentfilteredtopic error\n");
	        }
	        
	        ContentFilteredTopic accidentTopicFiltered =  participant.create_contentfilteredtopic_with_filter(
	                "P2464_ischultz:ACC", accidentTopic, "route MATCH %0", 
	                parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
	        if (accidentTopicFiltered == null) {
	            System.err.println("create_contentfilteredtopic error\n");            
	        }

	        // Generate readers that allow us to grab data from the DDS when information becomes available
	        
	        positionReader = (PositionDataReader) subscriber.create_datareader(positionTopicFiltered, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_ALL);
	        if (positionReader == null) {
	        	System.err.println("create_datareader error\n");
	        }
	        
	        // The accident listener can be entirely de-coupled from the position listener in the loop below. However, the position listener will update the content based filter for accidents when the passenger gets on the bus
	        accidentListener = new AccidentListenerForPassenger();
	        
	        accidentReader = (AccidentDataReader) subscriber.create_datareader(accidentTopicFiltered, Subscriber.DATAREADER_QOS_DEFAULT, accidentListener, StatusKind.STATUS_MASK_ALL);
	        if (accidentReader == null) {
	        	System.err.println("create_datareader error\n");
	        }
			
			/* Create query condition */
	        // We will use this query condition to wake up the thread whenever content is published to the
			
            StringSeq query_parameters = new StringSeq(1);
            query_parameters.add(new String("'" + route + "'"));
            String query_expression = new String("route MATCH %0"); 
            
            QueryCondition Pquery_condition = positionReader.create_querycondition(
                    SampleStateKind.NOT_READ_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE,
                    query_expression,
                    query_parameters);
            if (Pquery_condition == null) {
                System.err.println("create_querycondition error\n");
                return;
            }

            WaitSet waitset = new WaitSet();
            
            /* Attach Query Conditions */
            //waitset.attach_condition(Aquery_condition); //We don't need to catch accidents in the waitset, only check for them when there was a postion update. This is because accidents only get published
            waitset.attach_condition(Pquery_condition);
            
            /* wait_timeaut is 1.5 secs */
            final Duration_t wait_timeout = new Duration_t(1,500000000);
            boolean atDestination = false; // variable to store whether the passenger has arrived at their destination yet
            boolean onBus = false; //variable to check in if statements if the passenger is on the bus, so we dont get off the bus before we were ever on it
            SimpleDateFormat timeStamper = new SimpleDateFormat("h:mm:ss a"); // time format object that will take number of miliseconds and turn it into a readable timestamp
            
            System.out.println("Waiting for bus at stop# " + start);// log where the passenger is waiting
            
            // --- Wait for data --- //
            while (!atDestination) { //loop until the passenger arrives at their destination
            	ConditionSeq active_conditions_seq = new ConditionSeq();
            	
            	try {
                    waitset.wait(active_conditions_seq, wait_timeout); //make this thread sleep until a location that we care about is published
                } catch (RETCODE_TIMEOUT to) {
                    //System.out.println(
                     //       "Wait timed out!! No conditions were triggered.");
                    continue;
                }
            	
            	// objects to deal with reading data from RTI DDS. Need one them for accident and position
                PositionSeq P_dataSeq = new PositionSeq();
                SampleInfoSeq P_infoSeq = new SampleInfoSeq(); //seq = sequence. Which is something in RTI? I think kind of like a list.
                AccidentSeq A_dataSeq = new AccidentSeq();
                SampleInfoSeq A_infoSeq = new SampleInfoSeq();
                
                //Read data from position publisher! Since accidents are always published when a bus arrives at a stop, when we recieve
                	// we will try to grab data from the position that was published.
                	try {
                		positionReader.take(
                            P_dataSeq, P_infoSeq,
                            ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                            SampleStateKind.ANY_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE,
                            InstanceStateKind.ANY_INSTANCE_STATE);

                        for(int i = 0; i < P_dataSeq.size(); ++i) {
                            SampleInfo info = (SampleInfo)P_infoSeq.get(i);
                            Position pos = (Position)P_dataSeq.get(i); // create a position object from the information that was published
                            
                            //System.out.println("current recieved data position" + i);

                            if (info.valid_data) {                    	                        	
                            	if (pos.stopNumber == start) //This is the stop that we get on at!
                            	{
                            		onBus = true; // get on the bus!
                            		
                            		System.out.println("Passenger getting on bus " + pos.vehicle + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + (end - pos.stopNumber) + " stops left");

                            		waitset.detach_condition(Pquery_condition); //remove the original query condition
                            		
                            		// change the topic filter to only care about that bus, and not the whole route
                            		list[0] = "'" + pos.vehicle + "'"; // RTI wants the parameters in this format.		
                        			parameters = new StringSeq(java.util.Arrays.asList(list)); // RTI wants the parameters in this format.
                        	        positionTopicFiltered.set_expression( "vehicle MATCH %0", parameters); //change the topic filter for both accident and position to match only the vehicle that the passenger just got on.
                        	        accidentTopicFiltered.set_expression( "vehicle MATCH %0", parameters);
                            		
                            		query_parameters.set(0,new String("'" + pos.vehicle + "'")); // create a new query condition that only subscribes to the bus we just got on.
                                    
                                    Pquery_condition = positionReader.create_querycondition( //need to make a new query condition object to change the query expression to vehicle
                                            SampleStateKind.NOT_READ_SAMPLE_STATE,
                                            ViewStateKind.ANY_VIEW_STATE,
                                            InstanceStateKind.ANY_INSTANCE_STATE,
                                            "vehicle MATCH %0",
                                            query_parameters);
                                    if (Pquery_condition == null) {
                                        System.err.println("create_querycondition error\n");
                                        return;
                                    }
                                    
                                    waitset.attach_condition(Pquery_condition); //re-attach the new condition with our new query expression!
                                    
                                    break; // we don't care about other messages published now, since we can only get on one bus at a time. So exit the loop.
                            	}
                            	else if (pos.stopNumber == end && onBus) // this is the stop we get off at and we are on the bus
                            	{
                            		System.out.println("Arriving at destination by " + pos.vehicle + " at " + timeStamper.format(new Date()));
                            		// Don't need to make new ones since we are now off the bus!
                            		atDestination = true; // we are now at our destination! when the while condition is re-evaluated, the loop will close.
                            	}
                            	else
                            	{
                            		System.out.println(pos.vehicle + " Arriving at stop #" + pos.stopNumber + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + (end - pos.stopNumber) + " stops left");
                            	}
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        // No data to process
                    } finally {
                        positionReader.return_loan(P_dataSeq, P_infoSeq);
                    }
                }
        } finally {

            // --- Shutdown --- //

            if(participant != null) {
                participant.delete_contained_entities();

                DomainParticipantFactory.TheParticipantFactory.
                delete_participant(participant);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Private Types
    // -----------------------------------------------------------------------
	
    private static class AccidentListenerForPassenger extends DataReaderAdapter {
        AccidentSeq _dataSeq = new AccidentSeq();
        SampleInfoSeq _infoSeq = new SampleInfoSeq();

        public void on_data_available(DataReader reader) {
            AccidentDataReader AccidentReader =
            (AccidentDataReader)reader;

            try {
                AccidentReader.take(
                    _dataSeq, _infoSeq,
                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                    SampleStateKind.ANY_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE);

                for(int i = 0; i < _dataSeq.size(); ++i) {
                    SampleInfo info = (SampleInfo)_infoSeq.get(i);
                    Accident acc = ((Accident)_dataSeq.get(i)); // make an accident object
                    
                    if (info.valid_data) {
                        System.out.println("Accident! Bus " + acc.vehicle + " on route "+ acc.route + " got in to an accident at stop number " + acc.stopNumber + "\t" + acc.timestamp);
                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No data to process
            } finally {
                AccidentReader.return_loan(_dataSeq, _infoSeq);
            }
        }
    }
    
 
}


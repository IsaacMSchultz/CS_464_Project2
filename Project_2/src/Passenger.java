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
        DataReaderListener positionListener = null;
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
			
			String list[] = {route}; // RTI wants the parameters in this format.
			
			String positionTypeName = PositionTypeSupport.get_type_name();
			PositionTypeSupport.register_type(participant, positionTypeName);

			positionTopic = participant.create_topic("P2464_ischultz:POS", positionTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (positionTopic == null) {
				System.err.println("create_topic error\n");
			}
					
			StringSeq parameters = new StringSeq(java.util.Arrays.asList(list)); // RTI wants the parameters in this format.
			
			ContentFilteredTopic positionTopicFiltered =  participant.create_contentfilteredtopic_with_filter(
	                "P2464_ischultz:POS", positionTopic, "route MATCH %0", 
	                parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
	        if (positionTopicFiltered == null) {
	            System.err.println("create_contentfilteredtopic error\n");
	        }

	        //DataReaderListener positionListener = new PositionListenerForPassenger(); //make new listeners with the specific route we are on as a passenger

	        positionReader = (PositionDataReader) subscriber.create_datareader(positionTopicFiltered, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_ALL);
	        if (positionReader == null) {
	        	System.err.println("create_datareader error\n");
	        }
			
			// Use a function that I defined to create the readers.
			//positionReader = CreatePositionReader(participant, subscriber, "route MATCH %0", list);
			accidentReader = CreateAccidentReader(participant, subscriber, "route MATCH %0", list);
			
			/* Create query condition */
			
			//Logger.get_instance().set_verbosity(LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_LOCAL);
			
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
            
            QueryCondition Aquery_condition = accidentReader.create_querycondition(
                    SampleStateKind.NOT_READ_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE,
                    query_expression,
                    query_parameters);
            if (Aquery_condition == null) {
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
            SimpleDateFormat timeStamper = new SimpleDateFormat("h:mm:ss a"); // time format object that will take number of miliseconds and turn it into a readable timestamp
            
            // --- Wait for data --- //
            while (!atDestination) { //loop until the passenger arrives at their destination
            	ConditionSeq active_conditions_seq = new ConditionSeq();
            	
            	System.out.println("YEET");
            	
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
                            
                            System.out.println("current recieved data position" + i);

                            if (info.valid_data) {                    	                        	
                            	if (pos.stopNumber == start) //This is the stop that we get on at!
                            	{
                            		System.out.println("Passenger getting on bus " + pos.vehicle + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + (end - pos.stopNumber) + " stops left");

                            		waitset.detach_condition(Pquery_condition); //remove the original query condition
                            		
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
                            	}
                            	else if (pos.stopNumber == end) // this is the stop we get off at.
                            	{
                            		System.out.println("Arriving at destination by " + pos.vehicle + " at " + timeStamper.format(new Date()));
                            		// Don't need to make new ones since we are now off the bus!
                            		atDestination = true; // we are now at our destination! when the while condition is re-evaluated, the loop will close.
                            	}
                            	else
                            	{
                            		System.out.println("Arriving at stop #" + pos.stopNumber + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + (end - pos.stopNumber) + " stops left");
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
            /* RTI Data Distribution Service provides the finalize_instance()
            method for users who want to release memory used by the
            participant factory singleton. Uncomment the following block of
            code for clean destruction of the participant factory
            singleton. */
            //DomainParticipantFactory.finalize_instance();
        }
    }

    // -----------------------------------------------------------------------
    // Private Types
    // -----------------------------------------------------------------------

    // =======================================================================
	
	//since this gets done multiple times, turning it into a function.
	// Takes the information a passenger cares about, and changes the topic filter to narrow down what they see
	private static PositionDataReader CreatePositionReader(DomainParticipant participant, Subscriber subscriber, String filter, String[] params) {
		/* Register type before creating topic */
		String positionTypeName = PositionTypeSupport.get_type_name();
		PositionTypeSupport.register_type(participant, positionTypeName);

		Topic positionTopic = participant.create_topic("P2464_ischultz:POS", positionTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
		if (positionTopic == null) {
			System.err.println("create_topic error\n");
		}
				
		StringSeq parameters = new StringSeq(java.util.Arrays.asList(params)); // RTI wants the parameters in this format.
		
		ContentFilteredTopic positionTopicFiltered =  participant.create_contentfilteredtopic_with_filter(
                "P2464_ischultz:POS", positionTopic, filter, 
                parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
        if (positionTopicFiltered == null) {
            System.err.println("create_contentfilteredtopic error\n");
        }

        DataReaderListener positionListener = new PositionListenerForPassenger(); //make new listeners with the specific route we are on as a passenger

        PositionDataReader positionReader = (PositionDataReader) subscriber.create_datareader(positionTopicFiltered, Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_ALL);
        if (positionReader == null) {
        	System.err.println("create_datareader error\n");
        }
        
        return positionReader;
	}
	
	//since this gets done multiple times, turning it into a function.
	// Takes the information a passenger cares about, and changes the topic filter to narrow down what they see
	private static AccidentDataReader CreateAccidentReader(DomainParticipant participant, Subscriber subscriber, String filter, String[] params) {
		/* Register type before creating topic */
        String accidentTypeName = AccidentTypeSupport.get_type_name();
        AccidentTypeSupport.register_type(participant, accidentTypeName);
		
		Topic accidentTopic = participant.create_topic("P2464_ischultz:ACC", accidentTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
		if (accidentTopic == null) {
			System.err.println("create_topic error\n");
		}
		
		StringSeq parameters = new StringSeq(java.util.Arrays.asList(params));// RTI wants the parameters in this format.
        
        ContentFilteredTopic accidentTopicFiltered =  participant.create_contentfilteredtopic_with_filter(
                "P2464_ischultz:ACC", accidentTopic, filter, 
                parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
        if (accidentTopicFiltered == null) {
            System.err.println("create_contentfilteredtopic error\n");            
        }
        
        DataReaderListener accidentListener = new AccidentListenerForPassenger(); //make new listeners with the specific route we are on as a passenger

        AccidentDataReader accidentReader = (AccidentDataReader) subscriber.create_datareader(accidentTopicFiltered, Subscriber.DATAREADER_QOS_DEFAULT, accidentListener, StatusKind.STATUS_MASK_ALL);
        if (accidentReader == null) {
        	System.err.println("create_datareader error\n");
        }
        
        return accidentReader;
	}

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
                        System.out.println("Accident!\t" + acc.route + "\t" + acc.vehicle + "\t\t\t\t" + acc.stopNumber + "\t\t\t\t\t\t" + acc.timestamp);
                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No data to process
            } finally {
                AccidentReader.return_loan(_dataSeq, _infoSeq);
            }
        }
    }
    
    private static class PositionListenerForPassenger extends DataReaderAdapter {
    	SimpleDateFormat timeStamper = new SimpleDateFormat("h:mm:ss a"); // time format object that will take number of miliseconds and turn it into a readable timestamp

        PositionSeq _dataSeq = new PositionSeq();
        SampleInfoSeq _infoSeq = new SampleInfoSeq();
        
        public void on_data_available(DataReader reader) {
            PositionDataReader PositionReader = (PositionDataReader)reader;

            try {
                PositionReader.take(
                    _dataSeq, _infoSeq,
                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                    SampleStateKind.ANY_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE);

                for(int i = 0; i < _dataSeq.size(); ++i) {
                    SampleInfo info = (SampleInfo)_infoSeq.get(i);
                    Position pos = (Position)_dataSeq.get(i);

                    if (info.valid_data) {                    	
                    	Subscriber subscriber = reader.get_subscriber(); // getting information from the parent of this reader so that we can change the filter after certain conditions are met.
                		DomainParticipant participant = subscriber.get_participant();
                		
                    	if (pos.stopNumber == start) //This is the stop that we get on at!
                    	{
                    		System.out.println("Passenger getting on bus " + pos.vehicle + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + (pos.stopNumber - end) + " stops left");
                    		String[] filter = {pos.vehicle}; //build the filter
                    		subscriber.delete_contained_entities(); //deletes all the datareaders
                			// Re-create the readers with the new filter
                			CreatePositionReader(participant, subscriber, "vehicle MATCH %0", filter);
                			CreateAccidentReader(participant, subscriber, "vehicle MATCH %0", filter);
                    		
                    	}
                    	else if (pos.stopNumber == end) // this is the stop we get off at.
                    	{
                    		System.out.println("Arriving at destination by " + pos.vehicle + " at " + timeStamper.format(new Date()));
                    		subscriber.delete_contained_entities(); //deletes all the datareaders
                    		// Don't need to make new ones since we are now off the bus!
                    	}
                    	else
                    	{
                    		System.out.println("Arriving at stop #" + pos.stopNumber + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + (pos.stopNumber - end) + " stops left");
                    	}
                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No data to process
            } finally {
                PositionReader.return_loan(_dataSeq, _infoSeq);
            }
        }
    }
}


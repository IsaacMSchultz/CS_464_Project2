// Isaac Schultz
// This file contains some of the functions that I went through the time to build, but ended up being completely useless unfortunately.

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;

public class SubscriberFunctions {
	
	static int start = 1; //making these so the compiler doesnt throw errors for this file.
			static int end = 2;
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

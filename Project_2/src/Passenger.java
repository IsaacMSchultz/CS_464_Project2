
// Isaac Schultz
// Passenger.java
// This class creates a thread for a passenger, which listens to all publications for a certain route. And then only one bus once they get on it. And then unsubscribes once they get off.

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.*;

// ===========================================================================

public class Passenger extends Thread {
    static String route; //route the passenger wants to get on
    static int start; //stop they are waiting at first
    static int end; //stop they get off at

    public Passenger(String route, int start, int end) {
        Passenger.route = route;
        Passenger.start = start;
        Passenger.end = end;
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

            participant = DomainParticipantFactory.TheParticipantFactory.create_participant(0, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (participant == null) {
                System.err.println("create_participant error\n");
                return;
            }

            // --- Create subscriber --- //

            subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (subscriber == null) {
                System.err.println("create_subscriber error\n");
                return;
            }

            // Generate the lists that we will use to for topic filtering. We start by listening only for routes
            String list[] = { route }; // RTI wants the parameters in this format.		
            StringSeq parameters = new StringSeq(java.util.Arrays.asList(list)); // RTI wants the parameters in this format.

            // Register typenames for accident and position first.
            String positionTypeName = PositionTypeSupport.get_type_name();
            PositionTypeSupport.register_type(participant, positionTypeName);

            String accidentTypeName = AccidentTypeSupport.get_type_name();
            AccidentTypeSupport.register_type(participant, accidentTypeName);

            // Create the topics for position and accident
            positionTopic = participant.create_topic("P2464_ischultz:POS", positionTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (positionTopic == null) {
                System.err.println("create_topic error\n");
            }

            accidentTopic = participant.create_topic("P2464_ischultz:ACC", accidentTypeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (accidentTopic == null) {
                System.err.println("create_topic error\n");
            }

            //Create content filtered topics that only care about the route declared in the constructor for now
            // Their content filters will be changed later in the main loop based on when the passenger gets on a bus.
            ContentFilteredTopic positionTopicFiltered = participant.create_contentfilteredtopic_with_filter("P2464_ischultz:POS", positionTopic, "route MATCH %0", parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
            if (positionTopicFiltered == null) {
                System.err.println("create_contentfilteredtopic error\n");
            }

            ContentFilteredTopic accidentTopicFiltered = participant.create_contentfilteredtopic_with_filter("P2464_ischultz:ACC", accidentTopic, "route MATCH %0", parameters, DomainParticipant.STRINGMATCHFILTER_NAME);
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
            // We will use this query condition to wake up the thread whenever content is published to the topic. Alternatively, we could just wait 1000ms. But since
            // I alredy went through the effort to create this, might as well use it for something.

            StringSeq query_parameters = new StringSeq(1);
            query_parameters.add(new String("'" + route + "'"));
            String query_expression = new String("route MATCH %0");

            QueryCondition Pquery_condition = positionReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, query_expression, query_parameters);
            if (Pquery_condition == null) {
                System.err.println("create_querycondition error\n");
                return;
            }

            WaitSet waitset = new WaitSet(); //Create the waitset that will wake and sleep the thread until information becomes available

            /* Attach Query Conditions */
            //waitset.attach_condition(Aquery_condition); //We don't need to catch accidents in the waitset, only check for them when there was a postion update. This is because accidents only get published
            waitset.attach_condition(Pquery_condition);

            final Duration_t wait_timeout = new Duration_t(1, 500000000); /* wait_timeaut is 1.5 secs */
            boolean atDestination = false; // variable to store whether the passenger has arrived at their destination yet
            boolean onBus = false; //variable to check in if statements if the passenger is on the bus, so we dont get off the bus before we were ever on it
            SimpleDateFormat timeStamper = new SimpleDateFormat("h:mm:ss a"); // time format object that will take number of miliseconds and turn it into a readable timestamp

            System.out.println("Waiting for bus at stop# " + start);// log where the passenger is waiting

            // --- Wait for data --- //
            while (!atDestination) { //loop until the passenger arrives at their destination
                ConditionSeq active_conditions_seq = new ConditionSeq();

                // pauses the thread until data becomes available or it times out.
                try {
                    waitset.wait(active_conditions_seq, wait_timeout); //make this thread sleep until a location that we care about is published
                } catch (RETCODE_TIMEOUT to) {
                    continue; //when it times out, just restart the loop again.
                }

                // objects to deal with reading data from RTI DDS. Need one them for accident and position
                PositionSeq P_dataSeq = new PositionSeq();
                SampleInfoSeq P_infoSeq = new SampleInfoSeq(); //seq = sequence. Which is something in RTI? I think kind of like a list.

                //Read data from position publisher! Since accidents are always published when a bus arrives at a stop, when we recieve
                // we will try to grab data from the position that was published.
                // This is fundamentally similar to the positionListener from the operator class, but it is declared within the main loop so it has access to variables within the main thread.
                // This allows us to change the topic's content based filter dynamically. And was probably the hardest part of this project to figure out.
                try {
                    //Get any positions that were published
                    positionReader.take(P_dataSeq, P_infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

                    for (int i = 0; i < P_dataSeq.size(); ++i) { // loop over all the ones we missed while sleeping.
                        SampleInfo info = (SampleInfo) P_infoSeq.get(i);
                        Position pos = (Position) P_dataSeq.get(i); // create a position object from the information that was published

                        //variables used to calculate the number of stops left to show on screen
                        int currStopDelta;
                        int stopsLeft;

                        if (info.valid_data) {// when the data is valid, check some information about its attributes.
                            if (pos.stopNumber == start) //This is the stop that we get on at!
                            {
                                onBus = true; // get on the bus!

                                //show stopsleft until we get on the bus
                                if (onBus)
                                    currStopDelta = end - pos.stopNumber;
                                else
                                    currStopDelta = start - pos.stopNumber;

                                //wrap negatives if needed.
                                if (currStopDelta < 0)
                                    stopsLeft = currStopDelta + pos.numStops;
                                else
                                    stopsLeft = currStopDelta;

                                System.out.println("Passenger getting on bus " + pos.vehicle + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + stopsLeft + " stops left");

                                waitset.detach_condition(Pquery_condition); //remove the original query condition since we are going to change it now to only wake the thread when the bus we care about moves.

                                // change the topic filter to only care about that bus, and not the whole route
                                list[0] = "'" + pos.vehicle + "'"; // RTI wants the parameters in this format.		
                                parameters = new StringSeq(java.util.Arrays.asList(list)); // RTI wants the parameters in this format.

                                //change the topic filter for both accident and position to match only the vehicle that the passenger just got on.
                                positionTopicFiltered.set_expression("vehicle MATCH %0", parameters);
                                accidentTopicFiltered.set_expression("vehicle MATCH %0", parameters);

                                query_parameters.set(0, new String("'" + pos.vehicle + "'")); // create a new query condition that only subscribes to the bus we just got on. new string cause I was getting weird errors when I
                                                                                              // used an existing string.

                                //need to make a new query condition object to change the query expression to vehicle
                                Pquery_condition = positionReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE, "vehicle MATCH %0", query_parameters);
                                if (Pquery_condition == null) {
                                    System.err.println("create_querycondition error\n");
                                    return;
                                }

                                waitset.attach_condition(Pquery_condition); //re-attach the new condition with our new query expression!

                                break; // we don't care about other messages published now, since we can only get on one bus at a time. So exit the loop.
                            } else if (pos.stopNumber == end && onBus) // this is the stop we get off at and we are on the bus
                            {
                                System.out.println("Arriving at destination by " + pos.vehicle + " at " + timeStamper.format(new Date()));
                                atDestination = true; // we are now at our destination! when the while condition is re-evaluated, the loop will close.
                            } else { //we didnt get on or off here, so just show the arriving status of the bus that we recieved informatino on.
                                //show stopsleft until we get on the bus
                                if (onBus)
                                    currStopDelta = end - pos.stopNumber;
                                else
                                    currStopDelta = start - pos.stopNumber;

                                //wrap negatives if needed.
                                if (currStopDelta < 0)
                                    stopsLeft = currStopDelta + pos.numStops;
                                else
                                    stopsLeft = currStopDelta;

                                System.out.println(pos.vehicle + " Arriving at stop #" + pos.stopNumber + " at " + timeStamper.format(new Date()) + "\tthere is " + pos.trafficConditions + " traffic. " + stopsLeft + " stops left");
                            }
                        }
                    }
                } catch (RETCODE_NO_DATA noData) { //There was no data or it wasnt valid, so just go back to the start of the loop.
                    // No data to process
                } finally {
                    positionReader.return_loan(P_dataSeq, P_infoSeq);
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

    /**
     * Listener object that handles when an accident is published. Prints information about when and what bus the accident affected asynchronously.
     */
    private static class AccidentListenerForPassenger extends DataReaderAdapter {
        AccidentSeq _dataSeq = new AccidentSeq();
        SampleInfoSeq _infoSeq = new SampleInfoSeq();

        public void on_data_available(DataReader reader) { //funciton that gets called by the accidentReader when data becomes available.
            AccidentDataReader AccidentReader = (AccidentDataReader) reader; //cast the recieved data as an accident data reader so we can get accident specific properties.

            try {
                //get the data from the DDS
                AccidentReader.take(_dataSeq, _infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

                for (int i = 0; i < _dataSeq.size(); ++i) { //Loop through all the messages that we missed when we were asleep.
                    SampleInfo info = (SampleInfo) _infoSeq.get(i);
                    Accident acc = ((Accident) _dataSeq.get(i)); // make an accident object with the recieved data

                    if (info.valid_data) { // If the data is valid, print it to the screen as well as its timestamp.
                        System.out.println("Accident! Bus " + acc.vehicle + " on route " + acc.route + " got in to an accident at stop number " + acc.stopNumber + "\t" + acc.timestamp);
                    }
                }
            } catch (RETCODE_NO_DATA noData) { // There was no data so we do nothing.
                // No data to process
            } finally {
                AccidentReader.return_loan(_dataSeq, _infoSeq);
            }
        }
    }
}

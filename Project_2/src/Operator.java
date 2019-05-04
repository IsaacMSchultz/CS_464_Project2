
// Isaac Schultz
// Operator subscriber file recieves information from both accident and from position.
// Writes them to the screen in a grid like way to easily see the status of all the busses in the fleet.


import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.*;

// ===========================================================================

public class Operator extends Thread {

    public Operator() {
        super();
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

            // --- Create reader --- //

            //defined at the end of the file. These are what print out the information as it arrives.
            accidentListener = new AccidentListener();
            positionListener = new PositionListener();

            // Create readers that call the related functions in the listeners defined above when informatino is ready
            accidentReader = (AccidentDataReader) subscriber.create_datareader(accidentTopic, Subscriber.DATAREADER_QOS_DEFAULT, accidentListener, StatusKind.STATUS_MASK_ALL);
            if (accidentReader == null) {
                System.err.println("create_datareader error\n");
                return;
            }

            positionReader = (PositionDataReader) subscriber.create_datareader(positionTopic, Subscriber.DATAREADER_QOS_DEFAULT, positionListener, StatusKind.STATUS_MASK_ALL);
            if (positionReader == null) {
                System.err.println("create_datareader error\n");
                return;
            }

            // --- Wait for data --- //

            //Print the header after the program begins running
            System.out.println("MessageType\tRoute\t\tVehicle\t\tTraffic\t\tStop#\t#Stops\tTimeBetweenStops\tFill%\tTimestamp");

            while (true) { //run forever, but sleep every second so CPU time doesnt get wasted.
                //When the thread wakes up, the listeners defined above will print any information that was published while sleeping.
                try {
                    Thread.sleep(1000); // in millisec
                } catch (InterruptedException ix) {
                    System.err.println("INTERRUPTED");
                    break;
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

    // -----------------------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------------------

    /**
     * Accident listener listens for accidents and when recieved, prints information about it to the screen for the operator to see
     */
    private static class AccidentListener extends DataReaderAdapter {

        AccidentSeq _dataSeq = new AccidentSeq();
        SampleInfoSeq _infoSeq = new SampleInfoSeq();

        public void on_data_available(DataReader reader) {
            AccidentDataReader AccidentReader = (AccidentDataReader) reader; //cast the reader as an accident reader so we can access fields specific to an accident class

            try {
                //Get the new information from the DDS
                AccidentReader.take(_dataSeq, _infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

                for (int i = 0; i < _dataSeq.size(); ++i) { //for the number of messeges we missed while sleeping, print them each. This will be outputted in the same terminal window as OperatorLauncher
                    SampleInfo info = (SampleInfo) _infoSeq.get(i);
                    Accident acc = ((Accident) _dataSeq.get(i)); // make an accident object that we can get information from

                    if (info.valid_data) { // if the data is valid, output a line to the screen that follows the same forma
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

    /***
     * Listens for position updates and displays them on a grid on the screen.
     */
    private static class PositionListener extends DataReaderAdapter {

        PositionSeq _dataSeq = new PositionSeq();
        SampleInfoSeq _infoSeq = new SampleInfoSeq();

        public void on_data_available(DataReader reader) {
            PositionDataReader PositionReader = (PositionDataReader) reader; //cast the reader as a position reader so we can access fields specific to an position class

            try {
                //Get the new information from the DDS
                PositionReader.take(_dataSeq, _infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

                for (int i = 0; i < _dataSeq.size(); ++i) { //for the number of messeges we missed while sleeping, print them each. This will be outputted in the same terminal window as OperatorLauncher
                    SampleInfo info = (SampleInfo) _infoSeq.get(i);
                    Position pos = (Position) _dataSeq.get(i); // make a position object that we can get information from

                    if (info.valid_data) { // if the data is valid, output a line to the screen that follows the same forma
                        System.out.println("Position\t" + pos.route + "\t" + pos.vehicle + "\t\t" + pos.trafficConditions + "\t\t" + pos.stopNumber + "\t" + pos.numStops + "\t" + pos.timeBetweenStops + "\t\t\t" + pos.fillInRatio + "\t" + pos.timestamp);
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

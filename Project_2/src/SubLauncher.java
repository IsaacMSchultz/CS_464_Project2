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

public class SubLauncher {
	public static void main(String[] args) {
			
			// Variables to start threads.
			int numThreads;
			int curThread = 0;
			SubThread[] subThreads;
				
			//Operator operator = new Operator();
			//operator.start();
			
			Passenger passenger = new Passenger("Express1", 2, 4);
			passenger.start();
			
			//Don't really need to wait on join since starting threads is the last thing this class does.

	}		
}

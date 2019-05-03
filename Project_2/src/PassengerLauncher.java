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

public class PassengerLauncher {
	public static void main(String[] args) {
			if (args.length != 3)
				System.err.println("Invalid arguments!");
			
			Passenger passenger = new Passenger(args[0], Integer.parseInt(args[0]), Integer.parseInt(args[0]));
			passenger.start();
			
			//Don't really need to wait on join since starting threads is the last thing this class does.

	}		
}

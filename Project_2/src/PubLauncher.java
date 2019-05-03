// Isaac Schultz 11583435
// Publisher launching is handled within this file.

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PubLauncher {
    public static void main(String[] args) {

        try (InputStream input = new FileInputStream("pub.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            prop.forEach((key, value) -> System.out.println("Key : " + key + ", Value : " + value));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

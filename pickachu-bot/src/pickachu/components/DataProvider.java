package pickachu.components;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;

import pickachu.components.communication.CommunicationUnit;
import pickachu.components.motors.DriverUnit;
import pickachu.components.motors.PickupUnit;
import pickachu.components.orientation.OrientationUnit;
import pickachu.components.sound.SoundUnit;
import pickachu.webserver.Webserver;

/**
 * The DataProvider Singleton keeps a reference to the robots state containing system components and
 * makes them available for the main class to call. The components themselves have no knowledge about each other.
 * Their interconnection is done by the user of the DataProvider.
 */
public class DataProvider {
	
	private static FileSystem fileSystem;
	private static Webserver webserver;
	private static CommunicationUnit communicationUnit;
	private static OrientationUnit orientationUnit;
	private static DriverUnit driverUnit;
	private static PickupUnit pickupUnit;
	private static SoundUnit soundUnit;
	
	public static synchronized Webserver webserver() {
		if (webserver == null) {
			webserver = new Webserver();
		}
		return webserver;
	}
	
	public static CommunicationUnit communicationUnit() {
		if (communicationUnit == null) {
			communicationUnit = new CommunicationUnit();
			communicationUnit.start();
		}
		return communicationUnit;
	}
	
	public static synchronized DriverUnit driverUnit() {
		if (driverUnit == null) {
			driverUnit = new DriverUnit();
		}
		return driverUnit;
	}
	
	public static synchronized PickupUnit pickupUnit() {
		if (pickupUnit == null) {
			pickupUnit = new PickupUnit();
		}
		return pickupUnit;
	}
	
	public static synchronized SoundUnit soundUnit() {
		if (soundUnit == null) {
			soundUnit = new SoundUnit();
		}
		return soundUnit;
	}
	
	public static synchronized OrientationUnit orientationUnit() {
		if (orientationUnit == null) {
			orientationUnit = new OrientationUnit();
		}
		return orientationUnit;
	}
	
	public static synchronized FileSystem getFileSystem() {
		if (fileSystem == null) {
			try {
				fileSystem = FileSystems.newFileSystem(URI.create("jar:file:/home/lejos/programs/PickachuBot.jar"), new HashMap<String, String>());
			} catch (IOException e) {
				throw new ComponentInitializationError("The FileSystem could not be initialized");
			}
		}
		return fileSystem;
	}
}

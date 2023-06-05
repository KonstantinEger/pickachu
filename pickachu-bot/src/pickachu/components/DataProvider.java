package pickachu.components;

import pickachu.components.communication.CommunicationUnit;
import pickachu.components.motors.DriverUnit;
import pickachu.components.motors.PickupUnit;

public class DataProvider {
	
	private static CommunicationUnit communicationUnit;
	private static DriverUnit driverUnit;
	private static PickupUnit pickupUnit;
	
	public static synchronized CommunicationUnit communicationUnit() {
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
}

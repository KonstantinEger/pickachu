package pickachu.components;

import pickachu.components.communication.CommunicationUnit;
import pickachu.components.movement.DriverUnit;

public class DataProvider {
	
	private static CommunicationUnit communicationUnit;
	private static DriverUnit driverUnit;
	
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
}

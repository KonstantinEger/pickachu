package pickachu.components;

import pickachu.components.communication.CommunicationUnit;

public class DataProvider {
	
	private static CommunicationUnit communicationUnit;
	
	public static synchronized CommunicationUnit communicationUnit() {
		if (communicationUnit == null) {
			communicationUnit = new CommunicationUnit();
		}
		return communicationUnit;
	}
}

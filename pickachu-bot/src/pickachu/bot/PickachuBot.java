package pickachu.bot;

import pickachu.components.DataProvider;
import pickachu.components.communication.Message;
import pickachu.components.communication.MessageHandler;

public class PickachuBot {
	public static void main(String[] args) {
	
		DataProvider.communicationUnit().setHandler(new MessageHandler() {
			
			@Override
			public Message handle(Message message) {
				switch (message.opCode) {
				case NoOp:
				default:
					return null;
				}
			}
		});
		DataProvider.driverUnit().left(900);
		//DataProvider.driverUnit().left(90);
		//DataProvider.driverUnit().forward(5*360);
	}
}

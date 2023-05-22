package pickachu.bot;

import java.io.IOException;

import lejos.hardware.Button;
import pickachu.components.DataProvider;
import pickachu.components.communication.Message;
import pickachu.components.communication.MessageHandler;
import pickachu.webserver.Webserver;

public class PickachuBot {
	public static void main(String[] args) throws IOException {
	
		DataProvider.communicationUnit().setHandler(new MessageHandler() {
			
			@Override
			public Message handle(Message message) {
				switch (message.opCode) {
				case Forward:
					DataProvider.driverUnit().forward(extractRotations(message));
					return null;
				case Left:
					DataProvider.driverUnit().left(extractRotations(message));
					return null;
				case Right:
					DataProvider.driverUnit().right(extractRotations(message));
					return null;
				case NoOp:
				default:
					return null;
				}
			}
		});
		
		
		Webserver.getInstance().host();
		
		
		shutdownOnEnterButtonClicked();
	}
	
	public static int extractRotations(Message message) {
		return Integer.parseInt(message.content[0]);
	}
	
	public static void shutdownOnEnterButtonClicked(){
		while (Button.waitForAnyPress() != Button.ID_ENTER) {
			try {
				Webserver.getInstance().kill();
				DataProvider.communicationUnit().stop();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

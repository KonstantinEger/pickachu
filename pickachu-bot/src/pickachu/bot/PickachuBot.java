package pickachu.bot;

import java.net.URL;

import lejos.hardware.Button;
import pickachu.components.DataProvider;
import pickachu.components.communication.Message;
import pickachu.components.communication.MessageHandler;

public class PickachuBot {
	public static void main(String[] args) {
	
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
		
		URL lUrl = PickachuBot.class.getResource("/index.html");
		System.out.println("OUR URL " + lUrl);
		//Button.waitForAnyEvent()
		
		try {
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int extractRotations(Message message) {
		return Integer.parseInt(message.content[0]);
	}
}

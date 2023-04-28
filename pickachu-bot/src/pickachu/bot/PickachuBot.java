package pickachu.bot;

import lejos.utility.Delay;
import pickachu.components.DataProvider;
import pickachu.components.communication.Message;
import pickachu.components.communication.MessageHandler;

public class PickachuBot {
	public static void main(String[] args) {
		System.out.println("hello world");
		Delay.msDelay(5000);
		
	
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
	}
}

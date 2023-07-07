package pickachu.bot;

import java.io.IOException;
import lejos.hardware.Button;
import pickachu.components.DataProvider;
import pickachu.components.Observer;
import pickachu.components.communication.Message;
import pickachu.components.communication.MessageHandler;
import pickachu.components.communication.OpCode;
import pickachu.components.sound.SoundUnit.Sounds;
import pickachu.webserver.Webserver;

public class PickachuBot {
	public static void main(String[] args) throws IOException {
	
		// Inject message handling rules into the websocket.
		DataProvider.communicationUnit().setHandler(new MessageHandler() {
			
			@Override
			public Message handle(Message message) {
				switch (message.opCode) {
				case Forward:
					DataProvider.driverUnit().forward(extractRotations(message));
					return acknowledge(message);
				case Left:
					DataProvider.driverUnit().left(extractRotations(message));
					return acknowledge(message);
				case Right:
					DataProvider.driverUnit().right(extractRotations(message));
					return acknowledge(message);
				case PickUp:
					DataProvider.soundUnit().playSound(Sounds.BattleBegin);
					DataProvider.pickupUnit().pickUp();
					return acknowledge(message);
				case Drop:
					DataProvider.pickupUnit().drop();
					DataProvider.soundUnit().playSound(Sounds.BattleWin);
					return acknowledge(message);
				case Stop:
					DataProvider.driverUnit().stop();
					return acknowledge(message);
				case NoOp:
				default:
					return null;
				}
			}
		});
		
		
		// Push gyro sensor updates to client whenever it updates.
		DataProvider.orientationUnit().registerObservers(new Observer<Float>() {
			@Override
			public void onValueChange(Float value) {
				DataProvider.communicationUnit().broadcast(new Message(OpCode.Gyro, new String[]{value.toString()}));				
			}
		});
		
		Webserver.getInstance().host();
		
		DataProvider.soundUnit();
		
		shutdownOnEnterButtonClicked();
	}
	
	public static int extractRotations(Message message) {
		return Integer.parseInt(message.content[0]);
	}
	
	/**
	 * Builds an acknowledgement message for the input message
	 */
	public static Message acknowledge(Message message) {
		 String[] responseContent = new String[message.content.length + 1];
		 responseContent[0] = message.opCode.name();
		 for (int index = 0; index < message.content.length; index++) {
			 responseContent[index+1]= message.content[index];
		 }
		 return new Message(OpCode.Ack, responseContent);
	}
	
	public static void shutdownOnEnterButtonClicked(){
		System.out.print("Waiting for shutdown");
		while (Button.waitForAnyPress() == Button.ID_ENTER) {
			System.out.print("Enter button registered");
			try {
				Webserver.getInstance().kill();
				DataProvider.driverUnit().dispose();
				DataProvider.orientationUnit().stop();
				DataProvider.communicationUnit().stop();
				System.exit(0);
			} catch (IOException | InterruptedException e) {
				System.out.print("In Error state");
				e.printStackTrace();
			}
		}
	}
}

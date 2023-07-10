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
	
		// Inject Message-handling rules into the Websocket.
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
				case NoOp:
				default:
					return null;
				}
			}
		});
		
		// Push Gyro Sensor Updates to Client whenever it updates.
		DataProvider.orientationUnit().registerObservers(new Observer<Float>() {
			@Override
			public void onValueChange(Float value) {
				DataProvider.communicationUnit().broadcast(new Message(OpCode.Gyro, new String[]{value.toString()}));				
			}
		});
		
		// Host the Webserver
		Webserver.getInstance().host();
		
		// Start the SoundUnit
		DataProvider.soundUnit();
		
		// Block Main Thread until Enter Button is pressed
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
		while (Button.waitForAnyPress() == Button.ID_ENTER) {
			try {
				Webserver.getInstance().dispose();
				DataProvider.driverUnit().dispose();
				DataProvider.pickupUnit().dispose();
				DataProvider.orientationUnit().dispose();
				DataProvider.communicationUnit().dispose();
				System.exit(0);
			} catch (IOException e) {
				System.out.print("Failed to properly shut down the robot.");
				e.printStackTrace();
			}
		}
	}
}

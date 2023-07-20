package pickachu.bot;

import java.io.IOException;
import java.util.concurrent.Future;

import lejos.hardware.Button;
import pickachu.components.DataProvider;
import pickachu.components.Observer;
import pickachu.components.communication.Message;
import pickachu.components.communication.MessageHandler;
import pickachu.components.communication.OpCode;
import pickachu.components.Utils;
import pickachu.components.sound.SoundUnit.Sounds;

public class PickachuBot {
	public static void main(String[] args) throws IOException {
	
		// Inject Message-handling rules into the Websocket.
		DataProvider.communicationUnit().setHandler(new MessageHandler() {
			
			@Override
			public Message handle(Message message) {
				Future<?> awaitable;
				
				switch (message.opCode) {
				case Forward:
					awaitable = DataProvider.driverUnit().forward(extractRotations(message));
					Utils.waitForFuture(awaitable);
					return message.acknowledge();
				case Left:
					awaitable = DataProvider.driverUnit().left(extractRotations(message));
					Utils.waitForFuture(awaitable);
					return message.acknowledge();
				case Right:
					awaitable = DataProvider.driverUnit().right(extractRotations(message));
					Utils.waitForFuture(awaitable);
					return message.acknowledge();
				case PickUp:
					DataProvider.soundUnit().playSound(Sounds.BattleBegin);
					awaitable = DataProvider.pickupUnit().pickUp();
					return message.acknowledge();
				case Drop:
					awaitable = DataProvider.pickupUnit().drop();
					DataProvider.soundUnit().playSound(Sounds.BattleWin);
					return message.acknowledge();
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
		DataProvider.webserver().host();
		
		// Start the SoundUnit
		DataProvider.soundUnit();
		
		// Block Main Thread until Enter Button is pressed
		shutdownOnEnterButtonClicked();
	}
	
	public static int extractRotations(Message message) {
		return Integer.parseInt(message.content[0]);
	}
	
	public static void shutdownOnEnterButtonClicked(){
		while (Button.waitForAnyPress() == Button.ID_ENTER) {
			DataProvider.webserver().dispose();
			DataProvider.driverUnit().dispose();
			DataProvider.pickupUnit().dispose();
			DataProvider.orientationUnit().dispose();
			DataProvider.communicationUnit().dispose();
			System.exit(0);
		}
	}
}

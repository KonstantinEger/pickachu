package pickachu.components.motors;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import pickachu.components.Worker;
import pickachu.components.Disposable;
import pickachu.components.SimpleAction;

public class PickupUnit implements Disposable{
	
	private static final int ROTATION = 135;
	private final RegulatedMotor motor;
	private final Worker picker;
	
	public PickupUnit() {
		motor = new EV3MediumRegulatedMotor(MotorPort.C);
		picker = new Worker(0);
	}
	
	public void pickUp() {
		picker.submit(new SimpleAction() {
			@Override
			public void execute() {
				motor.rotate(-ROTATION);
			}
		});
	}
	
	public void drop() {
		picker.submit(new SimpleAction() {
			@Override
			public void execute() {
				motor.rotate(ROTATION);
			}
		});
	}
	
	@Override
	public void dispose() {
		picker.stop();
	}

}

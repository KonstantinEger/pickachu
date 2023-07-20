package pickachu.components.motors;

import java.util.concurrent.Future;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import pickachu.components.Worker;
import pickachu.components.Disposable;
import pickachu.components.SimpleAction;

/**
 * Provides an abstraction to access the underlyig hardware interface provided by lejos.
 * This component controls two one motor and handles picking up / dropping objects.
 */
public class PickupUnit implements Disposable{
	
	private static final int ROTATION = 1485;
	private final RegulatedMotor motor;
	private final Worker picker;
	
	public PickupUnit() {
		motor = new EV3MediumRegulatedMotor(MotorPort.C);
		motor.setSpeed(500);
		picker = new Worker(0);
	}
	
	public Future<?> pickUp() {
		Future<?> task =  picker.submit(new SimpleAction() {
			@Override
			public void execute() {
				motor.rotate(-ROTATION);
			}
		});
		
		return task;
	}
	
	public Future<?> drop() {
		Future<?> task = picker.submit(new SimpleAction() {
			@Override
			public void execute() {
				motor.rotate(ROTATION);
			}
		});
		
		return task;
	}
	
	@Override
	public void dispose() {
		picker.stop();
	}

}

package pickachu.components.motors;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import pickachu.components.Worker;
import pickachu.components.Action;
import pickachu.components.SimpleAction;

public class PickupUnit {
	
	private static final int ROTATION = 135;
	private final RegulatedMotor motor;
	private final BlockingQueue<Action> bus;
	private final Worker worker;
	
	public PickupUnit() {
		motor = new EV3MediumRegulatedMotor(MotorPort.C);
		bus = new LinkedBlockingQueue<Action>();
		worker = new Worker(bus, 2);
	}
	
	public void pickUp() {
		bus.add(new SimpleAction() {
			@Override
			public void execute() {
				motor.rotate(-ROTATION);
			}
		});
	}
	
	public void drop() {
		bus.add(new SimpleAction() {
			@Override
			public void execute() {
				motor.rotate(ROTATION);
			}
		});
	}
	
	public void dispose() {
		worker.kill();
		bus.clear();
	}

}

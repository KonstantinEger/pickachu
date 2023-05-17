package pickachu.components.movement;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import pickachu.components.Worker;
import pickachu.components.Actions.*;


public class DriverUnit {
	
	private final RegulatedMotor leftMotor;
	private final RegulatedMotor rightMotor;
	
	private final BlockingQueue<Action> bus;
	private final Worker driver;
	
	
	public DriverUnit() {
		leftMotor = new EV3MediumRegulatedMotor(MotorPort.A);
		rightMotor = new EV3MediumRegulatedMotor(MotorPort.B);
		bus = new LinkedBlockingQueue<Action>();
		driver = new Worker(bus, 2);
	}

	public void left(final int degrees) {
		this.right(-degrees);
	}
	
	public void right(final int degrees) {
		bus.add(new MultiAction(){

			@Override
			public List<SimpleAction> getActions() {
				return Arrays.asList(
						new SimpleAction() {
							@Override
							public void execute() {
								rightMotor.rotate(degrees);
							}
						},
						new SimpleAction() {
							@Override
							public void execute() {
								leftMotor.rotate(-degrees);
							}
						}
				);
			}
			
		});
	}
	
	public void forward(final int degrees) {
		bus.add(new MultiAction(){

			@Override
			public List<SimpleAction> getActions() {
				return Arrays.asList(
						new SimpleAction() {
							@Override
							public void execute() {
								rightMotor.rotate(degrees);
							}
						},
						new SimpleAction() {
							@Override
							public void execute() {
								leftMotor.rotate(degrees);
							}
						}
				);
			}
		});
	}
	
	
	public void dispose() {
		driver.kill();
		bus.clear();
	}
}

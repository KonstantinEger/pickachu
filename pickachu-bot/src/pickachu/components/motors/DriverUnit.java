package pickachu.components.motors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import pickachu.components.Worker;
import pickachu.components.SimpleAction;
import pickachu.components.Disposable;
import pickachu.components.MultiAction;


/**
 * Provides an abstraction to access the underlyig hardware interface provided by lejos.
 * This component controls two motors to make the robot able to move in space.
 */
public class DriverUnit implements Disposable {
	
	private final RegulatedMotor leftMotor;
	private final RegulatedMotor rightMotor;
	private final Worker driver;
	private static final int SPEED = 100; //100;
	
	
	public DriverUnit() {
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);
		leftMotor.setSpeed(SPEED);
		rightMotor.setSpeed(SPEED);
		driver = new Worker(2);
	}

	public Future<?> left(final int degrees) {
		return this.right(-degrees);
	}
	
	public Future<?> right(final int degrees) {
		
		Future<?> task = driver.submit(new MultiAction(){

			@Override
			public List<SimpleAction> getActions() {
				return Arrays.asList(
					new SimpleAction() {
						@Override
						public void execute() {
							rightMotor.rotate(-degrees);
						}
					},
					new SimpleAction() {
						@Override
						public void execute() {
							leftMotor.rotate(+degrees);
						}
					}
				);
			}
			
		});
		
		return task;
	}
	
	public Future<?> forward(final int degrees) {
		Future<?> task = driver.submit(new MultiAction(){

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
		
		return task;
	}
	
	@Override
	public void dispose() {
		driver.stop();
	}
}

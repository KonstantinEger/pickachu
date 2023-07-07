package pickachu.components.motors;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import pickachu.components.Worker;
import pickachu.components.SimpleAction;
import pickachu.components.Disposable;
import pickachu.components.MultiAction;


public class DriverUnit implements Disposable {
	
	private final RegulatedMotor leftMotor;
	private final RegulatedMotor rightMotor;
	private final Worker driver;
	private static final int SPEED = 100;
	
	
	public DriverUnit() {
		leftMotor = new EV3MediumRegulatedMotor(MotorPort.A);
		rightMotor = new EV3MediumRegulatedMotor(MotorPort.B);
		leftMotor.setSpeed(SPEED);
		rightMotor.setSpeed(SPEED);
		driver = new Worker(2);
	}

	public void left(final int degrees) {
		this.right(-degrees);
	}
	
	public void right(final int degrees) {
		
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
							leftMotor.rotate(-degrees);
						}
					}
				);
			}
			
		});
		
		Utils.waitForFuture(task);
	}
	
	public void forward(final int degrees) {
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
		
		Utils.waitForFuture(task);
	}
	
	@Override
	public void dispose() {
		driver.stop();
	}
}

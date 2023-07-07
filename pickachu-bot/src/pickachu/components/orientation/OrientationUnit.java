package pickachu.components.orientation;

import java.util.HashSet;
import java.util.Set;

import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import pickachu.components.Observer;

public class OrientationUnit {
		private final EV3GyroSensor gyroSensor;
		private final SampleProvider sampleProvider;
		private final Worker worker;
		private final int samplingIntervalMs = 500;
		private volatile boolean running;
		private final Set<Observer<Float>> observers;
		
		public OrientationUnit() {
			gyroSensor = new EV3GyroSensor(SensorPort.S1);
			sampleProvider = gyroSensor.getAngleMode();

			observers = new HashSet<>();
			
			running = true;
			worker = new Worker();
			worker.start();
		}
		
		public synchronized float getRotation() {
			float [] sample = new float[sampleProvider.sampleSize()];
			sampleProvider.fetchSample(sample, 0);
            return sample[0];
		}
		
		public void registerObservers(Observer<Float> observer) {
			observers.add(observer);
		}
		
		public void unregisterObserver(Observer<Float>  observer) {
			observers.remove(observer);
		}
		
		public void stop() {
			running = false;
			observers.clear();
		}
		
		private class Worker extends Thread{
			@Override
			public void run() {
				while (running) {
					float rotation = getRotation();
					for (Observer<Float> observer : observers) {
						observer.onValueChange(rotation);
					}
					Delay.msDelay(samplingIntervalMs);
				}
			}
		}
}

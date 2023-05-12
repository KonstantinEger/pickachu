package pickachu.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pickachu.components.Actions.*;


/**
 * A Worker is a Thread that runs and works on Actions that arrive via the bus.
 */
public class Worker extends Thread{

	BlockingQueue<Action> bus;
	private boolean running = true;
	private final ExecutorService coWorkers;
	
	public Worker(BlockingQueue<Action> bus, int coWorkers) {
		this.bus = bus;
		this.coWorkers = Executors.newFixedThreadPool(coWorkers);
		this.start();
	}
	
	/**
	 * Kills the thread without chance of reactivation
	 */
	public void kill() {
		this.running = false;
	}
	
	
	/**
	 * Interrupts the worker and clears the bus
	 * The worker should immediately become available again
	 */
	@Override
	public void interrupt() {
		bus.clear();
		super.interrupt();
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				Action action = bus.take();
				if (action instanceof SimpleAction) {
					SimpleAction simpleAction = (SimpleAction) action;
					simpleAction.execute();
				}else if (action instanceof MultiAction) {
					MultiAction multiAction = (MultiAction) action;
					List<Future<?>> futures = new ArrayList<>();
					
					// submit each action to a CoWorker and start it
					for (final SimpleAction simpleAction : multiAction.getActions()) {
						Runnable task = new Runnable() {
							@Override
							public void run() {
								simpleAction.execute();
							}
						};
						
						futures.add(coWorkers.submit(task));
					}
					
					// ensure the worker is waiting for its CoWorkers
					for (Future<?> future: futures) {
						try {
							future.get();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (InterruptedException e) {}
		}
	}
}

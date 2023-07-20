package pickachu.components;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * A Worker is a Thread that runs and works on Actions that arrive via the bus.
 */
public class Worker {

	private final ExecutorService coWorkers;
	
	/**
	 * @param coWorkers : The number of Threads that are needed for a Componenet to be able to operate in parallel
	 * lets say you want to operate two motors - then you need two coworkers.
	 */
	public Worker(int coWorkers) {
		// Needs one extra thread for self
		this.coWorkers = Executors.newFixedThreadPool(coWorkers+1);
	}
	
	/**
	 * Kills the thread without chance of reactivation
	 */
	public void stop() {
		coWorkers.shutdown();
	}
	
	
	public Future<?> submit(Action action) {
		Future<?> collector;
		
		if (action instanceof SimpleAction) {
			final SimpleAction simpleAction = (SimpleAction) action;
			collector = simpleActionHelper(simpleAction);
		}else {
			MultiAction multiAction = (MultiAction) action;
			collector = multiActionHelper(multiAction);
		}
		
		return collector;
	}
	
	
	private Future<?> simpleActionHelper(final SimpleAction action){
		Runnable task = new Runnable() {
			@Override
			public void run() {
				action.execute();
			}
		};
		
		return coWorkers.submit(task);
	}
	
	private Future<?> multiActionHelper(final MultiAction action) {
		final List<Future<?>> futures = new ArrayList<>();
		
		// Submit each action to a CoWorker and start it
		for (final SimpleAction simpleAction : action.getActions()) {
			Future<?> future = simpleActionHelper(simpleAction);
			futures.add(future);
		}
		
		// This future will be completed once all the other futures have been completed.
		Runnable collector = new Runnable() {
			@Override
			public void run() {
				// ensure the worker is waiting for its CoWorkers
				for (Future<?> future: futures) {
					try {
						future.get();
					} catch (ExecutionException | InterruptedException e) {
						e.printStackTrace();
					}
				}				
			}
		};
		
		return coWorkers.submit(collector);
	}
	
}

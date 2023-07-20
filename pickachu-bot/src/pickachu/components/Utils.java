package pickachu.components;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Utils {
	public static void waitForFuture(Future<?> future) {
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
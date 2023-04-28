package pickachu.components;

/**
 * Exception thrown during component initialization if something goes wrong,
 * this is unrecoverable from.
 */
public class ComponentInitializationError extends RuntimeException{
	public ComponentInitializationError(String message){
		super(message);
	}

}

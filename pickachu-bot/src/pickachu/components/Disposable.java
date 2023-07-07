package pickachu.components;

/**
 * This interface marks a system component as disposable.
 * Components marked with this interface need to be properly disposed before shutdown
 * in order to prevent memory leaks and unexpected behavior.
 */
public interface Disposable {
	public void dispose();
}

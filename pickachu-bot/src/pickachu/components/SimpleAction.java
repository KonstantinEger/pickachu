package pickachu.components;

/**
 * Defines an Action that does need only one Thread to be executed.
 */
public interface SimpleAction extends Action{
		public void execute();
}

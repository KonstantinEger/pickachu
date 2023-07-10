package pickachu.components;

import java.util.List;

/**
 * A MultiAction does require multiple Threads to be executed.
 * Hence we construct it from multiple SimpleActions.
 */
public interface MultiAction extends Action{
	public List<SimpleAction> getActions();
}

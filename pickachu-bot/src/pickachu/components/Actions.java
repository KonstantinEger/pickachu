package pickachu.components;

import java.util.List;

public class Actions {
	public interface Action {}

	@FunctionalInterface
	public interface SimpleAction extends Action{
		public void execute();
	}

	@FunctionalInterface
	public interface MultiAction extends Action{
		public List<SimpleAction> getActions();
	}
}
package pickachu.components;

import java.util.List;

public interface MultiAction extends Action{
	public List<SimpleAction> getActions();
}

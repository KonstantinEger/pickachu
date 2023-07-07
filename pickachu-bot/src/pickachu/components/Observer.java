package pickachu.components;

public interface Observer<T> {
	public void onValueChange(T value);
}

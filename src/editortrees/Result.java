package editortrees;

public class Result<T> {
	private T value;
	private boolean success = false;

	public T getResult() {
		return this.value;
	}

	public void setResult(T value) {
		this.value = value;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}

package editortrees;

public class Result<T> {
	private T value;
	private boolean success = false;

	public T getResult() {
		return this.value;
	}

	public Result<T> setResult(T value) {
		this.value = value;
		return this;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public Result<T> setSuccess(boolean success) {
		this.success = success;
		return this;
	}
}

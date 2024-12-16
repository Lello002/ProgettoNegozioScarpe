package controlPassword;

public abstract class ControlPassword {

	private ControlPassword next;
	
	public ControlPassword(ControlPassword next) {
		this.next = next;
	}

	public final boolean control(String password) {
		
		if (verify(password)) {
			return true;
		} else if (next != null) {
			return next.control(password);
		}
		return false;
	}

	protected abstract boolean verify(String password);
	
}

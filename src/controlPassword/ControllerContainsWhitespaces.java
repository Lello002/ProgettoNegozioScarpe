package controlPassword;

public class ControllerContainsWhitespaces extends ControlPassword{
	
	public ControllerContainsWhitespaces(ControlPassword next) {
		super(next);
	}

	@Override
	protected boolean verify(String password) {
		return password.chars().anyMatch(Character::isWhitespace);
	}

}

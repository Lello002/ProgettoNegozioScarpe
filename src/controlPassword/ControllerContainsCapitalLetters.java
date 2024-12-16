package controlPassword;

public class ControllerContainsCapitalLetters extends ControlPassword{
	
	public ControllerContainsCapitalLetters(ControlPassword next) {
		super(next);
	}

	@Override
	protected boolean verify(String password) {
		return !password.chars().anyMatch(Character::isUpperCase);
	}

}

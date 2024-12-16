package controlPassword;

public class ControllerContainsSpecialCharacters extends ControlPassword{

	public ControllerContainsSpecialCharacters(ControlPassword next) {
		super(next);	
	}

	@Override
	protected boolean verify(String password) {
		return !password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

	}
	
}

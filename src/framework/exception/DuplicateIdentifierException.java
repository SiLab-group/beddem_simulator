package framework.exception;

public class DuplicateIdentifierException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateIdentifierException(String message) {
		super(message);
	}

}

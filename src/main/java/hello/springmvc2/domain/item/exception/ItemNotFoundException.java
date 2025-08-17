package hello.springmvc2.domain.item.exception;

@SuppressWarnings("serial")
public class ItemNotFoundException extends RuntimeException {

	public ItemNotFoundException(String message) {
		super(message);
	}
	
}

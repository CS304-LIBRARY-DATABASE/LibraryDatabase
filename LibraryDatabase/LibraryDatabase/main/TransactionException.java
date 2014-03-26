package main;

@SuppressWarnings("serial")
public class TransactionException extends Exception {

	private String message;

	public TransactionException(String message){
		super();
		this.message = message;
	}

	public String getMessage(){
		return null;
	}


}

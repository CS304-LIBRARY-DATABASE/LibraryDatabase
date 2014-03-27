package main;

@SuppressWarnings("serial")
public class TransactionException extends Exception {

	public TransactionException(String message){
		super(message);
	}

	public TransactionException(){
		super();
	}
}

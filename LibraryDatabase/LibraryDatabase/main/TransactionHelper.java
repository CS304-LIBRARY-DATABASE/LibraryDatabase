package main;

import java.util.List;
import java.util.ArrayList;

public class TransactionHelper {

	public static void checkout(String [] properties, String sinOrStNo) {
		String type = null;
		String bid = null;
		try {
			// check if borrower is valid
			String bidType = TransactionManager.verifyBorrower(sinOrStNo);
			String [] spl = bidType.split(",");
			bid = spl[0];
			type = spl[1];
		} catch (TransactionException e) {
			Main.makeErrorAlert(e.getMessage());
			e.printStackTrace();
			return;
		}
		
		String result = "";
		for (int i = 1; i < properties.length; i++) {
			String callNumber = properties[i].trim();
			try {
				String copyNo = TransactionManager.checkAvailability(callNumber);
				if (copyNo != null) {
					// this book is available, create a borrowing record in db
					result += TransactionManager.checkoutBook(callNumber, bid, copyNo, type);
				}
			} catch (TransactionException e) {
				Main.makeErrorAlert(e.getMessage());
				e.printStackTrace();
				return;
			}
		}
		
		// output result
		if (result.isEmpty()) {
			Main.writeToOutputBox("None of the requested books are available");
		} else { 
			Main.writeToOutputBox(result);
		}
	}

	public static void addBook(String[] fields) {
		String callNumber = fields[0];
		
		// check if the book exists already, in which case only add a new copy
		try {
			boolean exists = TransactionManager.checkIfBookExists(callNumber);
			if (exists) {
				// add a new copy
				TransactionManager.addNewBookCopy(fields);
			} else {
				// add a new book, and copy C1
				TransactionManager.addNewBook(fields);
			}
		} catch (TransactionException e) {
			Main.makeErrorAlert(e.getMessage());
			e.printStackTrace();
			return;
		}
		
	}
	
	public static List<String> getCommaSeparatedVals(String input) {
		if (input == null)
			return null;
		input = input.trim();
		if (input.isEmpty())
			return null;
		
		String [] arr = input.split(",");
		List<String> result = new ArrayList<String>();
		
		for (int i=0; i < arr.length; i++) {
			String s = arr[i].trim();
			if (!s.isEmpty())
				result.add(s);
		}
		if (result.isEmpty())
			return null;
		return result;
	}

	public static void payFine(String sinOrStNo) throws TransactionException {
		TransactionManager.payFine(sinOrStNo);
	}
}

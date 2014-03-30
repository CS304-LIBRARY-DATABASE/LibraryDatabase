package main;

import java.util.List;
import java.util.ArrayList;

public class TransactionHelper {

	public static void checkout(String [] properties, String bid) {
		String type = null;
		try {
			// check if borrower is valid
			String bidType = TransactionManager.verifyBorrower(bid);
			String [] spl = bidType.split(",");
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

	public static void addBook(String[] fields) throws TransactionException {
		String callNumber = fields[0];

		// check if the book exists already, in which case only add a new copy

		boolean exists = TransactionManager.checkIfBookExists(callNumber);
		if (exists) {
			// add a new copy
			TransactionManager.addNewBookCopy(fields);
		} else {
			// add a new book, and copy C1
			TransactionManager.addNewBook(fields);
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

	public static void payFine(String bid) throws TransactionException {
		TransactionManager.payFine(bid);
	}


	/**
	 * Search for books using keyword search on titles, authors and subjects. The result is a 
	 * list of books that match the search together with the number of copies that are in and out.
	 * @return 
	 */
	public static String searchBy(String searchBy, String key) throws TransactionException {
		if (searchBy.equals("Title")) {
			return TransactionManager.searchByTitle(key);
		} else if (searchBy.equals("Author")) {
			return TransactionManager.searchByAuthor(key);
		} else if (searchBy.equals("Subject")) {
			return TransactionManager.searchBySubject(key);
		} else {
			throw new TransactionException("Error, unexpected searchBy string " + searchBy);
		}
	}
}

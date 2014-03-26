package main;

public class TransactionHelper {

	public static void checkout(String [] properties, String bid) {
		String type = null;
		try {
			// check if borrower is valid
			type = TransactionManager.verifyBorrower(bid);
		} catch (TransactionException e) {
			Main.makeErrorAlert(e.getMessage());
			return;
		}
		
		String result = "";
		for (int i = 1; i < properties.length; i++) {
			try {
				String copyNo = TransactionManager.checkAvailability(properties[i]);
				if (copyNo != null) {
					// this book is available, create a borrowing record in db
					result += TransactionManager.checkoutBook(properties[i], bid, copyNo, type);
				}
			} catch (TransactionException e) {
				Main.makeErrorAlert(e.getMessage());
				return;
			}
		}
		
		// output result
		Main.writeToOutputBox(result);
	}
}

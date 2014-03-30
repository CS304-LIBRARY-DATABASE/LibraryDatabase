package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
			type = spl[1];
			bid = spl[0];
		} catch (TransactionException e) {
			Main.makeErrorAlert(e.getMessage());
			e.printStackTrace();
			return;
		}

		String result = "";
		for (int i = 1; i < properties.length; i++) {
			String callNumber = properties[i].trim();
			try {
				String copyNo = checkAvailability(callNumber);
				if (copyNo != null) {
					// this book is available, create a borrowing record in db
					result += TransactionManager.checkoutBook(callNumber, bid, copyNo, type);
				}
				else {
					copyNo = checkOnHold(callNumber, bid);

					if(copyNo != null){

						Statement  stmt;
						ResultSet  rs;
						Connection con = DbConnection.getJDBCConnection();
						stmt = con.createStatement();

						rs = TransactionManager.executeQuery("SELECT hid"
								+ " FROM HoldRequest NATURAL JOIN Borrower"
								+ " WHERE callNumber = '" + callNumber + "'"
								+ " ORDER BY issuedDate", stmt);

						String hid = "";
						if(rs.next()){
							hid = rs.getString(1);
							
							PreparedStatement ps = con.prepareStatement("DELETE FROM HoldRequest WHERE hid = '" + hid + "'");
							TransactionManager.executeUpdate(ps, con);

							result += TransactionManager.checkoutBook(callNumber, bid, copyNo, type);
						}
					}
				}
			} catch (Exception e) {
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

	/**
	 * Checks if a copy of a book with given callNumber is "in"
	 * @param callNumber
	 * @return copyNumber of the copy if there is a copy with status "in", null otherwise
	 * @throws TransactionException
	 */
	public static String checkAvailability(String callNumber) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String copyNo = null;
		try {
			Connection con = DbConnection.getJDBCConnection();

			stmt = con.createStatement();

			rs = TransactionManager.executeQuery("SELECT copyNo FROM BookCopy "
					+ "where callNumber = '" + callNumber + "' AND status = 'in'", stmt);
			if(rs.next())
				copyNo = rs.getString(1);

			rs.close();
			stmt.close();
			return copyNo;

		} catch (SQLException ex) {
			throw new TransactionException("Error: " + ex.getMessage());
		}
	}

	public static String checkOnHold(String callNumber, String bid) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String copyNo = null;
		try {
			Connection con = DbConnection.getJDBCConnection();

			stmt = con.createStatement();

			rs = TransactionManager.executeQuery("SELECT copyNo"
					+ " FROM BookCopy NATURAL JOIN holdRequest"
					+ " WHERE callNumber = '" + callNumber + "'"
					+ " AND status = 'on-hold'"
					+ " AND bid = '" + bid + "'", stmt);
			if(rs.next())
				copyNo = rs.getString(1);

			rs.close();
			stmt.close();
			return copyNo;

		} catch (SQLException ex) {
			throw new TransactionException("Error: " + ex.getMessage());
		}
	}

	public static boolean addBook(String[] fields) throws TransactionException {
		String callNumber = fields[0];

		// check if the book exists already, in which case only add a new copy

		boolean exists = TransactionManager.checkIfBookExists(callNumber);
		if (exists) {
			// add a new copy
			int result = Main.createInfoAlert("", "The book with CallNumber " + fields[0] +
					" already exists in\nthe database. Would you like to add a new copy of this book?");
			if (result == 0) {
				TransactionManager.addNewBookCopy(callNumber);
				return true;
			}
			return false;
		} else {
			// add a new book, and copy C1
			TransactionManager.addNewBook(fields);
			return true;
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

	public static String hasBooksOut(String sinOrStNo) throws TransactionException {
		try {
			Connection con = DbConnection.getJDBCConnection();
			Statement stmt = con.createStatement();

			ResultSet rs = TransactionManager.executeQuery("SELECT DISTINCT callNumber, bid, name, emailAddress, outDate, inDate"
					+ " FROM Borrowing NATURAL JOIN BookCopy NATURAL JOIN Borrower"
					+ " WHERE sinOrStNo = '" + sinOrStNo + "' and status = 'out'"
					, stmt);

			String result = getDisplayString(rs);
			rs.close();
			stmt.close();
			return result;

		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}

	public static String hasHoldRequests(String sinOrStNo) throws TransactionException {
		try {
			Connection con = DbConnection.getJDBCConnection();
			Statement stmt = con.createStatement();

			ResultSet rs = TransactionManager.executeQuery("SELECT DISTINCT callNumber, bid, name, emailAddress, hid, issuedDate "
					+ " FROM Borrower NATURAL JOIN Book NATURAL JOIN HoldRequest"
					+ " WHERE sinOrStNo = '" + sinOrStNo + "'"
					, stmt);

			String result = getDisplayString(rs);
			rs.close();
			stmt.close();
			return result;

		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}



	public static String getDisplayString (ResultSet rs) throws SQLException, TransactionException{

		// get info on ResultSet
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCols = rsmd.getColumnCount();
		String[] column = new String[numCols];

		int [] columnTypes = new int[numCols];
		int maxNameLength = 0;
		// get column names;
		for (int i = 0; i < numCols; i++) {
			column[i] = rsmd.getColumnName(i+1);
			columnTypes[i] = rsmd.getColumnType(i+1);

			if (column[i].length() > maxNameLength) {
				maxNameLength = column[i].length();
			}
		}

		String result = "";

		while(rs.next()) {
			result += "------------------------------------"
					+ "------------------------------------\n";
			for (int i = 0; i < numCols; i++) {
				result += column[i] + ": ";
				// align with spaces
				for (int j = 0; j < maxNameLength - column[i].length(); j++) {
					result += "  ";
				}

				switch (columnTypes[i]) {
				case Types.VARCHAR:
				case Types.CHAR:
					result += rs.getString(i+1) + "\n";
					break;
				case Types.INTEGER:
				case Types.NUMERIC:
					result += rs.getInt(i+1) + "\n";
					break;
				case Types.FLOAT:
				case Types.DOUBLE:
					result += rs.getDouble(i+1) + "\n";
					break;
				case Types.DATE:
					result += rs.getDate(i+1) + "\n";
					break;
				default:
					throw new TransactionException("Error: unexpected type " +
							columnTypes[i] + " encountered in"
							+ " TransactionManager.getDisplayString()");
				}
			}
			result = result.trim() + "\n";
		}

		return result;
	}

	@SuppressWarnings("deprecation")
	public static java.sql.Date dateFromCalendar(Calendar c){
		int year = c.get(1) -1900;
		int month = c.get(2);
		int day = c.get(5);

		return new java.sql.Date(year, month, day);
	}

	@SuppressWarnings("deprecation")
	public static java.sql.Date dateFromString(String s) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date;
		date = sdf.parse(s);
		return new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
	}
}

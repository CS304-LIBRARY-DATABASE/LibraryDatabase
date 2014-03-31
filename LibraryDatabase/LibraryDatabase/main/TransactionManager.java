package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;


public class TransactionManager {

	/**
	 * Execute Insert, Update, or Delete statements
	 * @param ps
	 * @param con
	 * @return
	 */
	public static int executeUpdate(PreparedStatement  ps, Connection con) throws TransactionException {
		try {
			int rowCount = ps.executeUpdate();

			// commit work 
			con.commit();
			ps.close();

			return rowCount;

		} catch (SQLException e) {
			System.out.println("Message: " + e.getMessage());
			try  {
				// undo the update
				con.rollback();	
				throw new TransactionException(e.getMessage());
			}
			catch (SQLException ex) {
				System.out.println("Message: " + ex.getMessage());
				System.exit(-1);
			}
			return -1;
		}
	}


	public static ResultSet executeQuery(String query, Statement stmt) throws TransactionException {
		try {
			return stmt.executeQuery(query);

		} catch (SQLException e) {
			System.out.println("Message: " + e.getMessage());
			throw new TransactionException(e.getMessage());
		}
	}

	public static void executeQuery(String query) throws TransactionException  {
		try {
			Connection con = DbConnection.getJDBCConnection();
			PreparedStatement ps = con.prepareStatement(query);
			executeUpdate(ps, con);

		} catch (SQLException e) {
			System.out.println("Message: " + e.getMessage());
			throw new TransactionException(e.getMessage());
		}
	}

	/**
	 * Add a new borrower to Borrower Table
	 * @param attributes
	 * @throws TransactionException 
	 */
	public static void addBorrower(String[] attributes) throws TransactionException {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
			ps = con.prepareStatement("INSERT INTO Borrower VALUES (bid_sequence.nextval,?,?,?,?,?,?,?,?)");

			ps.setString(1, attributes[0]);
			ps.setString(2, attributes[1]);
			ps.setString(3, attributes[2]);
			ps.setString(4, VerifyAttributes.parsePhoneNumber(attributes[3]));
			ps.setString(5, attributes[4]);
			ps.setString(6, attributes[5]);

			ps.setDate(7, TransactionHelper.dateFromString(attributes[6]));
			ps.setString(8, attributes[7].toLowerCase());

			executeUpdate(ps, con);

		} catch (SQLException | ParseException e) {
			System.out.println("addBorrower Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}

	/**
	 * Select all borrowers in Borrower table and return as String
	 * @return
	 */
	public static String listTableConents(String tableName) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String result = "";

		try {
			Connection con = DbConnection.getJDBCConnection();
			stmt = con.createStatement();

			rs = executeQuery("SELECT * FROM " + tableName, stmt);

			result = TransactionHelper.getDisplayString(rs);

			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		} catch (SQLException e) {
			throw new TransactionException("Error: " + e.getMessage());
		}
		return result;
	}

	/**
	 * Check if a borrower's expiryDate has passed
	 * And check if the borrower has any outstanding fines
	 * @param bid
	 * @return
	 */
	public static String verifyBorrower(String sinOrStNo) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String type = null;
		String bid = null;
		try {
			Connection con = DbConnection.getJDBCConnection();
			stmt = con.createStatement();

			rs = executeQuery("SELECT * FROM Borrower where sinOrStNo = '" + sinOrStNo + "'", stmt);

			if (rs.next()) {
				bid = rs.getString(1);
				type = rs.getString(9);
				Date expiryDate = rs.getDate(8);
				// borrower has expired
				if (expiryDate.before(new java.util.Date())) {
					throw new TransactionException("Borrower with ID " + bid + " has a"
							+ " past expiry date of " + expiryDate.toString());
				}
			}
			// no borrowers with bid found
			else {
				throw new TransactionException("Borrower with ID " + bid + " does not exist");
			}
			rs.close();
			rs = executeQuery("SELECT callNumber, copyNo, amount"
					+ " FROM Borrower natural join Borrowing natural join Fine"
					+ " where bid = '" + bid + "' and paidDate is null", stmt);
			String error = "";
			int i=0;
			while (rs.next()) {
				if (i == 0) {
					error += "Error, Borrower has outstanding fine(s) on:\n" + 
							"| callNumber | copyNumber | fine amount |\n";
				}
				error += "| " + rs.getString(1) + " | ";
				error += rs.getString(2) + " | ";
				error += rs.getString(3) + " |\n";
				i++;
			}
			if (!error.isEmpty()) {
				throw new TransactionException(error);
			}

			// close the statement; 
			// the ResultSet will also be closed
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			throw new TransactionException("Error: " + ex.getMessage());
		}
		if (type == null || bid == null)
			return null;
		return bid.trim() + "," + type.trim();
	}

	public static String checkoutBook(String callNumber, String bid, String copyNo, String type) throws TransactionException {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
			ps = con.prepareStatement("Update BookCopy set status = 'out'"
					+ " where callNumber = '" + callNumber + "' and copyNo = '" + copyNo + "'");
			executeUpdate(ps, con);

			ps = con.prepareStatement("Insert into Borrowing values (borid_sequence.nextval,?,?,?,?,?)");
			ps.setString(1, bid);
			ps.setString(2, callNumber);
			ps.setString(3, copyNo);

			Calendar today = Calendar.getInstance();
			Calendar inDate = Calendar.getInstance();

			if (type.equals("borrower") || type.equals("public")) {
				inDate.add(Calendar.DAY_OF_MONTH, 14); // Adding 2 weeks
			} else if (type.equals("faculty")) {
				inDate.add(Calendar.DAY_OF_MONTH, 84); // Adding 12 weeks
			} else if (type.equals("staff")) {
				inDate.add(Calendar.DAY_OF_MONTH, 42); // Adding 6 weeks
			} else {
				System.err.println("Error, unexpected borrower type: " + type);
				System.exit(1);
			}
			ps.setDate(4, TransactionHelper.dateFromCalendar(today));
			ps.setDate(5, TransactionHelper.dateFromCalendar(inDate));

			executeUpdate(ps, con);
			
			String result = "------------------------------------"
					+ "------------------------------------\n";
			result += "callNumber: " + callNumber + "\n";
			result += "copyNo:     " + copyNo + "\n";
			result += "DueDate:    " + TransactionHelper.dateFromCalendar(inDate) + "\n";
			return result;
		} catch (SQLException e) {
			System.out.println("addBorrower Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}

	/**
	 * Processes a return. When an item is returned, the clerk records the
	 * return by providing the item's catalogue number. The system determines
	 * the borrower who had borrowed the item and records that the item is
	 * "in". If the item is overdue, a fine is assessed for the borrower.
	 * If there is a hold request for this item by another borrower, the
	 * item is registered as "on hold" and a message is send to the borrower
	 * who made the hold request.
	 */
	public static String returnItem(String callNumber, String copyNumber) {
		//Fine (fid, amount, issuedDate, paidDate, borid)
		//BookCopy (callNumber, copyNo, status)
		//HoldRequest(hid, bid, callNumber, issuedDate)
		//Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)

		Connection con = DbConnection.getJDBCConnection();
		Statement  stmt;
		ResultSet  rs;
		try {
			checkIfBookExists(callNumber);
		} catch (TransactionException e) {
			e.printStackTrace();
			return "Could not find book " + callNumber;
		}
		
		try {			
			stmt = con.createStatement();
			rs = executeQuery("SELECT status"
					+ " FROM BookCopy"
					+ " WHERE callNumber = '" + callNumber + "'"
					+ " and copyNo = '" + copyNumber + "'", stmt);
			if(!rs.next()) {
				return "Could not find book copy " + callNumber + " " + copyNumber;
			} else {
				String status = rs.getString(1);
				if (!status.equals("out")) {
					return "Could not return book " + callNumber + " " + copyNumber  + ".\n"
							+ "The book is not registered as checked out";
				}
			}
			rs.close();
			stmt.close();
		} catch (TransactionException | SQLException e) {
			e.printStackTrace();
			return "Could not find book " + callNumber + " " + copyNumber;
		}
		Date inDate;
		int borid;
		try {
			stmt = con.createStatement();

			rs = executeQuery("SELECT inDate, borid"
					+ " FROM Borrowing"
					+ " WHERE callNumber = '" + callNumber + "'"
					+ " AND copyNo = '" + copyNumber + "'"
					+ " order by outDate desc", stmt);

			if(rs.next()){
				inDate = rs.getDate(1);
				borid = rs.getInt(2);
			}
			else throw new TransactionException("Error, item returned with no Borrowing history");
			rs.close();
			stmt.close();

		}catch (Exception e){
			e.printStackTrace();
			return "Could not find borrower who is taking out this book.";
		}

		Date today = TransactionHelper.dateFromCalendar(Calendar.getInstance());

		if(inDate.before(today)){

			JOptionPane.showMessageDialog(null, "Assessing fine for late return.", "Overdue book", JOptionPane.INFORMATION_MESSAGE);

			try {
				PreparedStatement ps = con.prepareStatement("INSERT INTO Fine VALUES (fid_sequence.nextval,?,?,?,?)");

				ps.setFloat(1, (float) 5.25);
				ps.setDate(2, today);
				ps.setDate(3, null);
				ps.setInt(4, borid);

				executeUpdate(ps, con);

			} catch (Exception e) {
				e.printStackTrace();
				return "Unable to insert fine tuple.";
			}
		}
		
		// check if the item is on hold by
		// checking how many hold requests for callnumber there are
		// and checking how many items with callnumber are already on hold
		try {			
			stmt = con.createStatement();
			rs = executeQuery("SELECT * "
					+ " FROM BookCopy"
					+ " WHERE callNumber = '" + callNumber + "'"
					+ " and status = 'in'", stmt);
			
			if(rs.next()) {
				// we can set this copy to 'in' and not worry about hold requests
				rs.close();
				stmt.close();
				setBookIn(callNumber, copyNumber);
			} else {
				if (TransactionHelper.isMoreHoldRequestsThenCopiesOnHold(callNumber)) {
					// this copy must go on hold
					setBookOnHold(callNumber, copyNumber);
				} else {
					// we can set this copy to 'in' and not worry about hold requests
					setBookIn(callNumber, copyNumber);
				}
			}
		} catch (TransactionException | SQLException e) {
			e.printStackTrace();
			return "Could not find book " + callNumber + " " + copyNumber;
		}
		return null;
	}


	/**
	 * Hold request for a book that is out: When the item is returned, the system sends an email
	 * to the borrower and informs the library clerk to keep the book out of the shelves.
	 */
	private static void setBookOnHold(String callNumber, String copyNumber) throws TransactionException{
		try {
			Connection con = DbConnection.getJDBCConnection();
			Statement stmt = con.createStatement();

			ResultSet rs = executeQuery("SELECT emailAddress"
					+ " FROM HoldRequest NATURAL JOIN Borrower"
					+ " WHERE callNumber = '" + callNumber + "'"
					+ " ORDER BY issuedDate", stmt);

			String[] emailList = new String[1];
			if(rs.next()) {
				emailList[0] = rs.getString(1);
				
				String emailSubject = "Library - Your book is on hold";
				String emailBody = "Dear Borrower,\nThe book with call number " + callNumber + 
						" that you have placed a hold request on is now available. Please pick it up at your convenience."
						+ "\n \n Thanks, \nLocal Library 304 Staff";
				
				EmailHandler.sendEmail(emailList, emailSubject, emailBody);

				JOptionPane.showMessageDialog(null, "Hold request email has been sent to " + emailList[0] + ", please hold book.", "Hold book", JOptionPane.INFORMATION_MESSAGE);

					PreparedStatement ps = con.prepareStatement("UPDATE BookCopy SET status = 'on-hold'"
							+ " WHERE callNumber = '" + callNumber + "'"
							+ " AND copyNo = '" + copyNumber + "'");
					
					executeUpdate(ps, con);
			}
			rs.close();
			stmt.close();
						
		} catch (SQLException e) {
			throw new TransactionException(e.getMessage());
		}
	}


	private static void setBookIn(String callNumber, String copyNumber) throws TransactionException {
		try {
			Connection con = DbConnection.getJDBCConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE BookCopy SET status = 'in'"
					+ " WHERE callNumber = '" + callNumber + "'"
					+ " AND copyNo = '" + copyNumber + "'");
			
			executeUpdate(ps, con);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new TransactionException(e.getMessage());
		}
	}


	public static boolean checkIfBookExists(String callNumber) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		try {
			Connection con = DbConnection.getJDBCConnection();
			stmt = con.createStatement();

			boolean result;

			rs = executeQuery("SELECT * FROM Book b where b.callNumber = '" + callNumber + "'", stmt);

			if (rs.next()) {
				result = true;
			}
			// no borrowers with bid found
			else {
				result = false;
			}
			rs.close();
			stmt.close();
			return result;

		} catch (SQLException ex) {
			throw new TransactionException("Error: " + ex.getMessage());
		}
	}

	public static boolean checkIfBookExists(String callNumber, String copyNumber) throws TransactionException {
		if (checkIfBookExists(callNumber)) {
			// book exists, check if book copy exists
			Statement  stmt;
			ResultSet  rs;
			try {
				Connection con = DbConnection.getJDBCConnection();
				stmt = con.createStatement();
	
				boolean result;
	
				rs = executeQuery("SELECT * FROM BookCopy WHERE copyNo = '" + copyNumber + "'"
						+ " and callNumber = '" + callNumber + "'", stmt);
	
				if (rs.next()) {
					result = true;
				}
				else {
					result = false;
				}
				rs.close();
				stmt.close();
				return result;
	
			} catch (SQLException ex) {
				throw new TransactionException("Error: " + ex.getMessage());
			}
		} else {
			return false;
		}
	}


	public static void addNewBook(String[] fields) throws TransactionException {
		String callNumber = fields[0].trim();
		String isbn = fields[1].trim();
		String title = fields[2].trim();
		String mainAuthor = fields[3].trim();
		String publisher = fields[4].trim();
		int year = Integer.parseInt(fields[5]);

		List<String> additionalAuthors = TransactionHelper.getCommaSeparatedVals(fields[6]);
		List<String> subjects = TransactionHelper.getCommaSeparatedVals(fields[7]);

		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
			ps = con.prepareStatement("INSERT INTO Book VALUES (?,?,?,?,?,?)");

			ps.setString(1, callNumber);
			ps.setString(2, isbn);
			ps.setString(3, title);
			ps.setString(4, mainAuthor);
			ps.setString(5, publisher);
			ps.setInt(6, year);

			executeUpdate(ps, con);

			ps = con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");

			ps.setString(1, callNumber);
			ps.setString(2, "C1");
			ps.setString(3, "in");

			executeUpdate(ps, con);

			if (additionalAuthors != null) {
				for (String author : additionalAuthors) {
					ps = con.prepareStatement("INSERT INTO HasAuthor VALUES (?,?)");
					ps.setString(1, callNumber);
					ps.setString(2, author);
					executeUpdate(ps, con);
				}
			}
			if (subjects != null) {
				for (String subject : subjects) {
					ps = con.prepareStatement("INSERT INTO HasSubject VALUES (?,?)");
					ps.setString(1, callNumber);
					ps.setString(2, subject);
					executeUpdate(ps, con);
				}
			}
		} catch (SQLException e) {
			System.out.println("addBorrower Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}


	public static void addNewBookCopy(String callNumber) throws TransactionException {
		callNumber = callNumber.trim();
		String copyNo;
		
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {

			// determine next copy number
			Statement  stmt = con.createStatement();

			ResultSet rs = executeQuery("select copyNo from bookcopy where "
					+ "callnumber='" + callNumber + "' order by copyNo desc", stmt);

			String existingCopyNum;

			if (rs.next()) {
				existingCopyNum = rs.getString(1);
				int num = Integer.parseInt(existingCopyNum.trim().substring(1)) + 1;
				copyNo = "C" + num;

				rs.close();
				stmt.close();

				ps = con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");

				ps.setString(1, callNumber);
				ps.setString(2, copyNo);
				ps.setString(3, "in");

				executeUpdate(ps, con);
			} else {
				throw new TransactionException("Error: no Books found "
						+ "with callNumber: " + callNumber);
			}
		} catch (SQLException e) {
			System.out.println("addBook Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
		
		try{
			setBookOnHold(callNumber, copyNo);
		} catch(Exception e){
			//do nothing
		}
	}


	public static void payFine(String sinOrStNo) throws TransactionException {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
			ps = con.prepareStatement("Update Fine set paidDate = ? where fid in (select fid "
					+ "from Borrower natural join Borrowing natural join Fine "
					+ "where sinOrStNo = '" + sinOrStNo + "' and paidDate is null)");

			Calendar today = Calendar.getInstance();
			ps.setDate(1, TransactionHelper.dateFromCalendar(today));

			executeUpdate(ps, con);

		} catch (SQLException e) {
			System.out.println("payFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}


	public static String hasFines(String sinOrStNo) throws TransactionException {
		Connection con = DbConnection.getJDBCConnection();
		try {
			Statement  stmt = con.createStatement();
			ResultSet rs = executeQuery("select callnumber, copyNo, outDate, inDate, "
					+ "fid, amount "
					+ "from Borrower natural join Borrowing natural join Fine "
					+ "where sinOrStNo = '" + sinOrStNo + "' and paidDate is null", stmt);
			
			String result = TransactionHelper.getDisplayString(rs);
			rs.close();
			stmt.close();
			
			if (!result.isEmpty()) {
				// get total fine amount
				stmt = con.createStatement();
				rs = executeQuery("select sum(amount) as FinesTotal "
						+ "from Borrower natural join Borrowing natural join Fine "
						+ "where sinOrStNo = '" + sinOrStNo + "' and paidDate is null", stmt);
				
				result += TransactionHelper.getDisplayString(rs);
				rs.close();
				stmt.close();
			}
			return result;

		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}

	/*
	 * Checks overdue items. The system displays a list of the items that are overdue and
	 * the borrowers who have checked them out. The clerk may decide to send an email messages
	 * to any of them (or to all of them).
	 */
	public static ArrayList<String> checkForOverdueBooks() throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		ArrayList<String> result = new ArrayList<String>();

		try {
			Connection con = DbConnection.getJDBCConnection();
			stmt = con.createStatement();

			rs = executeQuery("SELECT DISTINCT callNumber, copyNo, bid, name, emailAddress, outDate, inDate as DueDate"
					+ " FROM Borrowing NATURAL JOIN BookCopy NATURAL JOIN Borrower"
					+ " WHERE status = 'out'"
					+ " AND inDate < SYSDATE"
					, stmt);

			result.add(TransactionHelper.getDisplayString(rs));


			rs = executeQuery("SELECT DISTINCT bid, emailAddress"
					+ " FROM Borrowing NATURAL JOIN BookCopy NATURAL JOIN Borrower"
					+ " WHERE status = 'out'"
					+ " AND inDate < SYSDATE"
					, stmt);


			while(rs.next())
				result.add(rs.getString(2).trim());


			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransactionException();
		}

		return result;	
	}

	/*
	 * Generate a report with all the books that have been checked out. For each book the report
	 * shows the date it was checked out and the due date. The system flags the items that are overdue.
	 * The items are ordered by the book call number. If a subject is provided the report lists only
	 * books related to that subject, otherwise all the books that are out are listed by the report.
	 */
	public static String checkoutReport(String subject) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String result = "";

		String[] subjectFilter = {"", "", ""};

		if(!subject.isEmpty()){
			subjectFilter[0] = " subject,";
			subjectFilter[1] = " NATURAL JOIN hasSubject";
			subjectFilter[2] = " AND subject LIKE '%" + subject + "%'";
		}

		try {
			Connection con = DbConnection.getJDBCConnection();
			stmt = con.createStatement();

			rs = executeQuery("SELECT DISTINCT callNumber, copyNo, outDate, inDate as DueDate," + subjectFilter[0]
					+ " CASE WHEN inDate < SYSDATE THEN '***yes***'"
					+      " WHEN inDate >= SYSDATE THEN 'no'"
					+      " END AS Overdue"
					+ " FROM Borrowing NATURAL JOIN BookCopy" + subjectFilter[1]
							+ " WHERE status = 'out'" + subjectFilter[2]
									+ " ORDER BY callNumber", stmt);

			result = TransactionHelper.getDisplayString(rs);

			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransactionException();
		}
		return result;	
	}

	/*
	 * Place a hold request for a book that is out. When the item is returned, the system sends an email
	 * to the borrower and informs the library clerk to keep the book out of the shelves.
	 */
	public static String holdRequest(String sinOrStNo, String callNumber) {

		String result = null;
		String bid = null;
		try {
			// check if borrower is valid
			String bidType = TransactionManager.verifyBorrower(sinOrStNo);
			String [] spl = bidType.split(",");
			bid = spl[0];
		} catch (TransactionException e) {
			e.printStackTrace();
			result = e.getMessage();
			return result;
		}
		try {
			String available = TransactionHelper.checkAvailability(callNumber);
			if(available != null){
				result = "Book copy " + available + " is currently available. Cannot issue hold request.";
				return result;
			}

		} catch (TransactionException e) {
			e.printStackTrace();
			result = "Could not find book with call number " + callNumber;
			return result;
		}

		Connection con = DbConnection.getJDBCConnection();
		PreparedStatement ps = null;

		try {
			//HoldRequest(hid, bid, callNumber, issuedDate)
			ps = con.prepareStatement("INSERT INTO HoldRequest VALUES (hid_sequence.nextval,?,?,?)");

			ps.setString(1, bid);
			ps.setString(2, callNumber);

			Calendar today = Calendar.getInstance();
			ps.setDate(3, TransactionHelper.dateFromCalendar(today));
			executeUpdate(ps, con);

		} catch (Exception e) {
			e.printStackTrace();
			result = "Transaction failed, please try again.";
		}

		return result;
	}

	/*
	 * Generate a report with the most popular items in a given year. The librarian provides a year and
	 * a number n. The system lists out the top n books that where borrowed the most times during that year.
	 * The books are ordered by the number of times they were borrowed.
	 */
	public static String popularBooks(String year, String n) throws TransactionException {
		//Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)
		//Book (callNumber, isbn, title, mainAuthor, publisher, year )

		Statement  stmt;
		ResultSet  rs;
		String result = "";

		try {
			Connection con = DbConnection.getJDBCConnection();
			stmt = con.createStatement();

			rs = executeQuery("select * from (SELECT callNumber, title, mainAuthor, year, COUNT(callNumber) AS count" 
					+ " FROM Borrowing NATURAL JOIN Book" 
					+ " Where (EXTRACT(year FROM indate) = " + year + " OR EXTRACT(year FROM outdate) = " + year + ")"
					+ " GROUP BY callNumber, title, mainAuthor, year"
					+ " ORDER BY count DESC)"
					+ " WHERE ROWNUM <= " + n
					, stmt);

			result = TransactionHelper.getDisplayString(rs);

			// close the statement; 
			// the ResultSet will also be closed
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransactionException();
		}
		return result;	

	}


	/**
	 * Search for books using keyword search on titles, authors and subjects. The result is a 
	 * list of books that match the search together with the number of copies that are in and out.
	 * @return 
	 */
	public static String searchByTitle(String title) throws TransactionException {
		Connection con = DbConnection.getJDBCConnection();
		try {
			Statement  stmt = con.createStatement();

			ResultSet rs = executeQuery("select callnumber, isbn, title, mainauthor, publisher, year, "
					+ "(select count(*) from "
					+ "BookCopy bc where b.callNumber = bc.callNumber and status = 'in') as NumberIn, "
					+ "(select count(*) from "
					+ "BookCopy bcO where b.callNumber = bcO.callNumber and status = 'out') as NumberOut "
					+ "from Book b "
					+ "where upper(title) like upper('%" + title + "%')", stmt);

			String result = TransactionHelper.getDisplayString(rs);
			rs.close();
			stmt.close();
			return result;

		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}


	public static String searchByAuthor(String author) throws TransactionException {
		Connection con = DbConnection.getJDBCConnection();
		try {
			Statement  stmt = con.createStatement();

			ResultSet rs = executeQuery("select distinct b.callnumber, b.isbn, b.title, b.mainauthor, ha.name, b.publisher, b.year, "
					+ "(select count(*) from "
					+ "BookCopy bc where b.callNumber = bc.callNumber and bc.status = 'in') as NumberIn, "
					+ "(select count(*) from "
					+ "BookCopy bcO where b.callNumber = bcO.callNumber and bcO.status = 'out') as NumberOut "
					+ "from Book b, HasAuthor ha "
					+ "where (b.callNumber = ha.callNumber and upper(ha.name) like upper('%" + author + "%')) or "
					+ "upper(b.mainAuthor) like upper('%" + author + "%')", stmt);

			String result = TransactionHelper.getDisplayString(rs);

			rs.close();
			stmt.close();
			return result;

		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}


	public static String searchBySubject(String subject) throws TransactionException {
		Connection con = DbConnection.getJDBCConnection();
		try {
			Statement  stmt = con.createStatement();

			ResultSet rs = executeQuery("select distinct b.callnumber, b.isbn, b.title, b.mainauthor,"
					+ "b.publisher, b.year, hs.subject, "
					+ "(select count(*) from "
					+ "BookCopy bc where b.callNumber = bc.callNumber and bc.status = 'in') as NumberIn, "
					+ "(select count(*) from "
					+ "BookCopy bcO where b.callNumber = bcO.callNumber and bcO.status = 'out') as NumberOut "
					+ "from Book b, HasSubject hs "
					+ "where b.callNumber = hs.callNumber and upper(hs.subject) like upper('%" + subject + "%')", stmt);

			String result = TransactionHelper.getDisplayString(rs);
			rs.close();
			stmt.close();
			return result;

		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}

}

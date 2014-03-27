package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TransactionManager {
	
	private static long borrowingID = 0;
	
	/**
	 * Execute Insert, Update, or Delete statements
	 * @param ps
	 * @param con
	 * @return
	 */
	private static int executeUpdate(PreparedStatement  ps, Connection con) throws TransactionException {
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
	
	
	private static ResultSet executeQuery(String query, Statement stmt) throws TransactionException {
		try {
		  return stmt.executeQuery(query);
		  
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
			ps = con.prepareStatement("INSERT INTO Borrower VALUES (?,?,?,?,?,?,?,?,?)");
			
			ps.setString(1, attributes[0]);
			ps.setString(2, attributes[1]);
			ps.setString(3, attributes[2]);
			ps.setString(4, attributes[3]);
			ps.setString(5, VerifyAttributes.parsePhoneNumber(attributes[4]));
			ps.setString(6, attributes[5]);
			ps.setString(7, attributes[6]);
			
			java.util.Date d = VerifyAttributes.stringToDate(attributes[7]);
			ps.setDate(8, new Date(d.getYear(), d.getMonth(), d.getDay()));
			ps.setString(9, attributes[8].toLowerCase());
			
			executeUpdate(ps, con);
		
		} catch (SQLException | ParseException e) {
			System.out.println("addBorrower Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
//		    try  {
//				ps.cancel();	
//		    }
//		    catch (SQLException ex) {
//				System.out.println("Message: " + ex.getMessage());
//				System.exit(-1);
//		    }
		}
	}
	
	/**
	 * Select all borrowers in Borrower table and return as String
	 * @return
	 */
	public static String listBorrowers() throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String result = "| bid | PW | Name | Address | Phone | Email | Sin/StNo | ExpDate | Type |\n";
		   
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  stmt = con.createStatement();

		  rs = executeQuery("SELECT * FROM Borrower", stmt);
		  
		  while(rs.next()) {
			  result += "| ";
		      result += rs.getString(1) + " | ";
		      result += rs.getString(2) + " | ";
		      result += rs.getString(3) + " | ";
		      result += rs.getString(4) + " | ";
		      result += rs.getString(5) + " | ";
		      result += rs.getString(6) + " | ";
		      result += rs.getString(7) + " | ";
		      result += rs.getDate(8) + " | ";
		      result += rs.getString(9) + " |\n";
		  }
	 
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
	public static String verifyBorrower(String bid) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		String type = null;
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  stmt = con.createStatement();

		  rs = executeQuery("SELECT * FROM Borrower b where b.bid = '" + bid + "'", stmt);
		  
		  if (rs.next()) {
			  type = rs.getString(9);
			  Date expiryDate = rs.getDate(8);
			  // borrower has expired
			  if (expiryDate.before(new java.util.Date())) {
				  throw new TransactionException("Borrower with bid " + bid + " has a"
				  		+ " past expiry date of " + expiryDate.toString());
			  }
		  }
		  // no borrowers with bid found
		  else {
			  throw new TransactionException("Borrower with bid " + bid + " does not exist");
		  }
		  
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
		  stmt.close();
		} catch (SQLException ex) {
			throw new TransactionException("Error: " + ex.getMessage());
		}
		return type;
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
		   
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  
		  stmt = con.createStatement();
		  
		  String result = null;
		  
		  rs = executeQuery("SELECT copyNo FROM BookCopy b "
		  		+ "where b.callNumber = '" + callNumber + "' and b.status = 'in'", stmt);
		  
		  if (rs.next()) {
			  result = rs.getString(1);
		  }
		  stmt.close();
		  return result;
		  
		} catch (SQLException ex) {
			throw new TransactionException("Error: " + ex.getMessage());
		}
	}


	public static String checkoutBook(String callNumber, String bid, String copyNo, String type) throws TransactionException {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		String result = "| callNumber | copyNo | inDate |\n";
		try {
			ps = con.prepareStatement("Update BookCopy set status = 'out'"
					+ " where callNumber = '" + callNumber + "'");
			executeUpdate(ps, con);
			
			ps = con.prepareStatement("Insert into Borrowing values (?,?,?,?,?,?)");
			ps.setString(1, "" + borrowingID++);
			ps.setString(2, bid);
			ps.setString(3, callNumber);
			ps.setString(4, copyNo);
			
			java.util.Date today = new java.util.Date();
			java.util.Date inDate = null;
			
			Calendar c = Calendar.getInstance();
			c.setTime(today); // Now use today date.
			
			if (type.equals("borrower") || type.equals("public")) {
				c.add(Calendar.DAY_OF_MONTH, 2*7); // Adding 2 weeks
				inDate = c.getTime();
			} else if (type.equals("faculty")) {
				c.add(Calendar.DAY_OF_MONTH, 12*7); // Adding 12 weeks
				inDate = c.getTime();
			} else if (type.equals("staff")) {
				c.add(Calendar.DAY_OF_MONTH, 6*7); // Adding 6 weeks
				inDate = c.getTime();
			} else {
				System.err.println("Error, unexpected borrower type: " + type);
				System.exit(1);
			}
			ps.setDate(5, new Date(today.getYear(), today.getMonth(), today.getDay()));
			ps.setDate(6, new Date(inDate.getYear(), inDate.getMonth(), inDate.getDay()));
			
			executeUpdate(ps, con);
			
			result += "| " + callNumber + " | ";
			result += copyNo + " | ";
			result +=  inDate + " |\n";
			
		} catch (SQLException e) {
			System.out.println("addBorrower Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
		
		return result;
	}
}

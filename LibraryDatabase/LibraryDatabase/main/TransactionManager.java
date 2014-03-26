package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class TransactionManager {
	
	/**
	 * Execute Insert, Update, or Delete statements
	 * @param ps
	 * @param con
	 * @return
	 */
	private static int executeUpdate(PreparedStatement  ps, Connection con) {
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
		    }
		    catch (SQLException ex) {
				System.out.println("Message: " + ex.getMessage());
				System.exit(-1);
		    }
		    return -1;
		}
	}
	
	
	private static ResultSet executeQuery(String query) {
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  Statement stmt = con.createStatement();

		  return stmt.executeQuery(query);
		  
		} catch (SQLException e) {
			System.out.println("Message: " + e.getMessage());
		    return null;
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
		ps.setDate(8, VerifyAttributes.parseDate(attributes[7]));
		ps.setString(9, attributes[8]);
		
	    
		} catch (SQLException e) {
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
		executeUpdate(ps, con);
	}
	
	/**
	 * Select all borrowers in Borrower table and return as String
	 * @return
	 */
	public static String listBorrowers() {
		Statement  stmt;
		ResultSet  rs;
		String result = "| bid | PW | Name | Address | Phone | Email | Sin/StNo | ExpDate | Type |\n";
		   
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  stmt = con.createStatement();

		  rs = executeQuery("SELECT * FROM Borrower");
		  
		  int i = 0;
		  while(rs.next()) {
			  i++;
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
		} catch (SQLException ex) {
		    System.out.println("Message: " + ex.getMessage());
		}
		return result;
    }
	
	/**
	 * Check if a borrower's expiryDate has passed
	 * And check if the borrower has any outstanding fines
	 * @param bid
	 * @return
	 */
	public static void verifyBorrower(String bid) throws TransactionException {
		Statement  stmt;
		ResultSet  rs;
		   
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  stmt = con.createStatement();

		  rs = executeQuery("SELECT * FROM Borrower b where b.bid = '" + bid + "'");
		  
		  if (rs.next()) {
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
		  
		  rs = executeQuery("SELECT bw.callNumber, bw.copyNo, f.amount"
		  		+ "FROM Borrower b natural join Borrowing bw natural join Fine f"
		  		+ " where b.bid = '" + bid + "' and f.paidDate is null");
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
	}


	public static void checkAvailability(String[] properties) {
		// TODO Auto-generated method stub
		
	}
}

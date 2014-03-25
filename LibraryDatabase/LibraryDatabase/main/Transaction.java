package main;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


public class Transaction {
	
	/**
	 * Execute Insert, Update, or Delete statements
	 * @param ps
	 * @param con
	 * @return
	 */
	private int executeUpdate(PreparedStatement  ps, Connection con) {
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
	
	
	private ResultSet executeQuery(String query) {
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
	 */
	public void addBorrower(String[] attributes) {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
		ps = con.prepareStatement("INSERT INTO Borrower VALUES (?,?,?,?,?,?,?,?)");
		
		ps.setString(1, attributes[1]);
		ps.setString(2, attributes[2]);
		ps.setString(3, attributes[3]);
		ps.setString(4, attributes[4]);
		ps.setInt(5, VerifyAttributes.parsePhoneNumber(attributes[5]));
		ps.setString(6, attributes[6]);
		ps.setDate(7, VerifyAttributes.parseDate(attributes[7]));
		ps.setString(8, attributes[8]);
		
	    
		} catch (SQLException e) {
			System.out.println("Message: " + e.getMessage());
//		    try  {
//				ps.cancel();	
//		    }
//		    catch (SQLException ex) {
//				System.out.println("Message: " + ex.getMessage());
//				System.exit(-1);
//		    }
			return;
		}
		executeUpdate(ps, con);
	}
	
	/**
	 * Select all borrowers in Borrower table and return as String
	 * @return
	 */
	public String listBorrowers() {
		String     bid;
		String     bname;
		String     baddr;
		String     bcity;
		String     bphone;
		Statement  stmt;
		ResultSet  rs;
		String result = "";
		   
		try {
		  Connection con = DbConnection.getJDBCConnection();
		  stmt = con.createStatement();

		  rs = executeQuery("SELECT * FROM Borrower");

		  // get info on ResultSet
		  ResultSetMetaData rsmd = rs.getMetaData();

		  // get number of columns
		  int numCols = rsmd.getColumnCount();

		  // display column names;
		  for (int i = 0; i < numCols; i++) {
		      // get column name and print it
			  System.out.printf("%-15s", rsmd.getColumnName(i+1));    
		  }

		  
		  int i = 0;
		  while(rs.next()) {
			  i++;
		      result += rs.getNString(1);
		  }
	 
		  // close the statement; 
		  // the ResultSet will also be closed
		  stmt.close();
		} catch (SQLException ex) {
		    System.out.println("Message: " + ex.getMessage());
		}
		return result;
    }
}

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
	
	public void addBorrower(String[] attributes) {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
		ps = con.prepareStatement("INSERT INTO Borrower VALUES (?,?,?,?,?,?,?,?)");
		
		Date d = new Date(0, 0, 0); //attributes[7]
		
		ps.setString(1, attributes[1]);
		ps.setString(2, attributes[2]);
		ps.setString(3, attributes[3]);
		ps.setString(4, attributes[4]);
		ps.setString(5, attributes[5]);
		ps.setString(6, attributes[6]);
		ps.setDate(7, d);
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
	
	/*
     * display information about branches
     */ 
    private void showBranch()
    {
	String     bid;
	String     bname;
	String     baddr;
	String     bcity;
	String     bphone;
	Statement  stmt;
	ResultSet  rs;
	   
	try
	{Connection con = DbConnection.getJDBCConnection();
	  stmt = con.createStatement();

	  rs = executeQuery("SELECT * FROM branch");

	  // get info on ResultSet
	  ResultSetMetaData rsmd = rs.getMetaData();

	  // get number of columns
	  int numCols = rsmd.getColumnCount();

	  System.out.println(" ");
	  
	  // display column names;
	  for (int i = 0; i < numCols; i++)
	  {
	      // get column name and print it

	      System.out.printf("%-15s", rsmd.getColumnName(i+1));    
	  }

	  System.out.println(" ");

	  while(rs.next())
	  {
	      // for display purposes get everything from Oracle 
	      // as a string

	      // simplified output formatting; truncation may occur

	      bid = rs.getString("branch_id");
	      System.out.printf("%-10.10s", bid);

	      bname = rs.getString("branch_name");
	      System.out.printf("%-20.20s", bname);

	      baddr = rs.getString("branch_addr");
	      if (rs.wasNull())
	      {
	    	  System.out.printf("%-20.20s", " ");
              }
	      else
	      {
	    	  System.out.printf("%-20.20s", baddr);
	      }

	      bcity = rs.getString("branch_city");
	      System.out.printf("%-15.15s", bcity);

	      bphone = rs.getString("branch_phone");
	      if (rs.wasNull())
	      {
	    	  System.out.printf("%-15.15s\n", " ");
              }
	      else
	      {
	    	  System.out.printf("%-15.15s\n", bphone);
	      }      
	  }
 
	  // close the statement; 
	  // the ResultSet will also be closed
	  stmt.close();
	}
	catch (SQLException ex)
	{
	    System.out.println("Message: " + ex.getMessage());
	}	
    }
}

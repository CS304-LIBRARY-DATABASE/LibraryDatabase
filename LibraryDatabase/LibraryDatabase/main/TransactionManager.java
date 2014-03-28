package main;

import java.sql.Connection;
import java.sql.Date;
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


public class TransactionManager {
	
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
		  
		  // get info on ResultSet
		  ResultSetMetaData rsmd = rs.getMetaData();
		  int numCols = rsmd.getColumnCount();
		  
		  int [] columnTypes = new int[numCols];
		  result += "| ";
		  // get column names;
		  for (int i = 0; i < numCols; i++) {
		      result += rsmd.getColumnName(i+1) + " | ";
		      columnTypes[i] = rsmd.getColumnType(i+1);
		  }
		  result = result.trim() + "\n";
		  
		  while(rs.next()) {
			  result += "| ";
			  for (int i = 0; i < numCols; i++) {
				  switch (columnTypes[i]) {
					  case Types.VARCHAR:
					  case Types.CHAR:
						  result += rs.getString(i+1) + " | ";
						  break;
					  case Types.INTEGER:
					  case Types.NUMERIC:
						  result += rs.getInt(i+1) + " | ";
						  break;
					  case Types.FLOAT:
					  case Types.DOUBLE:
						  result += rs.getDouble(i+1) + " | ";
						  break;
					  case Types.DATE:
						  result += rs.getDate(i+1) + " | ";
						  break;
					  default:
						  throw new TransactionException("Error: unexpected type " +
								  columnTypes[i] + " encountered in"
						  		+ " TransactionManager.listTableConents()");
				  }
			  }
			  result = result.trim() + "\n";
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
		  
		  //stmt = con.createStatement();
		  
		  // TODO MAKE THIS QUERY WORK
		  PreparedStatement ps = con.prepareStatement("SELECT copyNo FROM BookCopy b "
			  		+ "where b.callNumber = ? and b.status = ?");
		  ps.setString(1, callNumber);
		  ps.setString(2, "in");
		  
		  String result = null;
		  rs = ps.executeQuery();
//		  rs = executeQuery("SELECT copyNo FROM BookCopy b "
//		  		+ "where b.callNumber = '" + callNumber + "' and b.status = 'in'", stmt);
		  if (rs.next()) {
			  // FIXME control flow never reached this statement!
			  result = rs.getString(1);
		  }
		  ps.close();
		  rs.close();
		  //stmt.close();
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


	public static void addNewBookCopy(String[] fields) throws TransactionException {
		String callNumber = fields[0].trim();
		
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
				String copyNo = "C" + num;
				
				rs.close();
				stmt.close();
				
				ps = con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");
				
				ps.setString(1, callNumber);
				ps.setString(2, copyNo);
				ps.setString(3, "in");
				
				executeUpdate(ps, con);
			} else {
				throw new TransactionException("Error: no previous BookCopies found "
						+ "with callNumber: " + callNumber);
			}
		} catch (SQLException e) {
			System.out.println("addBorrower Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}


	public static void payFine(String bid) throws TransactionException {
		PreparedStatement ps = null;
		Connection con = DbConnection.getJDBCConnection();
		try {
			ps = con.prepareStatement("Update Fine set paidDate = ? where fid in (select fid "
					+ "from Borrowing natural join Fine "
					+ "where bid = '" + bid + "' and paidDate is null)");
			
			java.util.Date today = new java.util.Date();
			ps.setDate(1, new Date(today.getYear(), today.getMonth(), today.getDay()));
			
			executeUpdate(ps, con);
		
		} catch (SQLException e) {
			System.out.println("payFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}
	
	
	public static boolean hasFines(String bid) throws TransactionException {
		Connection con = DbConnection.getJDBCConnection();
		try {
			Statement  stmt = con.createStatement();
			
			ResultSet rs = executeQuery("select callnumber, copyNo, outDate, inDate, "
					+ "fid, amount "
					+ "from Borrowing natural join Fine "
					+ "where bid = '" + bid + "' and paidDate is null", stmt);
			
			boolean finesExist = false;
			if (rs.next()) {
				finesExist = true;
			}
			
			rs.close();
			stmt.close();
			return finesExist;
			
		} catch (SQLException e) {
			System.out.println("checkFine Error: " + e.getMessage());
			throw new TransactionException("Error: " + e.getMessage());
		}
	}
}

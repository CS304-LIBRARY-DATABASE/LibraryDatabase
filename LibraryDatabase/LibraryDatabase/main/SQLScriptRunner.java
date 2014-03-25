package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class SQLScriptRunner {
	
	public static void runSQLScript(Connection connection) {
		String dir = System.getProperty("user.dir");
		dir = dir.split("main")[0];
		dir += "SQLTableDefinitions/";
		
		String file = dir + "libraryTableDefs.sql";
				
		importSQL(connection, file);
	}
	
	private static void importSQL(Connection conn, String SQLfileName) {
		Scanner s = null;
		try {
			s = new Scanner(new File(SQLfileName));
		} catch (FileNotFoundException e) {
			System.out.println("Could not find SQL file: " + SQLfileName);
			e.printStackTrace();
		}
		s.useDelimiter("(;(\r)?\n)|(--\n)");
		Statement st = null;
		try {
			st = conn.createStatement();
			while (s.hasNext()) {
				String line = s.next();
				if (line.startsWith("/*!") && line.endsWith("*/")) {
					int i = line.indexOf(' ');
					line = line.substring(i + 1, line.length() - " */".length());
				}

				if (line.trim().length() > 0) {
					st.execute(line);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

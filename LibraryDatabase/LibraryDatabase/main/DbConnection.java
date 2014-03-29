package main;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class DbConnection implements ActionListener {
	
	private static final String DB_CONNECT_URL = "jdbc:oracle:thin:@dbhost.ugrad.cs.ubc.ca:1522:ug"; 

		
	private static DbConnection dbc = null;
	private static Connection con;
	
	// user is allowed 3 login attempts
    private int loginAttempts = 0;

    // components of the login window
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JFrame mainFrame;
	
	// constructor
	private DbConnection() {
		mainFrame = new JFrame("User Login");

	      JLabel usernameLabel = new JLabel("Enter username: ");
	      JLabel passwordLabel = new JLabel("Enter password: ");

	      usernameField = new JTextField(10);
	      passwordField = new JPasswordField(10);
	      passwordField.setEchoChar('*');
	      
	      
	      //TODO: remove before demo
	      usernameField.setText("ora_x7b8");
	      passwordField.setText("a26747113");


	      JButton loginButton = new JButton("Log In");

	      JPanel contentPane = new JPanel();
	      mainFrame.setContentPane(contentPane);


	      // layout components using the GridBag layout manager

	      GridBagLayout gb = new GridBagLayout();
	      GridBagConstraints c = new GridBagConstraints();

	      contentPane.setLayout(gb);
	      contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	      // place the username label 
	      c.gridwidth = GridBagConstraints.RELATIVE;
	      c.insets = new Insets(10, 10, 5, 0);
	      gb.setConstraints(usernameLabel, c);
	      contentPane.add(usernameLabel);

	      // place the text field for the username 
	      c.gridwidth = GridBagConstraints.REMAINDER;
	      c.insets = new Insets(10, 0, 5, 10);
	      gb.setConstraints(usernameField, c);
	      contentPane.add(usernameField);

	      // place password label
	      c.gridwidth = GridBagConstraints.RELATIVE;
	      c.insets = new Insets(0, 10, 10, 0);
	      gb.setConstraints(passwordLabel, c);
	      contentPane.add(passwordLabel);

	      // place the password field 
	      c.gridwidth = GridBagConstraints.REMAINDER;
	      c.insets = new Insets(0, 0, 10, 10);
	      gb.setConstraints(passwordField, c);
	      contentPane.add(passwordField);

	      // place the login button
	      c.gridwidth = GridBagConstraints.REMAINDER;
	      c.insets = new Insets(5, 10, 10, 10);
	      c.anchor = GridBagConstraints.CENTER;
	      gb.setConstraints(loginButton, c);
	      contentPane.add(loginButton);

	      // register password field and OK button with action event handler
	      passwordField.addActionListener(this);
	      loginButton.addActionListener(this);

	      // anonymous inner class for closing the window
	      mainFrame.addWindowListener(new WindowAdapter() 
	      {
		public void windowClosing(WindowEvent e) 
		{ 
		  System.exit(0); 
		}
	      });

	      // size the window to obtain a best fit for the components
	      mainFrame.pack();

	      // center the frame
	      Dimension d = mainFrame.getToolkit().getScreenSize();
	      Rectangle r = mainFrame.getBounds();
	      mainFrame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

	      // make the window visible
	      mainFrame.setVisible(true);

	      // place the cursor in the text field for the username
	      usernameField.requestFocus();

	      try 
	      {
		// Load the Oracle JDBC driver
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	      }
	      catch (SQLException ex)
	      {
		System.out.println("Message: " + ex.getMessage());
		System.exit(-1);
	      }
	}
	
	public static DbConnection getInstance() {
		if (dbc == null) {
			dbc = new DbConnection();
		}
		return dbc;
	}
	
		
		
		/*
	     * connects to Oracle database named ug using user supplied username and password
	     */ 
	    private boolean connect(String username, String password) {
	      try {
			con = DriverManager.getConnection(DB_CONNECT_URL,username,password);
		
			System.out.println("\nConnected to Oracle!");
			return true;
	      }
	      catch (SQLException ex)
	      {
			System.out.println("Message: " + ex.getMessage());
			return false;
	      }
	    }
	    
	    /*
	     * event handler for login window
	     */ 
	    public void actionPerformed(ActionEvent e) {
	    	if ( connect(usernameField.getText(), String.valueOf(passwordField.getPassword())) ) {
				  // if the username and password are valid, 
				  // remove the login window and display a text menu 
				  mainFrame.dispose();
				  
				  // load the main application
			      Main.init();
			      
				} else {
				  loginAttempts++;
				  
				  if (loginAttempts >= 3) {
				      mainFrame.dispose();
				      System.exit(-1);
				  }
				  else {
				      // clear the password
				      passwordField.setText("");
				  }
				}    
	    }

	    
		public static Connection getJDBCConnection() {
			return con;
		}
}

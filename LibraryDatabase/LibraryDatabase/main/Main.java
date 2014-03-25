package main;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;


@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener{

	private static Main app = new Main();


	public static void main(String[] args)
	{
		// make a connection to the database
		// if successful it will call init() below
		//DbConnection dbc = DbConnection.getInstance();

		init();
	}

	public static void init() {
		// Specify where will it appear on the screen:
		app.setLocation(100, 100);

		// Show it!
		app.setVisible
		(true);

		BorderLayout layout = new BorderLayout();

		app.setLayout(layout);

		app.add(clerkInterface(), BorderLayout.WEST);
		app.add(borrowerInterface(), BorderLayout.CENTER);
		app.add(librarianInterface(), BorderLayout.EAST);
		app.add(generalInterface(), BorderLayout.SOUTH);

		


		app.setSize(700, 600);
		app.repaint();
	}


	private static final String ADD_BORROWER_NAME = "Add Borrower";
	private static final String CHECK_OUT_NAME = "Check Out Items";
	private static final String RETURN_ITEM_NAME = "Return Item";
	private static final String CHECK_OVERDUE_NAME = "Check Overdue Items";


	private static JPanel clerkInterface(){
		JPanel panel = new JPanel();

		TitledBorder title;
		title = BorderFactory.createTitledBorder("Clerk Options");
		panel.setBorder(title);

		BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);

		panel.setLayout(layout);

		Button addNewBorrower = new Button(ADD_BORROWER_NAME);
		addNewBorrower.addActionListener(app);
		panel.add(addNewBorrower);

		Button checkOut = new Button(CHECK_OUT_NAME);
		checkOut.addActionListener(app);
		panel.add(checkOut);

		Button returnItem = new Button(RETURN_ITEM_NAME);
		returnItem.addActionListener(app);
		panel.add(returnItem);

		Button checkOverdue = new Button(CHECK_OVERDUE_NAME);
		checkOverdue.addActionListener(app);
		panel.add(checkOverdue);

		return panel;
	}

	private static final String SEARCH_NAME = "Search";
	private static final String CHECK_ACCOUNT_NAME = "Check Account";
	private static final String HOLD_REQUEST_NAME = "Make Hold Request";
	private static final String PAY_FINE_NAME = "Pay Fine";

	private static JPanel borrowerInterface(){
		JPanel panel = new JPanel();

		TitledBorder title;
		title = BorderFactory.createTitledBorder("Borrower Options");
		panel.setBorder(title);

		BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);

		panel.setLayout(layout);

		Button search = new Button(SEARCH_NAME);
		search.addActionListener(app);
		panel.add(search);

		Button checkAccount = new Button(CHECK_ACCOUNT_NAME);
		checkAccount.addActionListener(app);
		panel.add(checkAccount);

		Button holdRequest = new Button(HOLD_REQUEST_NAME);
		holdRequest.addActionListener(app);
		panel.add(holdRequest);

		Button payFine = new Button(PAY_FINE_NAME);
		payFine.addActionListener(app);
		panel.add(payFine);

		return panel;
	}
	
	private static final String ADD_BOOK_NAME = "Add Book";
	private static final String CHECKOUT_REPORT_NAME = "Checkout Report";
	private static final String POPULAR_BOOK_NAME = "Popular Book Report";
 

	private static JPanel librarianInterface(){
		JPanel panel = new JPanel();

		TitledBorder title;
		title = BorderFactory.createTitledBorder("Librarian Options");
		panel.setBorder(title);

		BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);

		panel.setLayout(layout);

		Button addBook = new Button(ADD_BOOK_NAME);
		addBook.addActionListener(app);
		panel.add(addBook);
		
		Button checkoutReport = new Button(CHECKOUT_REPORT_NAME);
		checkoutReport.addActionListener(app);
		panel.add(checkoutReport);
		
		Button popularBooks = new Button(POPULAR_BOOK_NAME);
		popularBooks.addActionListener(app);
		panel.add(popularBooks);

		return panel;
	}

	private static JPanel generalInterface(){
		JPanel panel = new JPanel();

		TitledBorder title;
		title = BorderFactory.createTitledBorder("Output");
		panel.setBorder(title);

		JTextArea box = new JTextArea();
		JScrollPane scroll = new JScrollPane(box);

		box.setSize(100, 300);
		box.setColumns(60);
		box.setRows(16);
		box.setAutoscrolls(true);
		box.setEditable(false);

		panel.add(scroll);

		return panel;		
	}



	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand() == ADD_BORROWER_NAME)
			addBorrower();

		if(e.getActionCommand() == CHECK_OUT_NAME)
			checkOut();

		if(e.getActionCommand() == RETURN_ITEM_NAME)
			returnItem();

		if(e.getActionCommand() == CHECK_OVERDUE_NAME)
			checkOverdue();

		if(e.getActionCommand() == SEARCH_NAME)
			search();

		if(e.getActionCommand() == CHECK_ACCOUNT_NAME)
			checkAccount();

		if(e.getActionCommand() == HOLD_REQUEST_NAME)
			holdRequest();

		if(e.getActionCommand() == PAY_FINE_NAME)
			payFine();
		
		if(e.getActionCommand() == ADD_BOOK_NAME)
			addBook();
		
		if(e.getActionCommand() == CHECKOUT_REPORT_NAME)
			checkoutReport();
		
		if(e.getActionCommand() == POPULAR_BOOK_NAME)
			popularBooks();
		
	}

	/* 
	 * Add a new borrower to the library. The user should provide all the required information
	 */
	private void addBorrower() {
		//Borrower (bid, password, name, address, phone, emailAddress, sinOrStNo, expiryDate, type)
		String bid;
		String password;
		String name;
		String address;
		String phone;
		String emailAddress;
		String sinOrStNo;
		String expiryDate;
		String type;

		String[] properties = {"ID", "Password", "Name", "Address", "Phone", "Email", "SIN/S.Num", "Expiry", "Type"};

		properties = createInputPopup(properties, "Add New Borrower");

		bid = properties[0];
		password = properties[1];
		name = properties[2];
		address = properties[3];
		phone = properties[4];
		emailAddress = properties[5];
		sinOrStNo = properties[6];
		expiryDate = properties[7];
		type = properties[8];


		JOptionPane.showMessageDialog(null, "ERROR MESSAGE", "Error", JOptionPane.ERROR_MESSAGE);


		//TODO: ERIC verify properties are valid
		//TODO: add borrower with given properties
	}

	/*
	Check-out items borrowed by a borrower. To borrow items, borrowers provide their card
	number and a list with the call numbers of the items they want to check out. The system
	determines if the borrower's account is valid and if the library items are available for
	borrowing. Then it creates one or more borrowing records and prints a note with the
	items and their due day (which is given to the borrower).
	 */
	private void checkOut() {
		// Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)
		String[] borid;
		String bid;
		String[] callNumber;
		String[] copyNo;
		String outDate;
		String inDate;


		int n = 0;

		while(true){
			String numItems = JOptionPane.showInputDialog("How many items are being checked out?");

			if(numItems == null)
				break;

			try{
				n = Integer.valueOf(numItems);

				if(n < 1){
					JOptionPane.showMessageDialog(null, "Please enter a value greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
					continue;
				}


				if(n > 10){
					JOptionPane.showMessageDialog(null, "Please enter only 10 at a time.", "Error", JOptionPane.ERROR_MESSAGE);
					n = 10;
				}

				break;
			}
			catch (Exception e){
				JOptionPane.showMessageDialog(null, "Please enter numerical value.", "Invalid input", JOptionPane.ERROR_MESSAGE);
			}
		}


		if(n > 0){
			String[] properties = new String[1 + n];
			properties[0] = "Borrower ID";

			for(int i = 1; i <= n; i++)
				properties[i] = "Callnumber #" + String.valueOf(i);

			properties = createInputPopup(properties, "Check out " + String.valueOf(n) + " books");

			borid = new String[n];
			bid =  properties[0];
			callNumber = new String[n];
			copyNo = new String[n];
			//outDate = ;
			//inDate = ;

			//TODO: verify correct input
			//TODO: get remaining tuple values
			//TODO: add to database
			//TODO: output result

		}

	}

	/*
	 * Processes a return. When an item is returned, the clerk records the
	 * return by providing the item's catalogue number. The system determines
	 * the borrower who had borrowed the item and records that the item is
	 * "in". If the item is overdue, a fine is assessed for the borrower.
	 * If there is a hold request for this item by another borrower, the
	 * item is registered as "on hold" and a message is send to the borrower
	 * who made the hold request.
	 */
	private void returnItem() {
		//Fine (fid, amount, issuedDate, paidDate, borid)
		//BookCopy (callNumber, copyNo, status)
		//HoldRequest(hid, bid, callNumber, issuedDate)
		//Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)


		String[] callNumber = {"Call Number"};
		callNumber = createInputPopup(callNumber, "Return a book");

		// TODO: determine borrower
		// TODO: check if borrower has outstanding fine, change afterwards
		// TODO: check if there is hold request for book
		// TODO: update all tuples

	}

	/*
	 * Checks overdue items. The system displays a list of the items that are overdue and
	 * the borrowers who have checked them out. The clerk may decide to send an email messages
	 * to any of them (or to all of them).
	 */
	private void checkOverdue() {
		// TODO everything

	}

	/*
	 * Search for books using keyword search on titles, authors and subjects. The result is a 
	 * list of books that match the search together with the number of copies that are in and out.
	 */
	private void search() {
		// TODO everything

	}

	/*
	 * Check his/her account. The system will display the items the borrower has currently borrowed and
	 * not yet returned, any outstanding fines and the hold requests that have been placed by the borrower.
	 */
	private void checkAccount() {
		// TODO everything

	}

	/*
	 * Place a hold request for a book that is out. When the item is returned, the system sends an email
	 * to the borrower and informs the library clerk to keep the book out of the shelves.
	 */
	private void holdRequest() {
		// TODO everything

	}

	/*
	 * Pay a fine.
	 */
	private void payFine() {
		// TODO Everything

	}
	
	/*
	 * Adds a new book or new copy of an existing book to the library. The librarian provides the
	 * information for the new book, and the system adds it to the library.
	 */
	private void addBook() {
		// TODO everything
		
	}

	/*
	 * Generate a report with all the books that have been checked out. For each book the report
	 * shows the date it was checked out and the due date. The system flags the items that are overdue.
	 * The items are ordered by the book call number. If a subject is provided the report lists only
	 * books related to that subject, otherwise all the books that are out are listed by the report.
	 */
	private void checkoutReport() {
		// TODO everything
		
	}

	/*
	 * Generate a report with the most popular items in a given year. The librarian provides a year and
	 * a number n. The system lists out the top n books that where borrowed the most times during that year.
	 * The books are ordered by the number of times they were borrowed.
	 */
	private void popularBooks() {
		// TODO everything
		
	}


	@SuppressWarnings("static-access")
	private String[] createInputPopup(String[] labels, String title){

		int n = labels.length;
		Object[] message = new Object[n];
		JPanel p = new JPanel(new SpringLayout());
		JTextField[] fields = new JTextField[n];

		for(int i = 0; i <= n - 1; i++){

			p.add(new JLabel(labels[i], JLabel.TRAILING));
			JTextField text = new JTextField(20);
			fields[i] = text;
			p.add(text);

			message[i] = p;
		}

		//Lay out the panel.
		SpringUtilities.makeCompactGrid(p,
				n, 2,        //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		final JOptionPane window = new JOptionPane();

		window.showOptionDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null); 

		String[] input = new String[n];

		for(int i = 0; i <= n - 1; i++){
			input[i] = fields[i].getText();
		}

		return input;
	}

}
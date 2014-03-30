package main;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;


@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener{

	private static Main app = new Main();

	private static JTextArea box;

	private static final String ADD_BORROWER_NAME = "Add Borrower";
	private static final String CHECK_OUT_NAME = "Check Out Items";
	private static final String RETURN_ITEM_NAME = "Return Item";
	private static final String CHECK_OVERDUE_NAME = "Check Overdue Items";
	private static final String LIST_TABLE_CONTENTS = "List Table Contents";
	private static final String EXECUTE_QUERY = "Execute Query";

	public static void main(String[] args)
	{
		// make a connection to the database
		// if successful it will call init() below
		DbConnection.getInstance();
	}

	public static void init() {
		app.setLocation(100, 100);
		app.setSize(700, 310);

		app.setVisible
		(true);

		BorderLayout layout = new BorderLayout();

		app.setLayout(layout);

		app.add(clerkInterface(), BorderLayout.WEST);
		app.add(borrowerInterface(), BorderLayout.CENTER);
		app.add(librarianInterface(), BorderLayout.EAST);
		app.add(generalInterface(), BorderLayout.SOUTH);

		WindowListener exitListener = new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				/*
				  int confirm = JOptionPane.showOptionDialog(null,
						"Are you sure you want to close the application?",
						"Exit Confirmation", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				 */

				System.exit(0);

			}

			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
		};

		app.addWindowListener(exitListener);


		app.setSize(700, 600);
		app.repaint();
	}


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

		Button listTableContents = new Button(LIST_TABLE_CONTENTS);
		listTableContents.addActionListener(app);
		panel.add(listTableContents);

		Button queryButton = new Button(EXECUTE_QUERY);
		queryButton.addActionListener(app);
		panel.add(queryButton);

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

		box = new JTextArea();
		JScrollPane scroll = new JScrollPane(box);

		box.setSize(100, 300);
		box.setColumns(60);
		box.setRows(16);
		box.setAutoscrolls(true);
		box.setEditable(false);

		panel.add(scroll);

		return panel;		
	}

	/**
	 * Dump all entries of given table to text box
	 */
	private void listTableContents() {
		try {
			String tableName = JOptionPane.showInputDialog("Table Name:");
			if(tableName == null || tableName.trim().isEmpty())
				return;
			String result = TransactionManager.listTableConents(tableName);

			writeToOutputBox(result);
		} catch (TransactionException e) {
			makeErrorAlert(e.getMessage());
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand() == ADD_BORROWER_NAME)
			addBorrower();

		if(e.getActionCommand() == LIST_TABLE_CONTENTS)
			listTableContents();

		if(e.getActionCommand() == EXECUTE_QUERY)
			executeQuery();

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


	private void executeQuery() {
		String[] properties = new String[1];
		properties[0] = "Raw Query String:";

		String [] memory = null;
		while (true) {
			memory = createInputPopup(properties, "Raw Query String:", memory);
			if (memory == null)
				return;

			String query = memory[0].trim();
			if (!query.isEmpty()) {
				try {
					TransactionManager.executeQuery(query);
					makeSuccessAlert("Query successful");
					break;
				} catch (TransactionException e) {
					makeErrorAlert(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/* 
	 * Add a new borrower to the library. The user should provide all the required information
	 */
	private void addBorrower() {
		//Borrower (bid, password, name, address, phone, emailAddress, sinOrStNo, expiryDate, type)
		String password;
		String name;
		String address;
		String phone = null;
		String emailAddress;
		String sinOrStNo;
		String expiryDate;
		String type;

		String memory[] = null;
		String[] properties = {"Password", "Name", "Address", "Phone", "Email",
				"SIN/S.Num", "Expiry (DD/MM/YYYY)", "Type"};

		while (true) {
			memory = createInputPopup(properties, "Add new borrower", memory);

			if (memory == null)
				return;

			password = memory[0];
			name = memory[1];
			address = memory[2];
			phone = memory[3];
			emailAddress = memory[4];
			sinOrStNo = memory[5];
			expiryDate = memory[6];
			type = memory[7];

			if (VerifyAttributes.verifyPassword(password) != null) {
				makeErrorAlert(VerifyAttributes.verifyPassword(password));
			} 
			else if (VerifyAttributes.verifyBorrowerName(name) != null) {
				makeErrorAlert(VerifyAttributes.verifyBorrowerName(name));
			} 
			else if (VerifyAttributes.verifyAddress(address) != null) {
				makeErrorAlert(VerifyAttributes.verifyAddress(address));
			} 
			else if (VerifyAttributes.verifyPhone(phone) != null) {
				makeErrorAlert(VerifyAttributes.verifyPhone(phone));
			} 
			else if (VerifyAttributes.verifyEmail(emailAddress) != null) {
				makeErrorAlert(VerifyAttributes.verifyEmail(emailAddress));
			} 
			else if (VerifyAttributes.verifySinOrStNo(sinOrStNo) != null) {
				makeErrorAlert(VerifyAttributes.verifySinOrStNo(sinOrStNo));
			}
			else if (VerifyAttributes.verifyDate(expiryDate) != null) {
				makeErrorAlert(VerifyAttributes.verifyDate(expiryDate));
			} 
			else if (VerifyAttributes.verifyType(type) != null) {
				makeErrorAlert(VerifyAttributes.verifyType(type));
			} 
			else {
				// add borrower with given properties

				try{
					TransactionManager.addBorrower(memory);
					makeSuccessAlert("Borrower successfully added");
				}
				catch(TransactionException e){
					makeErrorAlert(e.getMessage());
					e.printStackTrace();
				}

				break;
			}
		}
	}

	/**
	Check-out items borrowed by a borrower. To borrow items, borrowers provide their card
	number and a list with the call numbers of the items they want to check out. The system
	determines if the borrower's account is valid and if the library items are available for
	borrowing. Then it creates one or more borrowing records and prints a note with the
	items and their due day (which is given to the borrower).
	 */
	private void checkOut() {
		int n = 0;
		while(true){
			String numItems = JOptionPane.showInputDialog("How many items are being checked out?");
			if(numItems == null)
				return;
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

		String[] properties = new String[1 + n];
		properties[0] = "Borrower ID:";

		for(int i = 1; i <= n; i++) {
			properties[i] = "Callnumber #" + String.valueOf(i);
		}

		String [] memory = null;
		while (true) {
			memory = createInputPopup(properties, "Check out " + String.valueOf(n) + " books", memory);

			if (memory == null)
				return;

			// verify borrower
			String bid =  memory[0];
			if (VerifyAttributes.verifyBID(bid) != null) {
				makeErrorAlert(VerifyAttributes.verifyBID(bid));
			}
			else {
				// verify callnumbers
				boolean valid = true;
				for(int i = 1; i <= n; i++) {
					if (VerifyAttributes.verifyCallNumber(memory[i]) != null) {
						makeErrorAlert(VerifyAttributes.verifyCallNumber(memory[i]));
						valid = false;
						break;
					}
				}
				if (valid) {
					// checkout books
					TransactionHelper.checkout(memory, bid);
					break;
				}
			}
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
		callNumber = createInputPopup(callNumber, "Return a book", null);

		// TODO: determine borrower
		// TODO: check if borrower has outstanding fine, change afterwards
		// TODO: check if there is hold request for book
		// TODO: update all tuples
		
		// TODO: 	
		/*
		 * Place a hold request for a book that is out. When the item is returned, the system sends an email
		 * to the borrower and informs the library clerk to keep the book out of the shelves.
		 */

	}

	/*
	 * Checks overdue items. The system displays a list of the items that are overdue and
	 * the borrowers who have checked them out. The clerk may decide to send an email messages
	 * to any of them (or to all of them).
	 */
	private void checkOverdue() {
		//Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)

		ArrayList<String> result;
		
		try {
			result = TransactionManager.checkForOverdueBooks();
			writeToOutputBox(result.get(0));
			
			
			int q = JOptionPane.showOptionDialog(null, "Would you like to send out emails?", 
					"Send emails", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			result.remove(0);
			if(!result.isEmpty() && q == 0)
				OverdueReportFrame.open(result);

		} catch (TransactionException e) {
			makeErrorAlert("Problem encountered, transaction aborted");
		}
		

		
		// TODO email borrowers

	}

	/*
	 * Search for books using keyword search on titles, authors and subjects. The result is a 
	 * list of books that match the search together with the number of copies that are in and out.
	 */
	@SuppressWarnings("static-access")
	private void search() {
		//Book (callNumber, isbn, title, mainAuthor, publisher, year )
		//HasAuthor (callNumber, name)
		//HasSubject (callNumber, subject)
		//BookCopy (callNumber, copyNo, status)

		JRadioButton titleRB = new JRadioButton("Search by Title");
		JRadioButton authorRB = new JRadioButton("Search by Author");
		JRadioButton subjectRB = new JRadioButton("Search by Subject");
		ButtonGroup bg = new ButtonGroup();
		bg.add(titleRB);
		bg.add(authorRB);
		bg.add(subjectRB);

		JPanel p = new JPanel(new SpringLayout());
		p.add(titleRB);
		p.add(authorRB);
		p.add(subjectRB);

		//Lay out the panel.
		SpringUtilities.makeCompactGrid(p,
				3, 1,        //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		final JOptionPane window = new JOptionPane();

		while(true) {
			int result = window.showOptionDialog(null, p, "", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, null, null);

			if(result != 0)
				return;

			String[] searchKey = new String[1];
			if (titleRB.isSelected()) {
				searchKey[0] = "Title";
			} else if (authorRB.isSelected()) {
				searchKey[0] = "Author";
			} else if (subjectRB.isSelected()) {
				searchKey[0] = "Subject";
			} else {
				continue;
			}
			searchBy(searchKey);
			break;
		}
	}

	private void searchBy(String [] searchKey) {
		String searchBy = searchKey[0];
		String [] memory = null;
		while (true) {
			memory = createInputPopup(searchKey, "Search by " + searchBy, memory);
			if(memory == null)
				break;

			String search = memory[0];

			if(search.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please enter a search key", "No input", JOptionPane.ERROR_MESSAGE);
			}
			else {
				// search/filter for relevant books, and display result
				try {
					String result = TransactionHelper.searchBy(searchBy, search.trim());
					writeToOutputBox(result);
					break;
				} catch (TransactionException e) {
					makeErrorAlert(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Check his/her account. The system will display the items the borrower has currently borrowed and
	 * not yet returned, any outstanding fines and the hold requests that have been placed by the borrower.
	 */
	private void checkAccount() {
		//Borrower (bid, password, name, address, phone, emailAddress, sinOrStNo, expiryDate, type)

		String bid;

		String[] getInfo = {"Borrower ID"};
		getInfo = createInputPopup(getInfo, "Validation", null);

		if(getInfo == null)
			return;

		bid = getInfo[0];

		//TODO: check that Borrower ID is good
		//TODO: query for borrowed tuples
		//TODO: query for fines
		//TODO: query for hold requests
		//TODO: display



	}

	/*
	 * Place a hold request for a book that is out. When the item is returned, the system sends an email
	 * to the borrower and informs the library clerk to keep the book out of the shelves.
	 */
	private void holdRequest() {
		//HoldRequest(hid, bid, callNumber, issuedDate)

		String bid;
		String callNumber;

		String[] input = {"Borrower ID", "Call #"};
		input = createInputPopup(input, "Hold request", null);

		if(input == null)
			return;

		bid = input[0];
		callNumber = input[1];

		String result = TransactionManager.holdRequest(bid, callNumber);
		
		if(result == null)
			makeSuccessAlert("Hold request successful.");
		else
			makeErrorAlert(result);
	}

	/*
	 * Pay a fine.
	 */
	private void payFine() {
		//Borrower (bid, password, name, address, phone, emailAddress, sinOrStNo, expiryDate, type)

		String bid;

		String[] getInfo = {"Borrower ID:"};
		String [] memory = null;
		while (true) {
			memory = createInputPopup(getInfo, "Validation", memory);
			if(memory == null)
				return;

			bid = memory[0];
			if (VerifyAttributes.verifyBID(bid) != null) {
				makeErrorAlert(VerifyAttributes.verifyBID(bid));
			} else {
				// update unpaid fine tuples
				try {
					if (TransactionManager.hasFines(bid)) {
						TransactionHelper.payFine(bid);
						makeSuccessAlert("Fine successfully payed");
					} else {
						makeSuccessAlert("Borrower with ID " + bid + " "
								+ "has no outstanding fines");
					}
				} catch (TransactionException e) {
					makeErrorAlert(e.getMessage());
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/*
	 * Adds a new book or new copy of an existing book to the library. The librarian provides the
	 * information for the new book, and the system adds it to the library.
	 */
	private void addBook() {
		//Book (callNumber, isbn, title, mainAuthor, publisher, year)
		//BookCopy (callNumber, copyNo, status)
		//HasAuthor (callNumber, name)
		//HasSubject (callNumber, subject)

		String callNumber;
		String isbn;
		String title;
		String mainAuthor;
		String publisher;
		String year;

		String[] properties = {"Call Number *", "ISBN *", "Title *", "Main Author *", "Publisher *", "Year *",
				"Additional Author(s) (Comma Separated)", "Subject(s) (Comma Separated)"};
		String [] memory = null;

		while (true) {
			memory = createInputPopup(properties, "Add new book", memory);

			if (memory == null) {
				return;
			}

			callNumber = memory[0];
			isbn = memory[1];
			title = memory[2];
			mainAuthor = memory[3];
			publisher = memory[4];
			year = memory[5];
			String additionalAuthors = memory[6];
			String subjects = memory[7];

			if (VerifyAttributes.verifyCallNumber(callNumber) != null) {
				makeErrorAlert(VerifyAttributes.verifyCallNumber(callNumber));
			} else if (VerifyAttributes.verifyISBN(isbn) != null) {
				makeErrorAlert(VerifyAttributes.verifyISBN(isbn));
			} else if (VerifyAttributes.verifyTitle(title) != null) {
				makeErrorAlert(VerifyAttributes.verifyTitle(title));
			} else if (VerifyAttributes.verifyAuthor(mainAuthor) != null) {
				makeErrorAlert(VerifyAttributes.verifyAuthor(mainAuthor));
			} else if (VerifyAttributes.verifyPublisher(publisher) != null) {
				makeErrorAlert(VerifyAttributes.verifyPublisher(publisher));
			} else if (VerifyAttributes.verifyYear(year, 2) != null) {
				makeErrorAlert(VerifyAttributes.verifyYear(year, 2));
			} else {
				// Add book to the database
				try {
					TransactionHelper.addBook(memory);
					makeSuccessAlert("Book successfully added");
				} catch (TransactionException e) {
					makeSuccessAlert("Book was not successfully added");
					e.printStackTrace();
				}
				break;
			}
		}
	}

	/*
	 * Generate a report with all the books that have been checked out. For each book the report
	 * shows the date it was checked out and the due date. The system flags the items that are overdue.
	 * The items are ordered by the book call number. If a subject is provided the report lists only
	 * books related to that subject, otherwise all the books that are out are listed by the report.
	 */
	private void checkoutReport() {
		String[] response = {"Optional subject key"};
		response = createInputPopup(response, "Report for subject", null);

		if(response == null || response[0] == null)
			return;

		String result = "";
		
		try {
			result = TransactionManager.checkoutReport(response[0]);
		} catch (TransactionException e) {
			makeErrorAlert("Problem encountered, transaction aborted");
		}
		
		writeToOutputBox(result);
	}

	/*
	 * Generate a report with the most popular items in a given year. The librarian provides a year and
	 * a number n. The system lists out the top n books that where borrowed the most times during that year.
	 * The books are ordered by the number of times they were borrowed.
	 */
	private void popularBooks() {
		//Book (callNumber, isbn, title, mainAuthor, publisher, year)
		//Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)
		int year;
		int n;


		while(true){
			String[] response = {"Year", "# of Results"};
			response = createInputPopup(response, "Find popular items", null);

			if(response == null)
				return;

			try{
				year = Integer.valueOf(response[0]);

				if(year < 0){
					JOptionPane.showMessageDialog(null, "Please enter a positive year.", "Invalid input", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				n = Integer.valueOf(response[1]);

				if(n < 0){
					JOptionPane.showMessageDialog(null, "Please enter a positive value for results.", "Invalid input", JOptionPane.ERROR_MESSAGE);
					continue;
				}

				break;
			}
			catch (Exception e){
				JOptionPane.showMessageDialog(null, "Please enter numerical values.", "Invalid input", JOptionPane.ERROR_MESSAGE);
			}
		}

		//TODO: query for top books
		//TODO: display top books

	}


	@SuppressWarnings("static-access")
	private String[] createInputPopup(String[] labels, String title, String[] memory){

		int n = labels.length;
		Object[] message = new Object[n];
		JPanel p = new JPanel(new SpringLayout());
		JTextField[] fields = new JTextField[n];

		for(int i = 0; i <= n - 1; i++){

			p.add(new JLabel(labels[i], JLabel.TRAILING));
			JTextField text = new JTextField(20);
			fields[i] = text;

			if(memory != null)
				text.setText(memory[i]);

			p.add(text);

			message[i] = p;
		}

		//Lay out the panel.
		SpringUtilities.makeCompactGrid(p,
				n, 2,        //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		final JOptionPane window = new JOptionPane();

		int result = window.showOptionDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null);

		if(result != 0)
			return null;

		String[] input = new String[n];

		for(int i = 0; i <= n - 1; i++){
			input[i] = fields[i].getText();
		}

		return input;
	}

	/**
	 * Make a popup window with an error message
	 * @param message
	 */
	public static void makeErrorAlert(String message) {
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Make a popup window with an success message
	 * @param message
	 */
	public static void makeSuccessAlert(String message) {
		JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Write output string to output text box
	 * @param s
	 */
	public static void writeToOutputBox(String s) {
		box.setText(s);
	}
}
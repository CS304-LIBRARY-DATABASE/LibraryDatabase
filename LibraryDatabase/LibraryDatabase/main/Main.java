package main;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;


@SuppressWarnings("serial")
public class Main extends JFrame implements ActionListener{

	private static Main app = new Main();


	public static void main(String[] args)
	{
		// Specify where will it appear on the screen:
		app.setLocation(100, 100);
		app.setSize(300, 300);

		// Show it!
		app.setVisible
		(true);

		app.add(clerkInterface());	
	}


	private static String CHECK_OUT_NAME = "Check Out Items";
	private static String ADD_BORROWER_NAME = "Add Borrower";



	private static JPanel clerkInterface(){
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();

		panel.setLayout(layout);

		Button addNewBorrower = new Button(ADD_BORROWER_NAME);
		addNewBorrower.addActionListener(app);
		panel.add(addNewBorrower);

		Button checkOut = new Button(CHECK_OUT_NAME);
		checkOut.addActionListener(app);
		panel.add(checkOut);

		return panel;
	}



	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand() == ADD_BORROWER_NAME)
			addBorrower();

		if(e.getActionCommand() == CHECK_OUT_NAME)
			checkOut();
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

		//TODO: verify properties are valid
		//TODO: add borrower with given properties
	}
	
	/*
	Check-out items borrowed by a borrower. To borrow items, borrowers provide their card
	number and a list with the call numbers of the items they want to check out. The system
	determines if the borrower's account is valid and if the library items are available for
	borrowing. Then it creates one or more borrowing records and prints a note with the
	items and their due day (which is giver to the borrower).
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
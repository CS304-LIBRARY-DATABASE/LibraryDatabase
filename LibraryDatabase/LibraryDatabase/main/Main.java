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
	
	private void checkOut() {
		// Borrowing(borid, bid, callNumber, copyNo, outDate, inDate)
		String bid;
		String password;
		String name;
		String address;
		String phone;
		String emailAddress;
		String sinOrStNo;
		String expiryDate;
		String type;
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
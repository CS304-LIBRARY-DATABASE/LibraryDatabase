package main;

import java.awt.Button;
import java.awt.Component;
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



	private static JPanel clerkInterface(){
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();

		panel.setLayout(layout);

		Button addNewBorrower = new Button("Add Borrower");
		addNewBorrower.addActionListener(app);
		panel.add(addNewBorrower);

		return panel;
	}



	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand() == "Add Borrower")
			addBorrower();
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
		
		createInputPopup(properties, "Add New Borrower");

	}


	private void createInputPopup(String[] labels, String title){
		
		int n = labels.length;
		Object[] message = new Object[n];
		JPanel p = new JPanel(new SpringLayout());
		
		for(int i = 0; i <= n - 1; i++){
			
			p.add(new JLabel(labels[i], JLabel.TRAILING));
			p.add(new JTextField(20));
			
			message[i] = p;
		}
		
		//Lay out the panel.
		SpringUtilities.makeCompactGrid(p,
		                                n, 2,        //rows, cols
		                                6, 6,        //initX, initY
		                                6, 6);       //xPad, yPad
		
		JOptionPane.showOptionDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null); 

	}

}
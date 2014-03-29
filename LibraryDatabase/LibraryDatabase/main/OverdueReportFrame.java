package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class OverdueReportFrame extends JFrame {

	private static OverdueReportFrame frame = new OverdueReportFrame();
	private static ArrayList<JCheckBox> boxes;
	private static JTextField subject = new JTextField();
	private static JTextArea body = new JTextArea();		
	private static JButton send = new JButton();		



	public static void open(ArrayList<String> listOfEmails){

		boxes = new ArrayList<JCheckBox>();

		frame.setLocation(100, 100);
		frame.setSize(700, 400);

		frame.setVisible(true);		

		JPanel display = new JPanel();
		BoxLayout layout = new BoxLayout(display, BoxLayout.X_AXIS);
		display.setLayout(layout);

		display.add(makeEmailCheckList(listOfEmails));
		display.add(makeEmailDisplay());

		frame.add(display);

	}

	private static JScrollPane makeEmailCheckList(ArrayList<String> listOfEmails) {

		JPanel panel = new JPanel();
		panel.setSize(100, 100);
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);


		final JCheckBox masterBox = new JCheckBox("Select all/none");
		masterBox.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {

				if(masterBox.isSelected())
					changeAllValues(true);
				else
					changeAllValues(false);

			}
		});

		boxes.add(masterBox);
		panel.add(masterBox);

		for (int i = 0; i <= listOfEmails.size() - 1; i++){
			JCheckBox box = new JCheckBox(listOfEmails.get(i));
			boxes.add(box);
			panel.add(box);
		}

		JScrollPane scroll = new JScrollPane(panel);
		scroll.setMinimumSize(new Dimension(300, 0));

		return scroll;
	}

	private static JPanel makeEmailDisplay(){

		JPanel emailPanel = new JPanel();

		JPanel subjectPanel = new JPanel();
		BoxLayout subjectLayout = new BoxLayout(subjectPanel, BoxLayout.Y_AXIS);
		subjectPanel.setLayout(subjectLayout);
		JLabel subjectLabel = new JLabel("Subject");
		subject = new JTextField();

		subject.setColumns(40);

		subjectPanel.add(subjectLabel);
		subjectPanel.add(subject);

		body = new JTextArea();		
		JScrollPane scroll = new JScrollPane(body);

		body.setSize(100, 300);
		body.setColumns(40);
		body.setRows(16);
		body.setAutoscrolls(true);
		body.setEditable(true);
		body.setText("Email body goes here");

		final String name = "Send";
		send = new JButton(name);
		send.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals(name))
					try {
						sendEmail();
					} catch (TransactionException ex) {
						JOptionPane.showMessageDialog(null, "Email(s) failed to send", "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
			}

		});

		emailPanel.add(subjectPanel);
		emailPanel.add(scroll);
		emailPanel.add(send);

		return emailPanel;
	}

	private static void changeAllValues(boolean newValue){
		for(JCheckBox box : boxes)
			box.setSelected(newValue);
	}

	private static String[] getEmails(){
		ArrayList<String> temp = new ArrayList<String>();

		for(JCheckBox box : boxes){

			if(box.isSelected())
				temp.add(box.getText());
		}
		
		String[] list = new String[temp.size()];
		
		for(int i = 0; i<= temp.size()-1; i++)
			list[i] = temp.get(i);

		return list;
	}


	private static void sendEmail() throws TransactionException{

		String USER_NAME = "locallibrary304";  // GMail user name (just the part before "@gmail.com")
		String PASSWORD = "cs304lib"; // GMail password
		String RECIPIENT = "scott-mastro@hotmail.com";

		System.out.println(getEmails()[0]);
		
		String from = USER_NAME;
		String pass = PASSWORD;
		String[] to = { RECIPIENT }; // list of recipient email addresses
		String s = subject.getText();
		String b = body.getText();

		sendFromGMail(from, pass, to, s, b);
	}

	private static void sendFromGMail(String from, String pass, String[] to, String subject, String body)
			throws TransactionException {
		Properties props = System.getProperties();
		String host = "smtp.gmail.com";
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", from);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		send.setEnabled(false);

		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] toAddress = new InternetAddress[to.length];

			// To get the array of addresses
			for( int i = 0; i < to.length; i++ ) {
				toAddress[i] = new InternetAddress(to[i]);
			}

			for( int i = 0; i < toAddress.length; i++) {
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			}

			message.setSubject(subject);
			message.setText(body);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			JOptionPane.showMessageDialog(null, "Email was sent", "Success", JOptionPane.INFORMATION_MESSAGE);
			frame.setVisible(false);
		}
		catch (Exception e) {
			throw new TransactionException();
		}finally{
			send.setEnabled(true);
		}
	}
}
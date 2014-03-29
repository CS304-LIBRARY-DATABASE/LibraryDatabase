package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
						sendEmail();
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


	private static void sendEmail(){

		send.setEnabled(false);
		try {
			EmailHandler.sendEmail(getEmails(), subject.getText(), body.getText());
			JOptionPane.showMessageDialog(null, "Email was sent", "Success", JOptionPane.INFORMATION_MESSAGE);
			frame.setVisible(false);
		} catch (TransactionException e) {
			JOptionPane.showMessageDialog(null, "Email was unable to be sent", "Error", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		} finally { 
			send.setEnabled(true);
		}
		
	}
}
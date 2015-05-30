package gui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;


public class EnterKeyDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 
	public static void main(String[] args) {
		try {
			EnterKeyDialog dialog = new EnterKeyDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
	private Callback okayCallback;

	private JTextArea keyTextArea;

	private JLabel fileNameLabel;
	
	/**
	 * Create the dialog.
	 */
	public EnterKeyDialog(Callback _okayCallback) {
		okayCallback = _okayCallback;
		setBounds(100, 100, 450, 275);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			keyTextArea = new JTextArea();
			keyTextArea.setBounds(10, 123, 414, 45);
			JScrollPane scrollPane = new JScrollPane(keyTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setBounds(10, 32, 414, 81);
			contentPanel.add(scrollPane);
		}
		
		JLabel lblEnterKey = new JLabel("Enter key:");
		lblEnterKey.setBounds(10, 11, 64, 14);
		contentPanel.add(lblEnterKey);
		
		JButton btnChooseFile = new JButton("Choose file...");
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Create a file chooser
				final JFileChooser fc = new JFileChooser();
				//In response to a button click:
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            
		            fileNameLabel.setText(file.getName());
		            
		            BufferedReader in = null;
					try {
						in = new BufferedReader(new FileReader(file));
			            String line = in.readLine();
			            while(line != null){
			              keyTextArea.append(line + "\n");
			              line = in.readLine();
			            }
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		        } 
			}
		});
		btnChooseFile.setBounds(10, 150, 119, 23);
		contentPanel.add(btnChooseFile);
		
		JLabel lblNewLabel = new JLabel("Or browse file:");
		lblNewLabel.setBounds(10, 124, 84, 23);
		contentPanel.add(lblNewLabel);
		
		fileNameLabel = new JLabel("No file selected.");
		fileNameLabel.setBounds(139, 150, 106, 23);
		contentPanel.add(fileNameLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okayCallback.doAction(keyTextArea.getText());
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}

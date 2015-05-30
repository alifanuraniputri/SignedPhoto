package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import algorithm.dsa.ECDSA;
import algorithm.dsa.Point;

public class GenerateKeysFrame extends JFrame {

	private JPanel contentPane;
	
	// currently used ECC params
	BigInteger _p = null;
	BigInteger _a = null;
	BigInteger _b = null;
	BigInteger _xG = null;
	BigInteger _yG = null;
	BigInteger _n = null;

	private JTextArea privateKeyTextArea;

	private JTextArea publicKeyTextArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenerateKeysFrame frame = new GenerateKeysFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GenerateKeysFrame() {
		
		initialize();
		
		setTitle("Generate Keys");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 642, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox curveList = (JComboBox) e.getSource();
				int index = curveList.getSelectedIndex();
				// System.out.println("index: " + index);
				setECCParams(index);
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"NIST P-192", "NIST P-224", "NIST P-256"}));
		comboBox.setBounds(103, 11, 299, 20);
		contentPane.add(comboBox);
		
		JLabel lblEccCurve = new JLabel("ECC Curve:");
		lblEccCurve.setBounds(10, 14, 83, 14);
		contentPane.add(lblEccCurve);
		
		JLabel lblNewLabel = new JLabel("EC Private Key:");
		lblNewLabel.setBounds(10, 39, 96, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblEcPublicKey = new JLabel("EC Public Key:");
		lblEcPublicKey.setBounds(319, 42, 96, 14);
		contentPane.add(lblEcPublicKey);
		
		JButton btnGenerateEcKey = new JButton("Generate EC Key Pair");
		btnGenerateEcKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				generate();
			}
		});
		btnGenerateEcKey.setBounds(10, 245, 162, 75);
		contentPane.add(btnGenerateEcKey);
		
		JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 56, 299, 178);
		contentPane.add(scrollPane);
		
		privateKeyTextArea = new JTextArea();
		scrollPane.setViewportView(privateKeyTextArea);
		
		JScrollPane scrollPane_1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(319, 56, 299, 178);
		contentPane.add(scrollPane_1);
		
		publicKeyTextArea = new JTextArea();
		scrollPane_1.setViewportView(publicKeyTextArea);
		
		JButton btnNewButton = new JButton("Save private key");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePrivateKey(privateKeyTextArea.getText());
				// System.out.println("private key: " + privateKeyTextArea.getText());
			}
		});
		btnNewButton.setBounds(447, 245, 169, 34);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Save public key");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePublicKey(publicKeyTextArea.getText());
			}
		});
		btnNewButton_1.setBounds(447, 286, 169, 34);
		contentPane.add(btnNewButton_1);
		
		
	}

	private void initialize() {
		// init curve with NIST P-192
		_p = new BigInteger ("fffffffffffffffffffffffffffffffeffffffffffffffff",16);
		_a = new BigInteger ("fffffffffffffffffffffffffffffffefffffffffffffffc",16);
		_b = new BigInteger ("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1",16);
		_xG = new BigInteger ("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012",16);
		_yG = new BigInteger ("07192b95ffc8da78631011ed6b24cdd573f977a11e794811",16);
		_n = new BigInteger  ("ffffffffffffffffffffffff99def836146bc9b1b4d22831",16);
	}

	public void generate(){
		BigInteger pri = ECDSA.generatePrivateKeyECDSA(_n);
		Point pub = ECDSA.generatePublicKeyECDSA(pri, _a, _b, _p, new Point(_xG, _yG));
		
		privateKeyTextArea.setText(pri.toString());
		publicKeyTextArea.setText(pub.toString());
	}
	
	public void setECCParams(int index){
		if(index == 0){ // NIST P-192
			_p = new BigInteger ("fffffffffffffffffffffffffffffffeffffffffffffffff",16);
			_a = new BigInteger ("fffffffffffffffffffffffffffffffefffffffffffffffc",16);
			_b = new BigInteger ("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1",16);
			_xG = new BigInteger ("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012",16);
			_yG = new BigInteger ("07192b95ffc8da78631011ed6b24cdd573f977a11e794811",16);
			_n = new BigInteger  ("ffffffffffffffffffffffff99def836146bc9b1b4d22831",16);
		} else if(index == 1){ // NIST P-224
			_p = new BigInteger ("ffffffffffffffffffffffffffffffff000000000000000000000001",16);
			_a = new BigInteger ("fffffffffffffffffffffffffffffffefffffffffffffffffffffffe",16);
			_b = new BigInteger ("b4050a850c04b3abf54132565044b0b7d7bfd8ba270b39432355ffb4",16);
			_xG = new BigInteger ("b70e0cbd6bb4bf7f321390b94a03c1d356c21122343280d6115c1d21",16);
			_yG = new BigInteger ("bd376388b5f723fb4c22dfe6cd4375a05a07476444d5819985007e34",16);
			_n = new BigInteger  ("ffffffffffffffffffffffffffff16a2e0b8f03e13dd29455c5c2a3d",16);
		} else if(index == 2){ // NIST P-256
			_p = new BigInteger ("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff",16);
			_a = new BigInteger ("ffffffff00000001000000000000000000000000fffffffffffffffffffffffc",16);
			_b = new BigInteger ("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b",16);
			_xG = new BigInteger ("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16);
			_yG = new BigInteger ("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16);
			_n = new BigInteger ("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551",16);
		}
	}
	
	public void savePrivateKey(String key){
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		//In response to a button click:
		int returnVal = fc.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {		 
				File file = new File(fc.getSelectedFile().getPath() + ".private");
					 
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(_p + " " + _a + " " + _b + "\n");
				bw.write(_xG + " " + _yG + "\n");
				bw.write(_n + "\n");
				bw.write(key + "\n");
				bw.close();
	 	 
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
	
	public void savePublicKey(String key){
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		//In response to a button click:
		int returnVal = fc.showSaveDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {		 
				File file = new File(fc.getSelectedFile().getPath() + ".public");
					 
				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}
	 
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(_p + " " + _a + " " + _b + "\n");
				bw.write(_xG + " " + _yG + "\n");
				bw.write(_n + "\n");
				bw.write(key + "\n");
				bw.close();
	 	 
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
}

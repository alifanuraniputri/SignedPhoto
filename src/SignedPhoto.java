import gui.Callback;
import gui.EnterKeyDialog;
import gui.GenerateKeysFrame;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import javax.swing.JTextField;
import javax.swing.JTextArea;

import algorithm.dsa.ECDSA;
import algorithm.dsa.Point;
import algorithm.watermark.SteganografiProcessing;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;


public class SignedPhoto extends JFrame {

	private JPanel contentPane;
	private final JButton btnGenerateKey = new JButton("Generate Key");
	private final JButton btnUploadPhoto = new JButton("Upload Photo");
	private final JButton btnEnterKey = new JButton("Enter Key");
	private final JLabel labelName = new JLabel("");
	private File picture;
	protected String fileName;
	private BufferedImage chosenPicture;
	private BufferedImage resultPicture;
	private final JLabel lblKey = new JLabel("Key");
	private final JTextArea txtKey = new JTextArea();
	private final JButton btnEmbed = new JButton("Embed Digital Signature");
	private final JButton btnVerifyDigitalSignature = new JButton("Verify Digital Signature");
	private SteganografiProcessing stegano;
	
	// currently used ECC params
	BigInteger _p = null;
	BigInteger _a = null;
	BigInteger _b = null;
	BigInteger _xG = null;
	BigInteger _yG = null;
	BigInteger _n = null;
	BigInteger privateKey;
	Point publicKey;
	private final JScrollPane scrollPane = new JScrollPane();
	private final JButton btnSavePicture = new JButton("save picture");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException ex) {
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignedPhoto frame = new SignedPhoto();
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
	public SignedPhoto() {
		setTitle("SignedPhoto");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 604, 392);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		btnGenerateKey.setBounds(321, 11, 112, 23);
		
		btnGenerateKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GenerateKeysFrame frame = new GenerateKeysFrame();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setVisible(true);
			}
		});
		
		contentPane.add(btnGenerateKey);
		btnUploadPhoto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectImage();
			}
		});
		btnUploadPhoto.setBounds(31, 11, 106, 23);
		
		contentPane.add(btnUploadPhoto);
		btnEnterKey.setEnabled(false);
		btnEnterKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterKey();
			}
		});
		btnEnterKey.setBounds(172, 11, 118, 23);
		
		contentPane.add(btnEnterKey);
		labelName.setOpaque(true);
		labelName.setForeground(new Color(255, 105, 180));
		labelName.setBackground(Color.WHITE);
		labelName.setBounds(31, 53, 259, 264);
		
		contentPane.add(labelName);
		lblKey.setBounds(321, 53, 56, 23);
		
		contentPane.add(lblKey);
		btnEmbed.setEnabled(false);
		btnEmbed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				embedDigitalSignature();
			}
		});
		btnEmbed.setBounds(321, 208, 245, 23);
		
		contentPane.add(btnEmbed);
		btnVerifyDigitalSignature.setEnabled(false);
		btnVerifyDigitalSignature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				verifyDigitalSignature();
			}
		});
		btnVerifyDigitalSignature.setBounds(321, 242, 245, 23);
		
		contentPane.add(btnVerifyDigitalSignature);
		
		
		scrollPane.setViewportView(txtKey);
		txtKey.setEnabled(false);
		scrollPane.setBounds(321, 87, 245, 93);
		
		contentPane.add(scrollPane);
		btnSavePicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simpanCitra();
			}
		});
		btnSavePicture.setBounds(477, 11, 89, 23);
		
		contentPane.add(btnSavePicture);

	}
	
	public boolean isValidPrivateKeyFormat(String key){
		// p a b
		// xg yg
		// n
		// private_key
		// total 7 elemen
		String keys[] = key.split("\n|\r\n| ");
		return (keys.length == 7);
	}
	
	public boolean isValidPublicKeyFormat(String key){
		// p a b
		// xg yg
		// n
		// pubkey_x pubkey_y
		// total 8 elemen
		String keys[] = key.split("\n|\r\n| ");
		return (keys.length == 8);
	}
	
	public void setPublicKey(String key){
		String keys[] = key.split("\n|\r\n| ");
		
		_p = new BigInteger(keys[0]);
		_a = new BigInteger(keys[1]);
		_b = new BigInteger(keys[2]);
		_xG = new BigInteger(keys[3]);
		_yG = new BigInteger(keys[4]);
		_n = new BigInteger(keys[5]);
		
		publicKey = new Point(new BigInteger(keys[6]), new BigInteger(keys[7]));
	}
	
	public void setPrivateKeyValue(String key){
		String keys[] = key.split("\n|\r\n| ");
		
		_p = new BigInteger(keys[0]);
		_a = new BigInteger(keys[1]);
		_b = new BigInteger(keys[2]);
		_xG = new BigInteger(keys[3]);
		_yG = new BigInteger(keys[4]);
		_n = new BigInteger(keys[5]);
		privateKey = new BigInteger(keys[6]);
		
		System.out.println("_p: " + _p);
		System.out.println("_a: " + _a);
		System.out.println("_b: " + _b);
		System.out.println("_xG: " + _xG);
		System.out.println("_yG: " + _yG);
		System.out.println("_n: " + _n);
		System.out.println("privateKey: " + privateKey);
	}
	
	public void enterKey() {
		EnterKeyDialog dialog = new EnterKeyDialog(new Callback() {
			public void doAction(Object param) {
				String key = (String) param;
				if(isValidPrivateKeyFormat(key)){
					setPrivateKeyValue(key);
					txtKey.setText(privateKey.toString(16));
					btnEmbed.setEnabled(true);
					btnVerifyDigitalSignature.setEnabled(false);
				} else if(isValidPublicKeyFormat(key)){
					setPublicKey(key);
					txtKey.setText("( "+publicKey.x.toString(16)+" , "+publicKey.y.toString(16)+" ) ");
					btnEmbed.setEnabled(true);
					btnEmbed.setEnabled(false);
					btnVerifyDigitalSignature.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(null, "Format File is not a Public or Priave Key");
				}
			}
		});
		dialog.setTitle("Enter encryption key...");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
	
	public void selectImage() {
		EventQueue.invokeLater(new Runnable() {
			

			@Override
			public void run() {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setEnabled(false);
				FileFilter filter = new FileNameExtensionFilter(
						"JPG, GIF, BMP, & PNG Images", "jpg", "gif", "png",
						"bmp");
				fileChooser.setFileFilter(filter);
				fileChooser.setBounds(0, -41, 582, 397);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				// Configure some more here
				final int userValue = fileChooser.showOpenDialog(fileChooser);
				if (userValue == JFileChooser.APPROVE_OPTION) {
					picture = fileChooser.getSelectedFile();
					fileName = picture.getName();
					try {
						chosenPicture = ImageIO.read(picture);

					} catch (final IOException not_action) {
						not_action.printStackTrace();
					}

					labelName.setIcon(new ImageIcon(chosenPicture.getScaledInstance(
							labelName.getWidth(), labelName.getHeight(),
							BufferedImage.TRANSLUCENT)));
					btnEnterKey.setEnabled(true);

				}
			}
		});

	}
	
	public void embedDigitalSignature() {

		stegano = new SteganografiProcessing(chosenPicture, fileName, privateKey, _a, _b, _p, new Point(_xG,_yG), _n);
			resultPicture = stegano.sisipkanLSBstandard();
			
			labelName.setIcon(new ImageIcon(resultPicture.getScaledInstance(
					labelName.getWidth(), labelName.getHeight(),
					BufferedImage.TRANSLUCENT)));
			
			JOptionPane.showMessageDialog(getContentPane(), "ECDSA embedded");
		
	}
	
	public void verifyDigitalSignature() {
		stegano = new SteganografiProcessing(chosenPicture, publicKey, _a, _b, _p, new Point(_xG,_yG), _n);
		stegano.getPlainTextLSBstandard();
	}
	
	public void simpanCitra() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setApproveButtonText("Save");
				FileFilter filter = new FileNameExtensionFilter("BMP(.bmp)",
						"bmp");
				fileChooser.setDialogTitle("Save Stegano Image");
				fileChooser.setFileFilter(filter);

				final int userValue = fileChooser.showOpenDialog(fileChooser);

				if (userValue == JFileChooser.APPROVE_OPTION) {

					BufferedWriter writer = null;
					// write it new file
					try {
						if (fileChooser.getSelectedFile().getAbsolutePath()
								.contains(".bmp"))
							ImageIO.write(resultPicture, "BMP", new File(fileChooser
									.getSelectedFile().getAbsolutePath()));
						else
							ImageIO.write(resultPicture, "BMP", new File(fileChooser
									.getSelectedFile().getAbsolutePath()
									+ ".bmp"));
						JOptionPane.showMessageDialog(getContentPane(),
								"citra berpesan terseimpan");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}

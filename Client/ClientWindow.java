package Client;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPasswordField;

import Other.ChatMessage;

import java.awt.Font;
import java.awt.Color;

public class ClientWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextField txtName;
	private JTextField txtIP;
	private JTextField txtPort;
	private JTextArea history;
	private JTextArea txtrOnlineUsers;
	private JButton btnLogin, btnSignUp, btnSendFile, btnLogout;
	private JLabel lblUsername, lblPassword, lblIp, lblPort, lblConfirmedPassword;
	private JPasswordField txtPassword;
	private JPasswordField txtPasswordConfirmed;
	private DefaultCaret caret;
	private JMenuBar menuBar;
	
	static ClientWindow clw;
	static String username, password, ip;
	static int port;
	static int choice;
	Client client;
	
	static boolean login = false, signup = false; //Boolean for login and signup states
	
	ClientWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(950, 680);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		history = new JTextArea();
		history.setEditable(false);
		history.setBounds(10, 11, 685, 558);
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(history, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 10, 685, 550);
		contentPane.add(scroll);
		contentPane.add(scroll);
		
		txtMessage = new JTextField();
		

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnAbout = new JMenu("Help");
		menuBar.add(mnAbout);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, 
						" Project Telecommunication\n"
						+ "           Chat Program\n"
						+ "\n         Team Members:\n\n"
						+ "           Vo Duy Hieu\n"
						+ "          Mai Hong Phuc\n"
						+ "         Cao Minh Gia Huy");
			}
		});
		
		JMenuItem mntmInstruction = new JMenuItem("Instruction");
		mntmInstruction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Write '<To ' + username +'>' to send message to user with the name 'username'\n"
						+ "For example: <To A> abc    means send to user 'A' message 'abc'\n"
						+ "Default message send to all users");
			}
		});
		mnAbout.add(mntmInstruction);
		mnAbout.add(mntmAbout);
		
		
		txtrOnlineUsers = new JTextArea();
		txtrOnlineUsers.setEditable(false);
		txtrOnlineUsers.setBounds(713, 35, 211, 206);
		contentPane.add(txtrOnlineUsers);
		
		txtMessage.setBounds(10, 580, 685, 20);
		contentPane.add(txtMessage);
		txtMessage.setColumns(10);
		txtMessage.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ENTER){
	            	client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, txtMessage.getText()));
					send(txtMessage.getText());
	            }
	        }

	    });
		
		
		txtName = new JTextField();
		txtName.setBounds(713, 388, 211, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		txtIP = new JTextField();
		txtIP.setBounds(713, 277, 211, 20);
		contentPane.add(txtIP);
		txtIP.setColumns(10);
		
		txtPort = new JTextField();
		txtPort.setBounds(713, 333, 211, 20);
		contentPane.add(txtPort);
		txtPort.setColumns(10);
		
		lblUsername = new JLabel("Username:");
		lblUsername.setForeground(Color.WHITE);
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblUsername.setBounds(713, 364, 113, 14);
		contentPane.add(lblUsername);
		
		lblPassword = new JLabel("Password:");
		lblPassword.setForeground(Color.WHITE);
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPassword.setBounds(713, 419, 86, 14);
		contentPane.add(lblPassword);
		
		lblIp = new JLabel("Server IP:");
		lblIp.setForeground(Color.WHITE);
		lblIp.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblIp.setBounds(713, 252, 127, 14);
		contentPane.add(lblIp);
		
		lblPort = new JLabel("Port number:");
		lblPort.setForeground(Color.WHITE);
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPort.setBounds(713, 308, 127, 14);
		contentPane.add(lblPort);
		
		lblConfirmedPassword = new JLabel("Confirmed Password:");
		lblConfirmedPassword.setForeground(Color.WHITE);
		lblConfirmedPassword.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblConfirmedPassword.setBounds(713, 475, 186, 14);
		contentPane.add(lblConfirmedPassword);
		
		//If press button login
		btnLogin = new JButton("Login");
		btnLogin.setBounds(713, 543, 86, 23);
		contentPane.add(btnLogin);
		btnLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Login();
			}
		});
		
		btnSignUp = new JButton("Sign up");
		btnSignUp.setBounds(713, 577, 86, 23);
		contentPane.add(btnSignUp);
		btnSignUp.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SignUp();
			}
		});
		
		btnLogout = new JButton("Logout");
		btnLogout.setBounds(838, 543, 86, 23);
		contentPane.add(btnLogout);
		btnLogout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Logout();
			}
		});
		
		btnSendFile = new JButton("Send File");
		btnSendFile.setBounds(838, 577, 86, 23);
		contentPane.add(btnSendFile);
		btnSendFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendFile();
			}
		});
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(713, 444, 211, 20);
		contentPane.add(txtPassword);
		
		txtPasswordConfirmed = new JPasswordField();
		txtPasswordConfirmed.setBounds(713, 495, 211, 20);
		contentPane.add(txtPasswordConfirmed);
		
		
		//Set Enable button
		btnSendFile.setEnabled(false);
		btnLogout.setEnabled(false);
		txtMessage.setEditable(false);
		
		JLabel label = new JLabel("");
		Image background = new ImageIcon(this.getClass().getResource("/hd-road-wallpaper.jpg")).getImage();
		
		JLabel lblOnlineUsers = new JLabel("Online Users");
		lblOnlineUsers.setForeground(Color.ORANGE);
		lblOnlineUsers.setFont(new Font("Times New Roman", Font.BOLD, 23));
		lblOnlineUsers.setBounds(749, 9, 150, 20);
		contentPane.add(lblOnlineUsers);
		label.setIcon(new ImageIcon(background));
		label.setBounds(0, 0, 934, 620);
		contentPane.add(label);
	}	
	
	
	//Function of 'Login' button
	public void Login(){
		username = txtName.getText();
		password = txtPassword.getText();
		ip = txtIP.getText();
		port = Integer.parseInt(txtPort.getText());
		login = true;
		choice = 1;
		if(username.length() == 0) return;
		if(password.length() == 0) return;
		if(ip.length() == 0) return;
		client = new Client(this);
		client.establishClient(ip, port, choice, username, password);
		
		//Enable and disable button
		setTitle("User : " + username);
		btnLogin.setEnabled(false);
		btnSignUp.setEnabled(false);
		btnSendFile.setEnabled(true);
		btnLogout.setEnabled(true);
		txtMessage.setEditable(true);
		txtName.setEditable(false);
		txtPassword.setEditable(false);
		txtPasswordConfirmed.setEditable(false);
		txtIP.setEditable(false);
		txtPort.setEditable(false);
	}
	
	//Function of 'Signup' button
	public void SignUp(){
		username = txtName.getText();
		password = txtPassword.getText();
		ip = txtIP.getText();
		port = Integer.parseInt(txtPort.getText());
		signup = true;
		choice = 0;
		//Check
		if(username.length() == 0) return;
		else if(password.length() == 0) return;
		else if(ip.length() == 0) return;
		if ((!Arrays.equals(txtPassword.getPassword(), txtPasswordConfirmed.getPassword()))){
			JOptionPane.showMessageDialog(null, "Your password does not match");
			signup = false;
			return;
		}
		client = new Client(this);
		client.establishClient(ip, port, choice, username, password);
		
		//Enable and disable button
		setTitle("User : " + username);
		btnLogin.setEnabled(false);
		btnSignUp.setEnabled(false);
		btnSendFile.setEnabled(true);
		btnLogout.setEnabled(true);
		txtMessage.setEditable(true);
		txtName.setEditable(false);
		txtPassword.setEditable(false);
		txtPasswordConfirmed.setEditable(false);
		txtIP.setEditable(false);
		txtPort.setEditable(false);
	}
	
	//Function for 'Logout' button
	public void Logout(){
		btnLogin.setEnabled(true);
		btnSignUp.setEnabled(true);
		btnLogout.setEnabled(false);
		btnSendFile.setEnabled(false);
		txtMessage.setEditable(false);
		txtName.setEditable(true);
		txtPassword.setEditable(true);
		txtPasswordConfirmed.setEditable(true);
		txtIP.setEditable(true);
		txtPort.setEditable(true);
		txtName.setText("");
		txtPassword.setText("");
		txtPasswordConfirmed.setText("");
		txtIP.setText("");
		txtPort.setText("");
		JOptionPane.showMessageDialog(null, "Logout successfully");
		client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, "Logout"));
		client.Disconnect();
	}
	
	//Function for 'Send file' button
	public void sendFile(){
		String receiver = JOptionPane.showInputDialog(btnSendFile, "Enter receiver username: ");
		client.sendMessage(new ChatMessage(ChatMessage.SENDREQUEST, "", username,receiver));
	}
	
	//Reset the window when login/signup error occurs
	public void tryAgain(){
		btnLogin.setEnabled(true);
		btnSignUp.setEnabled(true);
		btnLogout.setEnabled(false);
		btnSendFile.setEnabled(false);
		txtMessage.setEditable(false);
		txtName.setEditable(true);
		txtPassword.setEditable(true);
		txtPasswordConfirmed.setEditable(true);
		txtIP.setEditable(true);
		txtPort.setEditable(true);
		txtName.setText("");
		txtPassword.setText("");
		txtPasswordConfirmed.setText("");
		history.setText("");
	}
	//Online users update
	public void onlineusers(String onlineuser, int update){
		if(update==1){
		txtrOnlineUsers.setText("");
		}
		txtrOnlineUsers.append("    " + onlineuser + "\n");
	}

	
	//Function to display message to the chat window
	public void send(String message){
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String hours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
		String seconds = Integer.toString(calendar.get(Calendar.SECOND));
		if (message.equals("")) return;
		message = hours + ":" + minutes + ":" + seconds + "| " + username + " : " + message;
		console(message);
		txtMessage.setText("");
	}
	
	public void console(String message){
		history.append(message + "\n\r");
	}
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow frame = new ClientWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

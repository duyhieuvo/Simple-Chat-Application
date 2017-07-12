package Client;

import Other.ChatMessage;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Client {
	static ObjectInputStream din;
	static ObjectOutputStream dout;
	static Socket s;
	
	String ip;
	String username, password;
	int pnum, choice; //'choice' variable is used to distinguish login/signup 
	static ClientWindow clw;
	
	@SuppressWarnings("static-access")
	Client(ClientWindow clw){
		this.clw = clw;
	}
	
	public static void establishClient(String ip, int pnum, int choice, String username, String password){
		ChatMessage msg;
		try{
			InetAddress addr = InetAddress.getByName(ip);
			s = new Socket(addr,pnum);
			System.out.println("Successfully connected " + s.getInetAddress() + ":" +s.getPort());
			dout = new ObjectOutputStream(s.getOutputStream());
			dout.flush();
			din = new ObjectInputStream(s.getInputStream());
			//Create thread to listen to server
			new FromServer().start();
			//Send login or signup request to Server
			if(choice==1){
				msg = new ChatMessage(ChatMessage.LOGIN,"");
				dout.writeObject(msg);
			}else if (choice == 0){
				msg = new ChatMessage(ChatMessage.SIGNUP,"");
				dout.writeObject(msg);
			}
			msg = new ChatMessage(ChatMessage.MESSAGE,username);
			dout.writeObject(msg);
			msg = new ChatMessage(ChatMessage.MESSAGE,password);
			dout.writeObject(msg);
			
		}catch(Exception e){
			System.out.println("Error connecting to server");
			System.exit(-1);
		}
	}
	
	public static void Disconnect(){
		try{
			if(dout!=null) dout.close();
			if(din!=null) din.close();
			if(s!=null)s.close();
		}catch(Exception e){
			System.out.println("Error closing the connection");
			System.exit(-1);
		}
		System.exit(0);
	}
	
	public static void sendMessage(ChatMessage msg){
		try{
			dout.writeObject(msg);
			dout.flush();
		}catch(IOException e){
			System.out.println("Error sending out message");
			System.exit(-1);
		}
	}
	
	//Used for console testing
	public static void main(String args[]){
		BufferedReader br;
		String ip ="";
		int pnum = 0;
		String username ="", password ="";
		String msg = "";
		String choice = ""; //login or signup
		ChatMessage chatmsg = null;
		boolean keepgoing = true;
		
		try{
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter the ip address");
			ip = br.readLine();
			System.out.println("Enter port number");
			pnum = Integer.parseInt(br.readLine());
			do{
			System.out.println("Login (enter 1) or Signup (enter 0): ");
			choice = br.readLine();
			}while((!"1".equals(choice))&&(!"0".equals(choice)));
			
			if(choice.equals("1")){
			System.out.println("Enter username: ");
			username = br.readLine();
			System.out.println("Enter password: ");
			password = br.readLine();
			}else if(choice.equals("0")){
				System.out.println("Choose your username: ");
				username = br.readLine();
				System.out.println("Create a password: ");
				String pass = br.readLine();
				System.out.println("Confirm your password: ");
				String pass_confirm = br.readLine();
				while(!pass.equals(pass_confirm)){
					System.out.println("These passwords don't match");
					System.out.println("Create a password: ");
					pass = br.readLine();
					System.out.println("Confirm your password: ");
					pass_confirm = br.readLine();
				}
				password = pass;
			}
			
		}catch(Exception e){
			System.out.println("Error occured");
			System.exit(-1);
		}
		establishClient(ip,pnum,Integer.parseInt(choice),username,password);
		System.out.println("To send message to specific user: ");
		System.out.println("\"<To 'username'>\" + message");
		while(keepgoing){
			try{
			br = new BufferedReader(new InputStreamReader(System.in));
			msg = br.readLine();
			}catch(IOException e){
				System.exit(-1);
			}
			if(msg.equalsIgnoreCase("LOGOUT")){
				chatmsg = new ChatMessage(ChatMessage.LOGOUT,msg);
				keepgoing = false;
			}//For testing purpose of the file sending function in console mode.
			else if(msg.equalsIgnoreCase("duyhieu")){
				chatmsg = new ChatMessage(ChatMessage.SENDREQUEST,"",username,"duyhieu");
			}
			else{
				chatmsg = new ChatMessage(ChatMessage.MESSAGE,msg);
			}
			sendMessage(chatmsg);
		}
		Disconnect();
	}
	
	//test
	static class FromServer extends Thread{
		public void run(){
			while(true){
				try{
				ChatMessage msg = (ChatMessage) din.readObject();
				System.out.println(msg.getMessage());
				if(msg.getType()==ChatMessage.MESSAGE){
					if(msg.getMessage().equals("Login successfull")){
						JOptionPane.showMessageDialog(null, "Login successfully");
					}
					else if(msg.getMessage().equals("Sign up successfully")){
						JOptionPane.showMessageDialog(null, "Sign up successfully");
					}
					else{
						clw.console(msg.getMessage());
						System.out.println(msg.getMessage());
					}
				}
				if(msg.getType()==ChatMessage.ERROR){
					if(msg.getMessage().equals("Wrong username or password")){
						JOptionPane.showMessageDialog(null, "Wrong username or password");
						clw.tryAgain();
						return;
					}
					else if(msg.getMessage().equals("Username is already exists, please choose another username")){
						JOptionPane.showMessageDialog(null, "Username is already exists, please choose another username");
						clw.tryAgain();
						return;
					}
				}
				///When establish the send file connection
				else if(msg.getType()==ChatMessage.SENDREQUEST){
					System.out.println("Request received");
					InetAddress ip = InetAddress.getLocalHost();
					ChatMessage confirmation = new ChatMessage(ChatMessage.SENDCONFIRM,"",msg.getSender(),msg.getReceiver(),ip);
					sendMessage(confirmation);
					System.out.println("Confirmation sent");
					FileReceive.receive();
				}else if(msg.getType()==ChatMessage.SENDCONFIRM){
					System.out.println("Confirmation received");
					System.out.println("Ip of the receiver: " + msg.getIP());
					FileSend.send(msg.getIP());
				}
				else if(msg.getType()==ChatMessage.ONLINEUSERS){
					clw.onlineusers(msg.getMessage(),msg.update());
				}
				}catch(IOException e){
					
				}catch(ClassNotFoundException e){
					
				}
				
			}
		}
	}
}

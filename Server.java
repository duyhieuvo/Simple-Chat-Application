package Server;

import Other.ChatMessage;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	static int pnum = 13000;
	static InetAddress ip;
	static ChatMessage chatmsg = null;
	static ServerSocket ss;
	static int connectionID = -1;
	static ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
	static String onlineusers[] = new String[20];
	
	//specify directory to the Data.txt file which stores the usernames and passwords
	static String database = "E:\\Eclipse project\\Final\\Simplest chat app 3.0\\Simplest chat app\\Data.txt";

	
	//Multithread: 
	//Multi clients connect to the same socket 
	//Listen to multi clients
	//transmit message to multi clients
	public class ClientThread extends Thread{
	
		Socket s;
		ObjectInputStream din;
		ObjectOutputStream dout;
		ChatMessage msg;
		String username; //name of the user connected
		String password; //password of this user
		int id;//the order of the client connected
		boolean loginstatus = false;
		boolean signup = false;
		//Constructor
		ClientThread(Socket s){
			this.s = s;

			try{
				dout = new ObjectOutputStream(s.getOutputStream());
				dout.flush();
				din = new ObjectInputStream(s.getInputStream());
				msg = (ChatMessage) din.readObject();
				if(msg.getType() == ChatMessage.LOGIN){
					msg = (ChatMessage) din.readObject();
					username = msg.getMessage();
					msg = (ChatMessage) din.readObject();
					password = msg.getMessage();
					if(Login(username,password)){//Check username and password for login function
						loginstatus = true; //change login status to true
						System.out.println(username + " successfully connected");
						connectionID +=1;
						id = connectionID;
						dout.writeObject(new ChatMessage(ChatMessage.MESSAGE,"Login successfull"));
					}else{
						loginstatus = false;
						System.out.println(username + " login fail");
						msg = new ChatMessage(ChatMessage.ERROR,"Wrong username or password");
						dout.writeObject(msg);
						dout.flush();
					}
				}else if(msg.getType() == ChatMessage.SIGNUP){
					msg = (ChatMessage) din.readObject();
					username = msg.getMessage();
					msg = (ChatMessage) din.readObject();
					password = msg.getMessage();
					if(Signup(username,password)){//Check username and password for signup function
					System.out.println(username + " successfully signed up");
					connectionID +=1;
					id = connectionID;
					dout.writeObject(new ChatMessage(ChatMessage.MESSAGE,"Sign up successfully"));
					signup = true; //change signup status to true
					}else{
						System.out.println("'" + username + "'" + " already exists.");
						dout.writeObject(new ChatMessage(ChatMessage.ERROR,"Username is already exists, please choose another username"));
					}
				}
				
			}catch(IOException e){
				System.out.println("Error creating data stream with " + username);
				System.out.println(e.getStackTrace());
			}catch(ClassNotFoundException e){
				System.out.println("Error creating data stream with " + username);
				System.out.println(e.getStackTrace());
			}
		}
		public void run(){
			boolean keepGoing = true;
			while(keepGoing){
				try{
				msg = (ChatMessage) din.readObject(); //read input message from client
				}catch(ClassNotFoundException e){
					System.out.println("Error");
					break;
				}catch(IOException e){
					System.out.println("Error connection with " + username);
					break;
				}
				switch(msg.getType()){
				case ChatMessage.LOGOUT:
					System.out.println(username + " logged out");
					keepGoing = false;
					break;
				case ChatMessage.MESSAGE: 
					System.out.println(username +": " + msg.getMessage());
					sendMessage(msg);
					break;
				case ChatMessage.SENDREQUEST: //Sending file request
					String receiver = msg.getReceiver();
					String sender = msg.getSender();
					System.out.println("Send requested, sender: " + sender + ", receiver " +receiver );
					ChatMessage request = new ChatMessage(ChatMessage.SENDREQUEST,"<To "+ receiver + ">",sender,receiver);
					sendMessage(request);
					break;
				case ChatMessage.SENDCONFIRM: //Sending file confirmation
					String receiver1 = msg.getReceiver();
					String sender1 = msg.getSender();
					InetAddress ip = msg.getIP();
					ChatMessage confirm = new ChatMessage(ChatMessage.SENDCONFIRM,"<To " + sender1 + ">",sender1,receiver1,ip);
					sendMessage(confirm);
					break;
				}
			}
			Remove(id);
			Disconnect();
		}
		
		public void Disconnect(){
			try{
				if(dout!=null) dout.close();
				if(din!=null) din.close();
				if(s!=null)s.close();
				}catch(Exception e){
					System.out.println("Error closing the connection");
					System.exit(-1);
				}
		}
		//Check login username and password: scan through a text file storing data about usernames and passwords of all users
		boolean Login(String username, String password){
			boolean login = false;
			Scanner scan;
			try{
			scan = new Scanner(new File(database));
			} catch(FileNotFoundException e){
	        	System.out.println("Error connecting to users data");
	        	return false;
	        }
	        try{
			while(scan.hasNextLine()){
	        	String user = scan.nextLine();
	        	String pass = scan.nextLine();
	        	if (username.equals(user) && password.equals(pass)) {
	        		scan.close();
	        		login = true;
	                break;
	        	}
	        }
			}catch (NoSuchElementException e){
	        	scan.close();
	        	login = false;
	        }
	        return login;
		}
		
		//Signup function: write new usernames and passwords to the data
		boolean Signup(String username, String password){
			boolean signup = false;
			try{
				File file = new File(database);
				//Test if username already exists
				int counter = 0;
				Scanner scan = new Scanner(file);
				while(scan.hasNextLine()){
		        	if((counter%2)==0){
		        		if(scan.nextLine().equals(username)){
		        			scan.close();
		        			signup = false;
		        			return signup;
		        		}
		        		counter++;
		        	}else
		        	counter++;
		        }
				signup = true;
				
				//Write new username data to the txt file
				FileWriter filewriter = new FileWriter(file,true);
				BufferedWriter writer = new BufferedWriter(filewriter);		
				writer.write(System.lineSeparator()+ username);
				writer.write(System.lineSeparator()+ password);
				scan.close();
				writer.close();
				return signup;
				}catch(Exception e){
					System.out.println("Error connecting to the data");
					System.out.println(e.getStackTrace());
					return false;
				}		
		}
		
		//Function used for sending message to clients 
		public synchronized void sendMessage(ChatMessage message){
			 //Obtain the date and time
			 Date date = new Date();
			 Calendar calendar = Calendar.getInstance();
			 calendar.setTime(date);
			 String hours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
			 String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
			 String seconds = Integer.toString(calendar.get(Calendar.SECOND));
			 
			 
			//Send message privately
			for(int j = 0; j < clientList.size();j++){
				ClientThread client = clientList.get(j);
				if (message.getMessage().startsWith(("<To " + client.username +">"))){
					String str = hours + ":" + minutes + ":" + seconds + "| " + username + ": " + message.getMessage().replaceAll("<[^>]*>", "");
					//Thêm vô
					ChatMessage newMessage;
					if(message.getType()==ChatMessage.SENDREQUEST){
						newMessage = new ChatMessage(ChatMessage.SENDREQUEST,"",message.sender(),message.receiver());
					} else if(message.getType()==ChatMessage.SENDCONFIRM) {
						newMessage = new ChatMessage(ChatMessage.SENDCONFIRM,"",message.sender(),message.receiver(),message.getIP());
					} ///
					else{	
						newMessage = new ChatMessage(ChatMessage.MESSAGE,str);
					}
					try{
					client.dout.writeObject(newMessage);
					client.dout.flush();
					}catch(IOException e){
						
					}
					return;
				}
			}	
			//send message to all user
		if(message.getType()!=ChatMessage.SENDREQUEST){ //to avoid accidentally send a message to all user when 1 user type wrong receiver username when sending file
			for(int j = 0; j < clientList.size();j++){
					ClientThread client = clientList.get(j);
					if(client.id == id){
						continue;
					}
					try{
					String str = hours + ":" + minutes + ":" + seconds + "| " + username + ": " + message.getMessage();
					ChatMessage newMessage = new ChatMessage(ChatMessage.MESSAGE,str);
					client.dout.writeObject(newMessage);
					client.dout.flush();
					}catch(Exception e){
						
					}
				}
			}
		}
		
		
		
	}
	
	//Function to send online users list
	public static void onlineuser(ClientThread client){
		int update = 0;
		for(int j = 0; j < clientList.size();j++){
			onlineusers[j] = clientList.get(j).username;
			if(j==0){
				update = 1;
			}else{
				update = 0;
			}
			ChatMessage users = new ChatMessage(ChatMessage.ONLINEUSERS,onlineusers[j],update);
			try{
				client.dout.writeObject(users);
				}catch(IOException e){
					System.out.println("Error sending the online users list");
					System.out.println(e.getStackTrace());
			}
		}	
	}

	//Function to remove client thread logged out
	public synchronized void Remove(int i){//when 1 element of the arraylist is removed, the subsequent elements are shifted to the left 1 position
		for (int j = 0;j<clientList.size();j++){
			ClientThread client = clientList.get(j);
			if(client.id== i){
				clientList.remove(j);
				break;
			}
		}
		for (int j = 0;j<clientList.size();j++){ //change the ID of the subsequent elements to match its order in the array
			ClientThread client = clientList.get(j);
			if(client.id> i){
				client.id-=1;
			}
		}
		connectionID-=1;
		//Resend the online users list when a client logged out
		if(clientList.size()!=0){
			for (int j = 0;j<clientList.size();j++){
				onlineuser(clientList.get(j));
			}
		}
	}
	
	public static void establishServer(){
		try{
			ip = InetAddress.getLocalHost();
			System.out.println("Ip address: " + ip.getHostAddress());
			System.out.println("Port number: " + pnum);
			System.out.println("Get ip successfully");
			ss = new ServerSocket(pnum);
			while(true){
				Socket socket = ss.accept();
				ClientThread client = new Server().new ClientThread(socket);
				if((client.loginstatus)||(client.signup)){
				clientList.add(client);
				client.start();
				//Send the online users list when there is new user logs in
				if(clientList.size()!=0){
					for (int j = 0;j<clientList.size();j++){
						onlineuser(clientList.get(j));
					}
				}
				}
			}	
		}catch (Exception e){
			System.out.println("Error creating server");
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}
	
	public static void main(String arg[]) throws ClassNotFoundException, IOException{
		establishServer();
	}
}
package Other;
import java.io.*;
import java.net.*;

public class ChatMessage  implements Serializable{
	//serialVersionUID  to compare the versions of the class 
	//ensuring that the same class was used during Serialization is loaded during Deserialization
	protected static final long serialVersionUID = 1112122200L; 
	public static final int MESSAGE = 0, LOGOUT = 1, LOGIN = 2, SIGNUP = 3, ERROR = 4, SENDREQUEST = 5, SENDCONFIRM = 6, ONLINEUSERS = 7;
	//MESSAGE: normal message
	//LOGOUT, LOGIN: logout and login message
	//SIGNUP: signup message
	//ERROR: when signup or login failed
	//SENDREQUEST, SENDCONFIRM: used for file transfer
	private int type;
	private String message;
	private String sender,receiver;
	private InetAddress ipofreceiver;
	private int update;//for online users
		
	public ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	//used for file transfer sender
	public ChatMessage(int type, String message, String sender, String receiver){
		this.type = type;
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	//used for file transfer receiver
	public ChatMessage(int type, String message, String sender, String receiver, InetAddress ipofreceiver){
		this.type = type;
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.ipofreceiver = ipofreceiver;
	}
	//used for online users 
	public ChatMessage(int type, String message, int update) {
		this.type = type;
		this.message = message;
		this.update = update;
	}

	////////	
	public int getType() {
		return type;
	}
		
	public String getMessage(){
		return message;
	}
	public String getSender(){
		return sender;
	}
	public String getReceiver(){
		return receiver;
	}
	public InetAddress getIP(){
		return ipofreceiver;
	}
	
	public int update(){
		return update;
	}
	public String sender(){
		return sender;
	}
	
	public String receiver(){
		return receiver;
	}
	///
}

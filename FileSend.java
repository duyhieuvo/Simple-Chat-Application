package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;

public class FileSend {

	static void send(InetAddress receiverip) {
		try { 
			Socket socket= new Socket(receiverip,13001);
			System.out.println("Connecting");
	    	PrintWriter output = new PrintWriter(socket.getOutputStream(), true);


			JFileChooser chooser = new JFileChooser();
		    int returnVal = chooser.showOpenDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		    	
				output.println(chooser.getSelectedFile().getName());
				
		    	File filepath=new File(chooser.getSelectedFile().getAbsolutePath());
				OutputStream out =  socket.getOutputStream();
				FileInputStream in = new FileInputStream(filepath);
				String response=input.readLine();
				if (response.equals("Ready")){
				  byte[] buffer = new byte[1024];
		            int count;
		            
		            while((count = in.read(buffer)) >= 0){
		                out.write(buffer, 0, count);
		            }
		            out.flush();
		            
		            if(in != null){ in.close(); }
            		if(out != null){ out.close(); }
            		if(socket != null){ socket.close(); }
				}
				else if (response.equals("Stop")) {
					in.close();
					out.close();
					socket.close();
				}
				
		    } else {
		    	
		    	//Send to reciver the message that file send completed/abort the send file process
		    	output.println("Cancel");
		    	socket.close();
		    }
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}

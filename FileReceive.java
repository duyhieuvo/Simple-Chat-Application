package Client;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFileChooser;

public class FileReceive {

	static void receive() {
		try {
			ServerSocket server = new ServerSocket(13001);
			System.out.println("Create server socket on the client successfully");
			JFileChooser chooser = new JFileChooser();
			Socket socket=server.accept();
			System.out.println("Connection established successfully");
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String name=br.readLine();
			if (name.equals("Cancel")){ server.close();   }
			else{ 
				String nameEnding[] = name.split("\\.");
				chooser.setSelectedFile(new File(name));
			    int returnVal = chooser.showSaveDialog(new Frame());
			    String saveto=chooser.getCurrentDirectory().toString();
	            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			    			    
			    if(returnVal == JFileChooser.APPROVE_OPTION && saveto != null) {
			    	
			    	name = chooser.getSelectedFile().getName();
			    	if(!name.contains(".")){
			    		name=name+"."+nameEnding[1];
			    	}
			    	
			    	File f=new File(saveto+"\\"+name);
			    	String nameParts[] = name.split("\\.");
			    	int file_order=1;
			    	while ((f.exists()) && (file_order<1000)){
			    		String tt="("+file_order+").";
			    		file_order++;
			    		name=nameParts[0]+tt+nameParts[1];
			    		f=new File(saveto+"\\"+name);
			    	}

		            InputStream in = socket.getInputStream();
		            FileOutputStream out = new FileOutputStream(saveto+"\\"+name);
		            
		            
					output.println("Ready");
		            
		            
		            byte[] buffer = new byte[1024];
		            int count;
		            
		            while((count = in.read(buffer)) >= 0){
		                out.write(buffer, 0, count);
		            }
		            
		            out.flush();

		            if(out != null){ out.close(); }
		            if(in != null){ in.close(); }
		         

			    }
			    else{
			    	output.println("Stop");
			    }
			    socket.close();
			    server.close();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
}

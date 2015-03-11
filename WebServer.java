package lab44;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public final class WebServer {

	
	private static final Map<String, String> mimeMap = new HashMap<String, String>() {{
		put("html", "text/html"); put("css", "text/css"); put("js", "application/js");
		put("jpg", "image/jpg"); put("jpeg", "image/jpeg"); put("png", "image/png");
	}};

	
	private final static String FILEPATH = "/Users/asadqureshi/Documents/workspace/lab44/";
	private final static String str = "Local Server v1.1";

	
	private static void headerResp(String code, String mime, int length, DataOutputStream output) throws Exception {
		System.out.println(" (" + code + ") ");
		output.writeBytes("HTTP/1.1 " + code + " OK\r\n");
		output.writeBytes("Content-Type: " + mimeMap.get(mime) + "\r\n");
		output.writeBytes("Content-Length: " + length + "\r\n"); 
		output.writeBytes(str);
		output.writeBytes("\r\n\r\n");
	}

	private static void content(String inString, DataOutputStream out) throws Exception {
		String method = inString.substring(0, inString.indexOf("/")-1);
		String file = inString.substring(inString.indexOf("/")+1, inString.lastIndexOf("/")-5);
		// Set default file to index.html
		if(file.equals(""))
			file = "index.html";	
		
		String mime = file.substring(file.indexOf(".")+1);		

		if(file. contains(";") || file.contains("*"))	{
			System.out.println(" (File contains potenitially bad string)");
			return;
		}

		
		if(method.equals("GET")) {
			try {
				// Open file
				byte[] fileBytes = null;
				InputStream input = new FileInputStream(FILEPATH+file);
				fileBytes = new byte[input.available()];
				input.read(fileBytes);
				headerResp("200", mime, fileBytes.length, out);
				out.write(fileBytes);
			
			} catch(FileNotFoundException e) {
				// Try to use 404.html
				try {
					byte[] fileBytes = null;
					InputStream input = new FileInputStream(FILEPATH+"404.html");
					fileBytes = new byte[input.available()];
					input.read(fileBytes);
					headerResp("404", "html", fileBytes.length, out);
					out.write(fileBytes);
				} catch(FileNotFoundException e2) {
					String responseString = "404 File Not Found";
					headerResp("404", "html", responseString.length(), out);
					out.write(responseString.getBytes());
				}
			}
			
			
			
		} else if(method.equals("POST")) {
			try{Socket connection = null;
			InputStream is = connection.getInputStream();
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		      String line;
		      StringBuffer response = new StringBuffer(); 
		      while((line = rd.readLine()) != null) {
		        response.append(line);
		        response.append('\r');
		      }
		      
		      rd.close();
		      out.writeChars(response.toString());
		      
			}catch(FileNotFoundException e2) {
				
				
					String responseString = "404 File Not Found";
					headerResp("404", "html", responseString.length(), out);
					out.write(responseString.getBytes());
					
			}
		      
		} else if(method.equals("HEAD")) {
			headerResp("200", "html", 0, out);
		} else {
			headerResp("501", "html", 0, out);
		}
	}

	public static class runn implements Runnable {

		protected Socket socket = null;

		BufferedReader in;
		DataOutputStream out;
		String inString;

		public runn(Socket connectionSocket) throws Exception {
			this.socket = connectionSocket;
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.out = new DataOutputStream(this.socket.getOutputStream());

			this.inString = this.in.readLine();

			Calendar calcu = Calendar.getInstance();
			calcu.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String time = "[" + sdf.format(calcu.getTime()) + "] ";
			System.out.print(time + this.socket.getInetAddress().toString() + " " + this.inString);			
		}

		public void run() {
			try{
				if(this.inString != null)
					content(this.inString, this.out);

				this.out.flush();
				this.out.close();
				this.in.close();

			} catch (Exception e) { 
				System.out.println("flushing and close");				
			}
		}
	}

	
}

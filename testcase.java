package lab44;

import java.net.ServerSocket;
import java.net.Socket;

import lab44.WebServer.runn;

public class testcase {

	public static void main(String argv[]) throws Exception {
		ServerSocket serverSocket = new ServerSocket(5555);

		for(;;) {
			Socket connectionSocket = serverSocket.accept();
		
			new Thread(new runn(connectionSocket)).start();	
		}
	}
}

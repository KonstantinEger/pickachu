package pickachu.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

import pickachu.components.DataProvider;


public class Webserver {
	
	public static final int port = 8080;
	private final ServerSocket socket;
	private final Worker worker;
	private boolean running;
	private static Webserver instance;

	private Webserver() throws IOException{
		socket = new ServerSocket(port);
		worker = new Worker();
	}
	

    public static Webserver getInstance() throws IOException {
    	if (instance == null) {
    		instance = new Webserver();
    	}
    	return instance;
    }
    
    public void host() {
    	running = true;
		worker.start();
    }
    
    public void kill() {
    	running = false;
    }
    
    private class Worker extends Thread {
    	    	
    	
    	@Override
    	public void run() {
    		while (running) {
    			try {
    				// read request
					Socket client =  socket.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String firstLine =  reader.readLine();
					String requestedResource = firstLine.split(" ")[1];
					
					// write response
					Response response = new Response(requestedResource);
					BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					writer.write(response.asString());
					writer.flush();
					
					// close resources
					writer.close();
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    	
	class Response {
		
		public String OK = "HTTP/1.1 200 OK \r\n"; 
		public String Error404 = "HTTP/1.1 404 Not Found \r\n";
		private StringBuilder requestBuilder = new StringBuilder();
		
		public Response(String requestedFileName) {
			byte[] encodedBytes;
			String message = "";
			try {
				encodedBytes = Files.readAllBytes(DataProvider.getFileSystem().getPath(requestedFileName));
				message = new String(encodedBytes);
				requestBuilder.append(OK);
			} catch (IOException e) {
				requestBuilder.append(Error404);
				e.printStackTrace();
			}
			requestBuilder.append(getContentLengthFor(message));
			requestBuilder.append(getContentTypeFor(requestedFileName));
			requestBuilder.append(message);
		}
	    		
		
		public String getContentLengthFor(String message) {
			return "Content-Length: " + message.length() + "\r\n";
		}
		
		
		private String getContentTypeFor(String file) {
			if (file.endsWith(".js")) {
				return "Content-Type: text/javascript \r\n\r\n";
			} else if (file.endsWith(".html")) {
				return "Content-Type: text/html \r\n\r\n";
			}
			return "Content-Type: text/plain \r\n\r\n";
		}
		
		public String asString() {
			return requestBuilder.toString();
		}
	}
}
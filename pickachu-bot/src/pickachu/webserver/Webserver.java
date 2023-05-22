package pickachu.webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


import pickachu.bot.PickachuBot;


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
					Socket client =  socket.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String firstLine =  reader.readLine();
					String requestedResource = firstLine.split(" ")[1];
					
					URL resourceUrl = PickachuBot.class.getResource(requestedResource);
					byte[] encodedBytes = Files.readAllBytes(Paths.get(resourceUrl.toURI()));
					
					BufferedWriter writer =  new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					writer.write(new String(encodedBytes));
				} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
}
package pickachu.components.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import pickachu.components.ComponentInitializationError;

/**
 * Contains all the logic necessary to send/receive tcp messages that arrive via the enclosed Socket Object
 * It offers a "comfortable" abstracted way of sending/receiving messages.
 */
public class CommunicationUnit {
	
	public static String address = "";
	public static int port = 123;
	
	
	private final Socket socket;
	private MessageHandler handler;
	private final Receiver receiver;
	BufferedWriter sender;
	
	public CommunicationUnit() {
		try {
			this.socket = new Socket(address, port);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ComponentInitializationError("Socket connection could not be established");
		}
		
		try {
			this.receiver = new Receiver();
			this.receiver.start();
		} catch (IOException e) {
			throw new ComponentInitializationError("Could not retrieve InputStream from socket.");
		}
		
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			sender = new BufferedWriter(outputStreamWriter);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ComponentInitializationError("Could not retrieve OutputStream from socket.");

		}
	}
	
	
	/**
	 * Sets the handler responsible for handling incoming messages
	 */
	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * Sends a message to the server.
	 * @return true if a message was sent successfully
	 */
	public boolean sendMessage(Message message) {
		try {
			sender.write(message.parseMessageToString()); //todo does writer write \n at the end of the msg? (we need that)
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

	/**
	 * Responsible for waiting on incoming messages, parsing them into a Message Object and triggering the handle function
	 * of our {@link MessageHandler}. This listens until the underlying InputStream is stopped.
	 */
	class Receiver extends Thread{
		
		private BufferedReader reader;
		
		public Receiver() throws IOException {
			InputStream input = socket.getInputStream();
			InputStreamReader streamReader = new InputStreamReader(input);
			reader = new BufferedReader(streamReader);
		}
		
		@Override
		public void run() {
			String clientInput = "";
			while (clientInput != null) {	
				try {
					clientInput = reader.readLine();
					Message response = handler.handle(Message.parseStringToMessage(clientInput));
					if (response != null) {
						sendMessage(response);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}	
		}
	}
}

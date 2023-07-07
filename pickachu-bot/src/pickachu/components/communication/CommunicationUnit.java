package pickachu.components.communication;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import pickachu.components.Disposable;


/**
 * Contains all the logic necessary to send/receive tcp messages that arrive via the enclosed Socket Object
 * It offers a "comfortable" abstracted way of sending/receiving messages.
 */
public class CommunicationUnit extends WebSocketServer implements Disposable{
	
	public static InetSocketAddress address = new InetSocketAddress(8081);
	private MessageHandler handler;
	
	public CommunicationUnit() {
		super(address);
		setReuseAddr(true);
	}
	
	/**
	 * Sends a message to a client. TODO this is currently implemented as broadcast.
	 * Because the applications behavior with multiple clients is currently undefined.
	 */
	public void broadcast(Message message) {
		broadcast(message.parseMessageToString(), getConnections());
	}
	
	/**
	 * Sets the handler responsible for handling incoming messages
	 */
	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}
	

	@Override
	public void onClose(WebSocket arg0, int arg1, String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onMessage(WebSocket sender, String message) {
		 System.out.println(message);
		 Message response = handler.handle(Message.parseStringToMessage(message));
		 if (response != null && sender.isOpen()) {
			 sender.send(response.parseMessageToString());
		 }
	}


	@Override
	public void onOpen(WebSocket arg0, ClientHandshake arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose() {
		try {
			this.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

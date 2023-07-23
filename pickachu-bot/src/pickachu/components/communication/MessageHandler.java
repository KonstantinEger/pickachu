package pickachu.components.communication;

/**
 * Handler responsible for handling incoming tcp messages
 */
public interface MessageHandler {
	
	/**
	 * @param message : the incoming message
	 * @return response : optionally returns a response message that will be sent back to the server
	 */
	public Message handle(Message message); //todo return type annotation nullable
}

package pickachu.components.communication;

import java.util.Arrays;


/**
 * Models a message sent via tcp
 */
public class Message {
	public static String separator = ":";
	public OpCode opCode;
	public String[] content;
	
	/**
	 * @param opCode : The {@link OpCode} of the message
	 * @param content : The content of the message, 
	 * it will later be concatenated to a single string with the initial entries separated by {@link separator}
	 * 
	 * Example:
	 * OpCode=Something, contents=["1234", "45.67", "uhu"] -> "Something:1234:45.67:uhu"
	 */
	public Message(OpCode opCode, String[] content) {
		this.opCode = opCode;
		this.content = content;
	}
	
	public static Message parseStringToMessage(String message) {
		String[] array = message.split(separator);
		String[] slice = Arrays.copyOfRange(array,1, array.length);
		return new Message(OpCode.stringLookup.getOrDefault(array[0], OpCode.NoOp), slice);
	}
	
	public String parseMessageToString() {
		return opCode.name() + separator + String.join(separator, content);
	}
}

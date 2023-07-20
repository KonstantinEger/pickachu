package pickachu.components.communication;

import java.util.HashMap;
import java.util.Map;

/**
 * OpCode written at the start of every message to make it identifiable
 */
public enum OpCode {
	NoOp, //not viable
	Forward, // rotations
	Left, // rotations
	Right, // rotations
	PickUp, // tells the robot to pick the object up
	Drop, // tells the robot to drop the object
	Gyro, // tells the server the robots rotation 
	Ack // Acknowledges a command after its execution Ack:<Command> -> Ack:Forward:100
	;
	
	public static Map <String, OpCode> stringLookup = new HashMap<>();
	
	static {
		for (OpCode opCode : OpCode.values()) {
			stringLookup.put(opCode.name(), opCode);
		}
	}
} 

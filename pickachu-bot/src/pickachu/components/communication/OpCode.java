package pickachu.components.communication;

import java.util.HashMap;
import java.util.Map;

/**
 * OpCode written at the start of every message to make it identifable
 */
public enum OpCode {
	NoOp, //not viable
	Stop,
	Forward, // rotations
	Left, // rotations
	Right, // rotations
	PickUp, // tells the robot to pick the object up
	Drop, // tells the robot to drop the object
	;
	
	public static Map <String, OpCode> stringLookup = new HashMap<>();
	
	static {
		for (OpCode opCode : OpCode.values()) {
			stringLookup.put(opCode.name(), opCode);
		}
	}
} 

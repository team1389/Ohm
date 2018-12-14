package com.team1389.hardware.registry;
/**
 * this exception is thrown by the registry when it attempts to initialize a hardware object on a taken port 
 * @author amind
 *
 */
public class PortTakenException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 * @param msg the message to print along with the exception error
	 */
	public PortTakenException(String msg) {
		super(msg);
	}
}

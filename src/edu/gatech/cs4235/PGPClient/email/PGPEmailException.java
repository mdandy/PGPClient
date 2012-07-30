package edu.gatech.cs4235.PGPClient.email;

/**
 * An exception for PGPEmail
 * @author mdandy
 */
public class PGPEmailException extends Exception
{
	/**
	 * Constructor
	 * @param message the exception message
	 */
	public PGPEmailException(String message)
	{
		super(message);
	}
}

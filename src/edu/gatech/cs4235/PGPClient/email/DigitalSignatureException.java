package edu.gatech.cs4235.PGPClient.email;

/**
 * This class represents an Exception that being thrown if the digital
 * signature does not matched with payload.
 * @author mdandy
 */
public class DigitalSignatureException extends Exception
{
	public DigitalSignatureException()
	{
		super ("Fail to authenticate digital signature.");
	}
}

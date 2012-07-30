package edu.gatech.cs4235.PGPClient.email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.gatech.cs4235.PGPClient.DAL.FileIO;
import edu.gatech.cs4235.PGPClient.DAL.ObjectRepository;
import edu.gatech.cs4235.PGPClient.DAL.Preferences;
import edu.gatech.cs4235.PGPClient.cryptex.CryptoEngine;

/**
 * This class represents a PGP email. It's a wrapper of javax.mail.Message
 * @author mdandy
 */
public class PGPEmail
{
	/* PGP email protocol:
	 * PGP combines symmetric-key encryption and public-key encryption. 
	 * The message is encrypted using a symmetric encryption algorithm, 
	 * which requires a symmetric key. Each symmetric key is used only 
	 * once and is also called a session key. The session key is 
	 * protected by encrypting it with the receiver's public key thus 
	 * ensuring that only the receiver can decrypt the session key. The 
	 * encrypted message along with the encrypted session key is sent 
	 * to the receiver. 
	 */

	private final static String BEGIN_EMAIL = "-----BEGIN PGP EMAIL-----";
	private final static String END_EMAIL = "-----END PGP EMAIL-----";
	private final static String BEGIN_KEY = "\n-----BEGIN PGP KEY-----\n";
	private final static String END_KEY = "\n-----END PGP KEY-----\n";
	private final static String BEGIN_MESSAGE = "\n-----BEGIN PGP SIGNED MESSAGE-----\n";
	private final static String END_MESSAGE = "\n-----END PGP SIGNED MESSAGE-----\n";
	private final static String BEGIN_SIGNATURE = "\n-----BEGIN PGP SIGNATURE-----\n";
	private final static String END_SIGNATURE = "\n-----END PGP SIGNATURE-----\n";

	public String to;
	public String subject;
	public String from;
	public String payload;
	public Message message;
	public boolean isAunthentic;
	
	/**
	 * An enumeration of email type
	 * @author mdandy
	 */
	public enum Type
	{
		NEW,
		REPLY,
		FORWARD
	}

	/**
	 * Constructor. Called by the sender.
	 * @param to the recipient
	 * @param subject the subject
	 * @param from the sender
	 * @param payload the email body
	 */
	public PGPEmail(String to, String subject, String from, String payload)
	{
		this.to = to;
		this.subject = subject;
		this.from = from;
		this.payload = payload;
		this.message = null;
	}

	/**
	 * Set Java message.
	 * @param message Java message
	 */
	public void setMessage(Message message)
	{
		this.message = message;
	}

	/**
	 * Encrypt the message body and sign it.
	 * @return the encrypted message
	 * @throws PGPEmailException 
	 */
	public Message prepare() throws PGPEmailException
	{
		String body = encryptContent(this.payload);

		/* Create Java message */
		Session session = EmailClient.getSMTPSession();
		try 
		{
			message = new MimeMessage(session);
			message.setSubject(subject);
			message.setContent(body, "text/plain");
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}

		this.isAunthentic = true;
		return message;
	}
	
	/**
	 * Encrypt email content.
	 * @param content the email content
	 * @return the encrypted email content
	 * @throws PGPEmailException 
	 */
	public String encryptContent(String payload) throws PGPEmailException
	{
		String result = "";
		
		ObjectRepository or = ObjectRepository.getInstance();
		Preferences pref = or.getPreferences();
		String passphrase = pref.getPassphrase();
		
		if (passphrase.isEmpty())
			throw new PGPEmailException("Passphrase has not been initialized.");
		
		/* Encrypt the message */
		CryptoEngine ce = CryptoEngine.getInstance();
		String[] cipher = ce.encrypt(passphrase, payload);
		
		/* Encrypt the key */
		String publicKeyRaw = or.getPublicKey(this.to);
		if (publicKeyRaw == null)
			throw new PGPEmailException("Unable to parse public key.");
		RSAPublicKey publicKey = ce.parsePublicKey(publicKeyRaw);
		String cipherKey = ce.encrypt(publicKey, cipher[0]);

		/* Sign the message */
		String filepath = pref.getKeyDirectory() + File.separator + pref.getPrivateKeyFilePath();
		String privateKeyRaw = FileIO.read(filepath);
		if (privateKeyRaw == null)
			throw new PGPEmailException("Unable to parse private key.");
		RSAPrivateKey privateKey = ce.parsePrivateKey(privateKeyRaw);
		String signature = ce.sign(privateKey, payload);
		
		result = PGPEmail.BEGIN_EMAIL;

		result += PGPEmail.BEGIN_KEY;
		result += cipherKey;
		result += PGPEmail.END_KEY;

		result += PGPEmail.BEGIN_MESSAGE;
		result += cipher[1];
		result += PGPEmail.END_MESSAGE;

		result += PGPEmail.BEGIN_SIGNATURE;
		result += signature;
		result += PGPEmail.END_SIGNATURE;

		result += PGPEmail.END_EMAIL;
		
		return result;
	}

	/**
	 * Parse an email to PGP email.
	 * @param message the email to be parsed
	 * @return true on successful or false otherwise
	 */
	public static PGPEmail parse(Message message)
	{
		try 
		{
			String mTo = InternetAddress.toString(message.getAllRecipients());
			String mSubject = message.getSubject();
			String mFrom = InternetAddress.toString(message.getFrom());
			String mBody = processBody(message);

			if (mBody.contains(PGPEmail.BEGIN_EMAIL))
			{
				try
				{
					/* Sanitize the input */
					mBody = mBody.replace("\r", "");
					
					/* Extract the payload */
					String key = mBody.substring(mBody.indexOf(PGPEmail.BEGIN_KEY) + PGPEmail.BEGIN_KEY.length(), 
							mBody.indexOf(PGPEmail.END_KEY));
					String payload = mBody.substring(mBody.indexOf(PGPEmail.BEGIN_MESSAGE) + PGPEmail.BEGIN_MESSAGE.length(), 
							mBody.indexOf(PGPEmail.END_MESSAGE));
					String signature = mBody.substring(mBody.indexOf(PGPEmail.BEGIN_SIGNATURE) + PGPEmail.BEGIN_SIGNATURE.length(), 
							mBody.indexOf(PGPEmail.END_SIGNATURE));
					
					CryptoEngine ce = CryptoEngine.getInstance();
					ObjectRepository or = ObjectRepository.getInstance();
					Preferences pref = or.getPreferences();
					
					/* Decrypt the key */
					String filepath = pref.getKeyDirectory() + File.separator + pref.getPrivateKeyFilePath();
					String privateKeyRaw = FileIO.read(filepath);
					if (privateKeyRaw == null)
						throw new PGPEmailException("Unable to parse private key.");
					RSAPrivateKey privateKey = ce.parsePrivateKey(privateKeyRaw);
					String plainKey = ce.decrypt(privateKey, key);;

					/* Decrypt the payload */
					payload = ce.decrypt(plainKey, payload);

					/* Retrieve Public Key */
					String publicKeyRaw = or.getPublicKey(mFrom);
					RSAPublicKey publicKey = ce.parsePublicKey(publicKeyRaw);

					PGPEmail email = new PGPEmail(mTo, mSubject, mFrom, payload);
					email.setMessage(message);
					email.isAunthentic = ce.authenticate(publicKey, signature, payload);
					return email;
				}
				catch (Exception e)
				{
					/* There is something wrong with the format */
					PGPEmail email = new PGPEmail(mTo, mSubject, mFrom, mBody);
					email.setMessage(message);
					email.isAunthentic = true;
					return email;
				}
			}
			else
			{
				/* This is regular email */
				PGPEmail email = new PGPEmail(mTo, mSubject, mFrom, mBody);
				email.setMessage(message);
				email.isAunthentic = true;
				return email;
			}
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Process the message body.
	 * @param message the message body
	 * @return String representation of the message body
	 */
	private static String processBody(Message message)
	{
		return processPart(message, "");
	}

	private static String processPart(Part message, String intermediate)
	{
		try 
		{
			if (message.isMimeType("text/plain"))
			{
				intermediate = (String)message.getContent();
			}
			else if (message.isMimeType("multipart/*"))
			{
				Multipart mp = (Multipart) message.getContent();
				int count = mp.getCount();
				for (int i = 0; i < count; i++)
				{
					intermediate += processPart(mp.getBodyPart(i), intermediate);
				}
			}
			else if (message.isMimeType("message/rfc822"))
			{
				intermediate = "Unsupported Email Type: message/rfc822";
			}
			else
			{
				/*
				 * If we actually want to see the data, and it's not a
				 * MIME type we know, fetch it and check its Java type.
				 */
				Object o = message.getContent();
				if (o instanceof String) 
				{
					intermediate += (String)o;
				} 
				else if (o instanceof InputStream) 
				{
					InputStream is = (InputStream)o;
					int c;
					while ((c = is.read()) != -1)
						intermediate += c;
				} 
				else 
				{
					intermediate = "Unsupported Email Type: Unknown";
				}
			}
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return intermediate;
	}
}

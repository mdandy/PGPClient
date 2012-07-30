package edu.gatech.cs4235.PGPClient.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

import edu.gatech.cs4235.PGPClient.DAL.EmailStore;
import edu.gatech.cs4235.PGPClient.DAL.ObjectRepository;
import edu.gatech.cs4235.PGPClient.DAL.Preferences;

/**
 * This class represent a general implementation of an email client. 
 * POP3 will be used to synchronize with email server. SMTP will be 
 * used to send an email. A good tutorial: 
 * http://java.sun.com/developer/onlineTraining/JavaMail/contents.html
 * @author mdandy
 */
public class EmailClient 
{
	/* A singleton object */
	private static EmailClient emailClient;
	private ObjectRepository or;
	private Preferences pref;
	private EmailStore es;

	private final String outbound = "smtp.gmail.com";
	private final String inbound = "pop.gmail.com";

	/**
	 * Constructor
	 */
	private EmailClient ()
	{
		or = ObjectRepository.getInstance();
		pref = or.getPreferences();
		es = or.getEmailStore();
	}

	/**
	 * Get an instance of email client.
	 * @return an instance of email client
	 */
	public static EmailClient getInstance()
	{
		if (emailClient == null)
			emailClient = new EmailClient ();
		return emailClient;
	}

	/**
	 * Login to email account.
	 * @param username the username of an email account
	 * @param password the password of an email account
	 * @return true on success or false otherwise
	 */
	public boolean login (String username, String password)
	{
		//TODO: Figure out how to authenticate this	
		pref.setUsername(username);
		pref.setPassword(password);
		return true;
	}

	/**
	 * Return the properties of this email client.
	 * @return the properties of this email client
	 * @throws PGPEmailException 
	 */
	public static Session getSMTPSession() throws PGPEmailException
	{
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		try
		{
			Session session = Session.getInstance(props, new Authenticator() 
			{
				@Override
				protected PasswordAuthentication getPasswordAuthentication() 
				{
					ObjectRepository or = ObjectRepository.getInstance();
					Preferences pref = or.getPreferences();
					String username = pref.getUsername();
					String password = pref.getPassword();
					return new PasswordAuthentication(username, password);
				}

			});
			return session;
		}
		catch (Exception e)
		{
			throw new PGPEmailException(e.getMessage());
		}
	}

	/**
	 * Send an email through SMTP. The email will be encrypted and signed.
	 * @param email the email to be sent
	 * @return true on success or false otherwise
	 */
	public boolean send (PGPEmail email)
	{		
		try 
		{			
			Message message = email.prepare();
			Transport.send(message);

			return true;
		} 
		catch (NoSuchProviderException e) 
		{
			e.printStackTrace();
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		} 
		catch (PGPEmailException e) 
		{
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Reply an email. A prefix "RE: " will be added to the 
	 * subject.
	 * @param email the email to be replied
	 * @param reply the content of email
	 * @return true on success or false otherwise
	 */
	public boolean reply(PGPEmail email, String reply)
	{
		/* "RE: " will be added by Java Mail library automatically
		 * upon calling Messaage.reply().
		 */
		
		/* TODO: Figure out how to reply using outbound connection. The
		 * method below will send the message through SMTP server on 
		 * port 25. This will throw Exception if no SMTP server is
		 * installed.
		 */

//		try 
//		{
//			Message message = email.message;
//			MimeMessage replyMessage = (MimeMessage) message.reply(false);
//			replyMessage.setFrom(new InternetAddress(pref.getUsername()));
//			replyMessage.setText(reply);
//			Transport.send(replyMessage);
//
//			return true;
//		} 
//		catch (MessagingException e) 
//		{
//			e.printStackTrace();
//		}

		return false;
	}

	/**
	 * Mark an email to be deleted.
	 * @param email the email to be deleted
	 * @return true on success or false otherwise
	 */
	public boolean delete(PGPEmail email)
	{
		return false;
	}

	/**
	 * Mark an email to be read
	 * @param email the email to be read
	 * @return true on success or false otherwise
	 */
	public boolean markRead(PGPEmail email)
	{
		return false;
	}

	/**
	 * Get all messages in INBOX folder.
	 * @param folder the folder
	 * @return all messages within a folder
	 */
	public PGPEmail[] getMessages()
	{
		PGPEmail[] emails = null;

		try 
		{
			String username = pref.getUsername();
			String password = pref.getPassword();

			Properties props = System.getProperties();
			Session session = Session.getInstance(props, null);
			Store store = session.getStore("pop3s");
			store.connect(inbound, username, password);

			Folder folder = store.getDefaultFolder();
			if (folder != null)
			{
				folder = store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);
				Message[] messages = folder.getMessages();

				FetchProfile fp = new FetchProfile();
				fp.add(FetchProfile.Item.ENVELOPE);
				fp.add(FetchProfile.Item.FLAGS);
				fp.add("X-Mailer");
				folder.fetch(messages, fp);

				int emailCount = EmailStore.MAX_COUNT;
				if (emailCount > folder.getMessageCount())
					emailCount = folder.getMessageCount();

				emails = new PGPEmail[emailCount];
				for (int i = 0; i < emails.length; i++)
				{
					/* Messages come in the reverse chronological order */
					PGPEmail email = PGPEmail.parse(messages[messages.length - 1 - i]);
					emails[i] = email;
				}
				
				/* Store the emails */
				es.add(emails);
			}

			folder.close(false);
			store.close();
			or.saveEmailStore(es);
		} 
		catch (NoSuchProviderException e) 
		{
			e.printStackTrace();
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}

		return emails;
	}
}

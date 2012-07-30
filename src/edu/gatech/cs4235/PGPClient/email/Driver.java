package edu.gatech.cs4235.PGPClient.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bouncycastle.jce.provider.asymmetric.EC;

import edu.gatech.cs4235.PGPClient.email.EmailClient;

public class Driver {
	
	public static void main(String args[]){
//		String host = "smtp.gmail.com";
//		int port = 587;
//		String username = "smarmius";
//		String password = "31415926";
//		
//		Properties props = new Properties();
//		props.put("mail.smtp.auth", true);
//		props.put("mail.smtp.starttls.enable", true);
//		
//		Session session = Session.getInstance(props);
//		
//		try{
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress("smarmius@gmail.com"));
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("finalproject4235@gmail.com"));
//			message.setSubject("TEST SUBJECT");
//			message.setText("Hello, world!");
//			
//			Transport transport = session.getTransport("smtp");
//			transport.connect(host, port, username, password);
//			Transport.send(message);
//		} catch (MessagingException e){
//			throw new RuntimeException(e);
//		}
//		
		
		String host = "smtp.gmail.com";
		int port = 465;
		String user = "finalproject4235";
		String password = "cs4235password";
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", host);
		props.put("mail.smtp.auth", "true");
		
		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);
		try{
			Transport transport = session.getTransport();
			
			MimeMessage message = new MimeMessage(session);
			message.setSubject("Test Subject");
			message.setContent("This is a test", "text/plain");
			
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("finalproject4235@gmail.com"));
			
			transport.connect(host, port, user, password);
			transport.sendMessage(message,  message.getRecipients(Message.RecipientType.TO));
			
			transport.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}

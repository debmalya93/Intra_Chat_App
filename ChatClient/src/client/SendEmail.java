package client;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendEmail {
	
	static void sendOTP(String recipientMail,final String OTP)
	   {    
	      final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	      // Get a Properties object
	      Properties props = System.getProperties();
	      props.setProperty("mail.smtp.host", "smtp.gmail.com");
	      props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
	      props.setProperty("mail.smtp.socketFactory.fallback", "false");
	      props.setProperty("mail.smtp.port", "465");
	      props.setProperty("mail.smtp.socketFactory.port", "465");
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.debug", "true");
	      props.put("mail.store.protocol", "pop3");
	      props.put("mail.transport.protocol", "smtp");
	      final String username = "intrachat0@gmail.com";//
	      final String password = "adminintrachat";
	      try{
	      Session session = Session.getDefaultInstance(props, 
	                           new Authenticator(){
	                              protected PasswordAuthentication getPasswordAuthentication() {
	                                 return new PasswordAuthentication(username, password);
	                              }});

	    // -- Create a new message --
	      Message msg = new MimeMessage(session);

	   // -- Set the FROM and TO fields --
	      msg.setFrom(new InternetAddress("intrachat0@gmail.com"));
	      msg.setRecipients(Message.RecipientType.TO, 
	                       InternetAddress.parse(recipientMail,false));
	      msg.setSubject("OTP");
	      msg.setText("Thank you for registering in Intra-Chat Application.\n\nYou're just one step away from completing the process.\n\nYou need to enter "+OTP+" as your OTP in order to get registered successfully.\n\nThis is just one time password so no need to keep it saved.\nHope to see you soon.\n\n\nWith regards\n  -admin");
	      msg.setSentDate(new Date());
	      Transport.send(msg);
	      System.out.println("Message sent.");
	   }catch (MessagingException e){ 
		   System.out.println("Error cause: " + e);}
	      
	   }
	   
	

}

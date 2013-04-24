package fr.istic.kercar.sendmailwebcam.webcamsender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws Exception {
		// Attach File for mail
		List<String> command = new java.util.ArrayList<String>();
		// fswebcam -r 960x720 -d /dev/video0 webcam.jpg
		command.add("fswebcam");
		command.add("-r");
		command.add("960x720");
		command.add("-d");
		command.add("/dev/video0");
		command.add("webcam.jpg");

		ProcessBuilder probuilder = new ProcessBuilder(command);

		// You can set up your work directory
		// probuilder.directory(new File("/tmp"));

		try {
			Process process = probuilder.start();
			StringBuilder out1 = new StringBuilder();

			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			line = br.readLine();

			while (line != null) {
				out1.append(line);
				line = br.readLine();
			}

			// Wait to get exit value
			int exitValue = process.waitFor();
			// System.out.println("\n\nExit output is " + out1.toString);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Send an email
		String host = "smtp.gmail.com";// host name
		String from = "olivier.barais@gmail.com";// sender id
		String to = "barais@irisa.fr";// reciever id
		String pass = "TOCHANGE";// sender's password
		String fileAttachment = "webcam.jpg";// file name for attachment
		// system properties
		Properties prop = System.getProperties();
		// Setup mail server properties
		prop.put("mail.smtp.gmail", host);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", host);
		prop.put("mail.smtp.user", from);
		prop.put("mail.smtp.password", pass);
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.auth", "true");
		// session
		Session session = Session.getInstance(prop, null);
		// Define message
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject("KerCar view");
		// create the message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		// message body
		messageBodyPart.setText("The Kercar view image");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		// attachment
		messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(fileAttachment);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileAttachment);
		multipart.addBodyPart(messageBodyPart);
		message.setContent(multipart);
		// send message to reciever
		Transport transport = session.getTransport("smtp");
		transport.connect(host, from, pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}
}

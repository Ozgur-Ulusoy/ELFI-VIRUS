package com.isee.elfi;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;


public class EmailSender {

    public static void sendEmailWithInMemoryAttachment(String toEmail, String subject, String bodyText, byte[] zipData, String zipFileName) {

        Dotenv dotenv = Dotenv.load();

        // Access variables
        String username = dotenv.get("mail_username");
        String password = dotenv.get("mail_password");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create the message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); // Sender address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Recipient address
            message.setSubject(subject); // Subject

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Add the text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText); // Email body text
            multipart.addBodyPart(textPart);

            // Add the attachment part
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.setFileName(zipFileName); // Set the attachment name
            attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(zipData, "application/zip"))); // Add the ZIP data as an attachment
            multipart.addBodyPart(attachmentPart);

            // Set the multipart content to the message
            message.setContent(multipart);

            // Send the email
            Transport.send(message);

            System.out.println("Email sent successfully with the ZIP file attached.");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}